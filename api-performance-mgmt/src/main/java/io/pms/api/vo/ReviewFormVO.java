package io.pms.api.vo;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.INVALID_PERFORMANCE_CYCLE;
import static io.pms.api.common.Constants.SOURCE_REQUIRED;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.pms.api.common.ErrorType;
import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.exception.Errors;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ReviewFormVO {
	private String id;
	private String empId;
	private PerformanceCycle cycle;
	private Integer year;
	private FormStatus formStatus;
	private Double rating;
	private String finalDiscussionDate;
	private List<AccomplishmentVO> accomplishments;
	private List<TrainingCertificationVO> trainingCertification;
	private List<CommonTagVO> roleResponsibilities;
	private List<CommonTagVO> commonCompetencies;
	private List<CommonTagVO> businessPhilosophies;
	private String failureMsg;
	private Double avgRoleSpecificRating;
	private Double avgCommonCompetencyRating;
	private Double avgGoalRating;
	
	public List<Errors> validate(){

		List<Errors> errorList = new ArrayList<>();

		if (StringUtils.isBlank(empId))
			createErrorList("Employee Id", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED, errorList);

		if (null == year || year < Year.now().getValue() || year > Year.now().plusYears(1).getValue())
			createErrorList("Year value", ErrorType.BAD_REQUEST.getErrorMessage(), INVALID_PERFORMANCE_CYCLE,
					errorList);
		if (FormStatus.PENDING.equals(formStatus)) {
			checkMinRequiredToCreateForm(errorList);
		} else if (FormStatus.UNDER_REVIEW.equals(formStatus)) {
			checkMinRequiredForReviewProcess(errorList);
		} else if (FormStatus.APPROVED.equals(formStatus)) {
			checkMinRequiredToApprove(errorList);
		}

		return errorList;
	}

	private void checkMinRequiredToApprove(List<Errors> errorList) {
		List<CommonTagVO> roleResponsibilityList = roleResponsibilities.stream()
				.filter(roleResponsibility -> (roleResponsibility.getReviewerComments() == null
						|| roleResponsibility.getRating() == null))
				.collect(Collectors.toList());

		List<CommonTagVO> commonCompetenciesList = commonCompetencies.stream()
				.filter(commonCompetency -> (commonCompetency.getReviewerComments() == null
						|| commonCompetency.getRating() == null))
				.collect(Collectors.toList());

		List<CommonTagVO> businessPhilosophiesList = businessPhilosophies.stream()
				.filter(businessPhilosophy -> (businessPhilosophy.getReviewerComments() == null))
				.collect(Collectors.toList());

		if (null == roleResponsibilityList || !roleResponsibilityList.isEmpty())
			createErrorList("Role Responsibility : Reviewer Comments", ErrorType.BAD_REQUEST.getErrorMessage(),
					SOURCE_REQUIRED, errorList);
		if (null == commonCompetenciesList || !commonCompetenciesList.isEmpty())
			createErrorList("Common Competency : Reviewer Comments", ErrorType.BAD_REQUEST.getErrorMessage(),
					SOURCE_REQUIRED, errorList);
		if (null == businessPhilosophiesList || !businessPhilosophiesList.isEmpty())
			createErrorList("Business Philosophy : Reviewer Comments", ErrorType.BAD_REQUEST.getErrorMessage(),
					SOURCE_REQUIRED, errorList);
	}

	private void checkMinRequiredForReviewProcess(List<Errors> errorList) {
		List<CommonTagVO> roleResponsibilityList = roleResponsibilities.stream()
				.filter(roleResponsibility -> roleResponsibility.getSelfComments() == null)
				.collect(Collectors.toList());

		List<CommonTagVO> commonCompetenciesList = commonCompetencies.stream()
				.filter(commonCompetency -> commonCompetency.getSelfComments() == null).collect(Collectors.toList());

		List<CommonTagVO> businessPhilosophiesList = businessPhilosophies.stream()
				.filter(businessPhilosophy -> businessPhilosophy.getSelfComments() == null)
				.collect(Collectors.toList());

		if (null == roleResponsibilityList || !roleResponsibilityList.isEmpty())
			createErrorList("Role Responsibility ", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED,
					errorList);
		if (null == commonCompetenciesList || !commonCompetenciesList.isEmpty())
			createErrorList("Common Competency ", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED, errorList);
		if (null == businessPhilosophiesList || !businessPhilosophiesList.isEmpty())
			createErrorList("Business Philosophy ", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED,
					errorList);
	}

	private void checkMinRequiredToCreateForm(List<Errors> errorList) {
		if (null == roleResponsibilities || roleResponsibilities.isEmpty())
			createErrorList("Role Responsibilities", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED,
					errorList);
		if (null == commonCompetencies || commonCompetencies.isEmpty())
			createErrorList("Common Competencies", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED, errorList);
		if (null == businessPhilosophies || businessPhilosophies.isEmpty())
			createErrorList("Business Philosophies", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED,
					errorList);
	}
}
