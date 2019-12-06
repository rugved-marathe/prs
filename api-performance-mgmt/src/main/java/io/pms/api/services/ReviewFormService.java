package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.pms.api.common.CommonUtils;
import io.pms.api.common.Constants;
import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import io.pms.api.exception.Errors;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.UnauthorizedException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.CommonTag;
import io.pms.api.model.Employee;
import io.pms.api.model.Goal;
import io.pms.api.model.ReviewForm;
import io.pms.api.model.RoleTag;
import io.pms.api.model.TrainingCertification;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.repositories.GoalRepository;
import io.pms.api.repositories.ReviewFormRepository;
import io.pms.api.repositories.RoleTagRepository;
import io.pms.api.vo.CommonTagVO;
import io.pms.api.vo.EmailResponse;
import io.pms.api.vo.FinalRatingReportVO;
import io.pms.api.vo.FormNotificationVO;
import io.pms.api.vo.GenericResponse;
import io.pms.api.vo.NotificationVO;
import io.pms.api.vo.ReviewFormListVO;
import io.pms.api.vo.ReviewFormVO;
import io.pms.api.vo.TrainingCertificationVO;

/**
 * @author shashikumar.t
 *
 */
@Service
public class ReviewFormService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewFormService.class);

	@Autowired
	private ReviewFormRepository reviewFormRepository;

	@Autowired
	private GoalRepository goalRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private RoleTagRepository roleTagRepository;

	@Autowired
	private NotificationService notificationService;

	@Value("${hrEmailGroup}")
	private String hrEmailGroup;

	@Value("${finalRatingReportDir}")
	private String finalRatingReportDir;

	@Transactional(rollbackFor = { Exception.class })
	public GenericResponse swapReviewCycle(List<String> employeeIds) {
		GenericResponse response = new GenericResponse();
		try {
			employeeIds.stream().forEach(employeeId -> {
				Employee employee = employeeRepository.findByEmpId(employeeId);
				if (null != employee) {
					if (PerformanceCycle.APRIL.equals(employee.getPerformanceCycle()))
						employee.setPerformanceCycle(PerformanceCycle.OCTOBER);
					else
						employee.setPerformanceCycle(PerformanceCycle.APRIL);
					employeeRepository.save(employee);
				} else {
					LOGGER.debug("emp with document id : {} not found.", employeeId);
				}
			});
			response.setMessage("Performance Cycle changed for employees");
		} catch (Exception e) {
			LOGGER.error("error processing the request {} Cause {}", e.getMessage(), e.getCause());
			response.setMessage("Failed to process the request.");
			throw new PMSAppException(listOfErrors("Failed to process the request."));
		}
		return response;
	}

	public ReviewFormVO createReviewForm(ReviewFormVO reviewFormVO) {
		LOGGER.debug("Creating review form {}", reviewFormVO);
		if (null != reviewFormVO) {
			List<Errors> errorList = reviewFormVO.validate();
			if (!errorList.isEmpty()) {
				LOGGER.error("Error occurred while validating ReviewFormVO");
				throw new ValidationException(errorList);
			}

			ReviewForm reviewForm = new ReviewForm();
			CommonUtils.copyNonNullProperties(reviewFormVO, reviewForm);
			reviewForm = reviewFormRepository.save(reviewForm);
			LOGGER.debug("Added review form {}", reviewForm);
			CommonUtils.copyNonNullProperties(reviewForm, reviewFormVO);
			return reviewFormVO;
		} else {
			throw new ValidationException(listOfErrors("ReviewForm is empty"));
		}

	}

	public ReviewFormVO updateReviewForm(ReviewFormVO reviewFormVO) {
		LOGGER.debug("Updating review form {}", reviewFormVO);
		if (null != reviewFormVO) {
			List<Errors> errorList = reviewFormVO.validate();
			if (!errorList.isEmpty()) {
				LOGGER.debug("Error occurred while validating ReviewFormVO");
				throw new ValidationException(errorList);
			}

			ReviewForm reviewForm = reviewFormRepository.findOne(reviewFormVO.getId());
			if (null != reviewForm) {
				CommonUtils.copyNonNullProperties(reviewFormVO, reviewForm);

				reviewForm.setRoleResponsibilities(convert(reviewFormVO.getRoleResponsibilities()));
				reviewForm.setCommonCompetencies(convert(reviewFormVO.getCommonCompetencies()));
				reviewForm.setBusinessPhilosophies(convert(reviewFormVO.getBusinessPhilosophies()));

				if (reviewForm.getFormStatus().equals(FormStatus.FORM_RECEIVED)
						|| reviewForm.getFormStatus().equals(FormStatus.APPROVED)) {

					List<Integer> goalRating = new ArrayList<>();
					List<Integer> roleSpecificRating = new ArrayList<>();
					List<Integer> commomCompetencyRating = new ArrayList<>();

					List<Goal> goals = goalRepository.findAllByEmpIdAndPerformanceCycleAndYearAndStatus(
							reviewForm.getEmpId(), reviewForm.getCycle().previous().toString(),
							(reviewForm.getCycle().ordinal() < reviewForm.getCycle().previous().ordinal())
									? (reviewForm.getYear() - 1)
									: reviewForm.getYear(),
							Status.ACTIVE);
					for (Goal goal : goals) {
						goalRating.add(goal.getRating());
					}

					for (CommonTag roleSpecific : reviewForm.getRoleResponsibilities()) {
						roleSpecificRating.add(roleSpecific.getRating());
					}

					for (CommonTag commomCompetency : reviewForm.getCommonCompetencies()) {
						commomCompetencyRating.add(commomCompetency.getRating());
					}

					Map<String, Double> averageRatings = average(roleSpecificRating, goalRating,
							commomCompetencyRating);
					reviewForm.setAvgRoleSpecificRating(averageRatings.get("averageRoleSpecificRating"));
					reviewForm.setAvgGoalRating(averageRatings.get("averageGoalRatings"));
					reviewForm.setAvgCommonCompetencyRating(averageRatings.get("averageCommonCompetencyRatings"));
					reviewForm.setRating(averageRatings.get("finalRatings"));

				}
				reviewForm = reviewFormRepository.save(reviewForm);
				LOGGER.debug("updated review form {}", reviewForm);
				CommonUtils.copyNonNullProperties(reviewForm, reviewFormVO);
			} else {
				LOGGER.debug("ReviewForm does not exists {}", reviewFormVO);
				throw new NotFoundException(listOfErrors("ReviewForm does not exists for given id"));
			}
			return reviewFormVO;
		} else {
			throw new ValidationException(listOfErrors("ReviewForm is empty"));
		}
	}

	private List<CommonTag> convert(List<CommonTagVO> commonTagVos) {
		if (null != commonTagVos) {
			List<CommonTag> commonTags = new ArrayList<>();
			for (CommonTagVO commonTagVO : commonTagVos) {
				CommonTag commonTag = new CommonTag();
				CommonUtils.copyNonNullProperties(commonTagVO, commonTag);
				commonTags.add(commonTag);
			}
			return commonTags;
		}
		return null;
	}

	public ReviewFormListVO getReviewForm(String employeeId, String performanceCycle, String year, Role role) {
		ReviewFormListVO reviewFormListVO = new ReviewFormListVO();
		List<ReviewForm> reviewFormList = null;
		if (null == performanceCycle) {
			LOGGER.info("Fetch all review forms for the employee.");
			reviewFormList = reviewFormRepository.findByEmpId(employeeId);
		} else {
			LOGGER.info("Fetch specific review form for the employee by review cycle and year.");
			reviewFormList = reviewFormRepository.findByEmpIdAndCycleAndYear(employeeId, performanceCycle,
					Integer.valueOf(year));
		}

		if (null != reviewFormList && !reviewFormList.isEmpty()) {
			List<ReviewFormVO> formListVO = reviewFormList.stream().map(form -> {
				ReviewFormVO formVO = new ReviewFormVO();
				CommonUtils.copyNonNullProperties(form, formVO);
				List<CommonTag> commonCompetencyList = form.getCommonCompetencies();
				List<CommonTagVO> commonCompetencyVOList = new ArrayList<>();
				commonCompetencyList.forEach(competency -> {
					CommonTagVO tagVO = new CommonTagVO();
					BeanUtils.copyProperties(competency, tagVO);
					commonCompetencyVOList.add(tagVO);
				});
				formVO.setCommonCompetencies(commonCompetencyVOList);
				List<CommonTag> roleResponsibilitiesList = form.getRoleResponsibilities();
				List<CommonTagVO> roleResponsibilitiesVOList = new ArrayList<>();
				roleResponsibilitiesList.forEach(competency -> {
					CommonTagVO tagVO = new CommonTagVO();
					BeanUtils.copyProperties(competency, tagVO);
					roleResponsibilitiesVOList.add(tagVO);
				});
				formVO.setRoleResponsibilities(roleResponsibilitiesVOList);
				List<CommonTag> businessPhilosophiesList = form.getBusinessPhilosophies();
				List<CommonTagVO> businessPhilosophiesVOList = new ArrayList<>();
				businessPhilosophiesList.forEach(competency -> {
					CommonTagVO tagVO = new CommonTagVO();
					BeanUtils.copyProperties(competency, tagVO);
					businessPhilosophiesVOList.add(tagVO);
				});
				formVO.setBusinessPhilosophies(businessPhilosophiesVOList);
				List<TrainingCertification> trainingCertificationList = form.getTrainingCertification();
				List<TrainingCertificationVO> trainingCertificationVOList = new ArrayList<>();
				trainingCertificationList.forEach(competency -> {
					TrainingCertificationVO tagVO = new TrainingCertificationVO();
					BeanUtils.copyProperties(competency, tagVO);
					trainingCertificationVOList.add(tagVO);
				});
				formVO.setTrainingCertification(trainingCertificationVOList);
				if (Role.EMPLOYEE.equals(role)) {
					if (!FormStatus.FINISHED.equals(form.getFormStatus()) || !FormStatus.COMPLETED.equals(form.getFormStatus())) {
						formVO.getRoleResponsibilities().forEach(commonTag -> {
							commonTag.setReviewerComments("");
							commonTag.setRating(0);
						});
						formVO.getCommonCompetencies().forEach(commonTag -> {
							commonTag.setReviewerComments("");
							commonTag.setRating(0);
						});
						formVO.getBusinessPhilosophies().forEach(commonTag -> {
							commonTag.setReviewerComments("");
						});
						formVO.getTrainingCertification().forEach(trainingCertificationVo -> {
							trainingCertificationVo.setReviewerComments("");
						});
						formVO.setAvgGoalRating(0.0);
						formVO.setRating(0.0);
						formVO.setAvgRoleSpecificRating(0.0);
						formVO.setAvgCommonCompetencyRating(0.0);
					}
				}
				return formVO;
			}).collect(Collectors.toList());

			reviewFormListVO.setReviewForms(formListVO);
		} else {
			LOGGER.debug("New employee found, sent 404 response.");
			throw new NotFoundException(listOfErrors("No review forms generated yet for employee : " + employeeId));
		}

		return reviewFormListVO;
	}

	@Async
	public void dispatchReviewForms(PerformanceCycle cycle, Integer year, List<String> empIdList) throws Exception {
		ConcurrentHashMap<String, String> errorRecords = new ConcurrentHashMap<>();
		LOGGER.debug("dispatch review forms");

		for (String empId : empIdList) {
			Employee employee = employeeRepository.findByEmpId(empId);
			if (null != employee) {

				ReviewForm form = reviewFormRepository.findByEmpIdAndCycleAndYear(employee.getEmpId(), cycle, year);
				ReviewFormVO reviewFormVO = new ReviewFormVO();
				reviewFormVO.setCycle(cycle);
				reviewFormVO.setYear(year);
				reviewFormVO.setEmpId(employee.getEmpId());
				reviewFormVO.setAccomplishments(Collections.emptyList());
				reviewFormVO.setTrainingCertification(Collections.emptyList());
				if (null == form || form.getFormStatus().equals(FormStatus.FAILED)) {
					if (null != form)
						reviewFormVO.setId(form.getId());

					List<RoleTag> roleTags = roleTagRepository.findByTagNameAndStatus(employee.getDesignation(),
							Status.ACTIVE);
					if (!roleTags.isEmpty()) {
						RoleTag roleTag = roleTags.get(0);

						reviewFormVO.setFormStatus(FormStatus.PENDING);
						reviewFormVO.setFailureMsg("");
						List<CommonTagVO> commonTagVOList = new ArrayList<>();
						for (CommonTag source : roleTag.getRoleResponsibilities()) {
							CommonTagVO target = new CommonTagVO();
							BeanUtils.copyProperties(source, target);
							commonTagVOList.add(target);
						}
						reviewFormVO.setRoleResponsibilities(commonTagVOList);
						commonTagVOList = new ArrayList<>();
						for (CommonTag source : roleTag.getCommonCompetencies()) {
							CommonTagVO target = new CommonTagVO();
							BeanUtils.copyProperties(source, target);
							commonTagVOList.add(target);
						}
						reviewFormVO.setCommonCompetencies(commonTagVOList);
						commonTagVOList = new ArrayList<>();
						for (CommonTag source : roleTag.getBusinessPhilosophies()) {
							CommonTagVO target = new CommonTagVO();
							BeanUtils.copyProperties(source, target);
							commonTagVOList.add(target);
						}
						reviewFormVO.setBusinessPhilosophies(commonTagVOList);

						try {
							if (null == form)
								createReviewForm(reviewFormVO);
							else
								updateReviewForm(reviewFormVO);

							notifyEmployee(employee.getEmpId());
						} catch (ValidationException e) {
							LOGGER.error(
									"Error while creating form. Either validation failed for year or performance cycle for employee with id : "
											+ employee.getEmpId());
							errorRecords.put(employee.getEmpId(), "validation failed");
							reviewFormVO.setFormStatus(FormStatus.FAILED);
							reviewFormVO.setFailureMsg("validation failed");
							try {
								createReviewForm(reviewFormVO);

							} catch (Exception ex) {
								LOGGER.error(
										"Error while retrying to create form. Scenario : tag exist but with empty competencies or business philosophies or year/date validation failed again for employee with id : "
												+ employee.getEmpId());
							}

						} catch (Exception e) {
							LOGGER.error(
									"error while creating review form for employee with id :{} " + employee.getEmpId(),
									e);
							errorRecords.put(employee.getEmpId(), "DB/system error");
						}
					} else {
						// tag empty
						LOGGER.error("Tag not found for designation : " + employee.getDesignation());
						errorRecords.put(employee.getEmpId(),
								"tag not found for designation " + employee.getDesignation());
						reviewFormVO.setFormStatus(FormStatus.FAILED);
						reviewFormVO.setFailureMsg("tag not found");
						try {
							if (null != form)
								updateReviewForm(reviewFormVO);
							else
								createReviewForm(reviewFormVO);
						} catch (ValidationException e) {
							LOGGER.error(
									"Error while creating form. Either validation failed for year or performance cycle for employee with id : "
											+ employee.getEmpId());
							errorRecords.put(employee.getEmpId(), "validation failed");
						} catch (Exception e) {
							LOGGER.error(
									"Error while creating form. DB error. employee with id : " + employee.getEmpId());
							errorRecords.put(employee.getEmpId(), "DB/system error");
						}

					}
				} else {
					LOGGER.error("review form already generated for empId " + employee.getEmpId());
					errorRecords.put(employee.getEmpId(), "review form already exists");
				}

			} else {
				LOGGER.error("employee not found : " + empId);
				errorRecords.put(empId, "employee not found");
			}
		}

		EmailResponse emailResponse = sendNotification(mailBodyConstructor(errorRecords));
		LOGGER.error("email notification response is :\n" + emailResponse);

	}

	private void notifyEmployee(String employeeId) {

		/*
		 * Map<String, String> placeholders = new HashMap<>();
		 * placeholders.put("subject", Constants.EMAIL_SUBJECT_PENDING);
		 * notificationService.sendFormNotification(email, "PENDING.vm", placeholders);
		 */
		FormNotificationVO formNotificationVO = new FormNotificationVO();
		formNotificationVO.setRole(Role.HR);
		formNotificationVO.setFormStatus(FormStatus.PENDING);
		formNotificationVO.setEmployeeId(employeeId);

		notificationService.sendFormNotification(formNotificationVO);
	}

	private String mailBodyConstructor(ConcurrentHashMap<String, String> map) {
		if (map.isEmpty())
			return null;
		else {
			StringBuilder sb = new StringBuilder();
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				sb.append(entry.getKey());
				sb.append('=').append('"');
				sb.append(entry.getValue());
				sb.append('"');
				if (iter.hasNext()) {
					sb.append(',').append(System.lineSeparator());
				}
			}
			return sb.toString();
		}

	}

	private EmailResponse sendNotification(String mailBody) {
		NotificationVO notificationVO = new NotificationVO();
		notificationVO.setSubject(Constants.EMAIL_SUBJECT_FOR_FORM_DISPATCH_REPORT);
		notificationVO.setTo(hrEmailGroup);
		if (StringUtils.isBlank(mailBody))
			notificationVO.setHtml(Constants.EMAIL_ALL_RECORDS_SUCCESS_MESSAGE);
		else {
			notificationVO.setHtml(mailBody);
		}
		EmailResponse responseEntity = notificationService.sendNotification(notificationVO);
		return responseEntity;
	}

	/**
	 * method to get average of ratings and calculate final rating
	 * 
	 * @param roleSpecificRatings
	 * @param goalRatings
	 * @param commonCompetencyRatings
	 * @return Map<String,Double> allRatings
	 */
	public Map<String, Double> average(List<Integer> roleSpecificRatings, List<Integer> goalRatings,
			List<Integer> commonCompetencyRatings) {
		double averageRoleSpecificRating = (roleSpecificRatings != null && roleSpecificRatings.size() > 0)
				? getAverage(roleSpecificRatings)
				: 0;
		double averageGoalRatings = (goalRatings != null && goalRatings.size() > 0) ? getAverage(goalRatings) : 0;
		double averageCommonCompetencyRatings = (commonCompetencyRatings != null && commonCompetencyRatings.size() > 0)
				? getAverage(commonCompetencyRatings)
				: 0;
		double finalRatings = Math.round(((0.5 * averageRoleSpecificRating) + (0.3 * averageGoalRatings)
				+ (0.2 * averageCommonCompetencyRatings)) * 1e2) / 1e2;
		Map<String, Double> allRatings = new HashMap<>();
		allRatings.put("averageRoleSpecificRating", averageRoleSpecificRating);
		allRatings.put("averageGoalRatings", averageGoalRatings);
		allRatings.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		allRatings.put("finalRatings", finalRatings);
		return allRatings;

	}

	/**
	 * method to calculate average rating
	 * 
	 * @param int[] ratings
	 * @return double average
	 */
	private double getAverage(List<Integer> ratings) {
		double sum = 0;
		for (Integer rating : ratings) {
			sum += (rating == null) ? 0 : rating;
		}
		return Math.round((sum / ratings.size()) * 1e2) / 1e2;
	}

	public ReviewFormVO getReviewFormById(String formId) {
		ReviewForm reviewForm = reviewFormRepository.findOne(formId);
		if (reviewForm != null) {
			ReviewFormVO reviewFormVO = new ReviewFormVO();
			CommonUtils.copyNonNullProperties(reviewForm, reviewFormVO);
			reviewFormVO.setRoleResponsibilities(convertToVO(reviewForm.getRoleResponsibilities()));
			reviewFormVO.setCommonCompetencies(convertToVO(reviewForm.getCommonCompetencies()));
			reviewFormVO.setBusinessPhilosophies(convertToVO(reviewForm.getBusinessPhilosophies()));
			return reviewFormVO;

		} else {
			throw new NotFoundException(listOfErrors("No review forms generated found for given id: {}" + formId));
		}
	}

	private List<CommonTagVO> convertToVO(List<CommonTag> commonTags) {
		if (null != commonTags) {
			List<CommonTagVO> commonTagVOs = new ArrayList<>();
			for (CommonTag commonTag : commonTags) {
				CommonTagVO commonTagVO = new CommonTagVO();
				CommonUtils.copyNonNullProperties(commonTag, commonTagVO);
				commonTagVOs.add(commonTagVO);
			}
			return commonTagVOs;
		}
		return null;
	}

	public Path getFinalRatingReport(PerformanceCycle performanceCycle, Integer year) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!employeeRepository.findByEmail(authentication.getName()).getRoles().contains(Role.valueOf("HR"))) {
			throw new UnauthorizedException(listOfErrors("unauthorized"));
		}

//		List<ReviewForm> reviewForms = reviewFormRepository.findAllFormStatusByAndCycleAndYear(FormStatus.COMPLETED,
//				performanceCycle, year);
		List<ReviewForm> reviewForms = reviewFormRepository.findAllByAndCycleAndYear(performanceCycle, year);
		List<FinalRatingReportVO> finalRatingReportVOs = new ArrayList<FinalRatingReportVO>();

		for (ReviewForm reviewForm : reviewForms) {
			FinalRatingReportVO finalRatingReportVO = new FinalRatingReportVO();
			if (reviewForm.getFormStatus().equals(FormStatus.FAILED))
				finalRatingReportVO.setFormStatus("Form not sent to employee");
			else if (reviewForm.getFormStatus().equals(FormStatus.PENDING))
				finalRatingReportVO.setFormStatus("Form not submitted by employee");
			else if (reviewForm.getFormStatus().equals(FormStatus.UNDER_REVIEW))
				finalRatingReportVO.setFormStatus("Form not submitted by manager");
			else if (reviewForm.getFormStatus().equals(FormStatus.FORM_RECEIVED))
				finalRatingReportVO.setFormStatus("Form not submitted by DU head");
			else if (reviewForm.getFormStatus().equals(FormStatus.APPROVED))
				finalRatingReportVO.setFormStatus("Form approved by DU Head but pending at HR");
			else if (reviewForm.getFormStatus().equals(FormStatus.FINISHED))
				finalRatingReportVO.setFormStatus("Form screened by HR but pending at manager");
			else if (reviewForm.getFormStatus().equals(FormStatus.COMPLETED))
				finalRatingReportVO.setFormStatus("form approved by manager. Review Completed.");
			else if (reviewForm.getFormStatus().equals(FormStatus.ARCHIVED))
				finalRatingReportVO.setFormStatus("form is archived");

			finalRatingReportVO.setEmpId(reviewForm.getEmpId());
//			finalRatingReportVO.setFormStatus(reviewForm.getFormStatus());
			finalRatingReportVO.setEmpName(employeeRepository.findByEmpId(reviewForm.getEmpId()).getEmpName());
			finalRatingReportVO.setFinalRating((null != reviewForm.getRating()) ? reviewForm.getRating() : 0.0);
			finalRatingReportVOs.add(finalRatingReportVO);
		}

		Charset charset = Charset.forName("US-ASCII");
		Path file = Paths.get(finalRatingReportDir,
				year + "_" + performanceCycle + "_" + new Date().getTime() + "_" + "report.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
			writer.write(Constants.FINAL_RATING_REPORT_HEADER + "\n");
			writer.write("Year," + year + "\n");
			writer.write("Cycle," + performanceCycle + "\n\n\n");
			for (FinalRatingReportVO finalRatingReportVO : finalRatingReportVOs) {
				writer.write(finalRatingReportVO.toString());
			}
		} catch (IOException x) {
			LOGGER.error("io exception while creating final rating report");
		}

		return file;
	}
}
