package io.pms.api.services;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.CommonUtils.listOfErrors;
import static io.pms.api.common.Constants.INVALID_STATUS;
import static io.pms.api.common.Constants.SOURCE_REQUIRED;
import static io.pms.api.common.ErrorType.RESOURCE_NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.pms.api.common.CommonUtils;
import io.pms.api.common.CompletionStatus;
import io.pms.api.common.Constants;
import io.pms.api.common.ErrorType;
import io.pms.api.common.GoalType;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Status;
import io.pms.api.exception.Errors;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.Employee;
import io.pms.api.model.Goal;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.repositories.GoalRepository;
import io.pms.api.vo.GoalListVO;
import io.pms.api.vo.GoalVO;

@Service
public class GoalService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoalService.class);

	@Autowired
	private GoalRepository goalRepository;
	@Autowired
	private EmployeeRepository employeeRepository;

	/**
	 * @param goalListVO
	 * @return GoalVO
	 */
	public GoalVO addGoal(GoalVO goalVO) {
		LOGGER.debug("adding new goal");
		List<Errors> validationErrorList = goalVO.validateCreateGoal();
		if (validationErrorList.isEmpty()) {
			LOGGER.debug("adding new goal: validation passed");
			Goal goal = new Goal();
			CommonUtils.copyNonNullProperties(goalVO, goal);
			goal.setCompletionStatus(goalVO.getCompletionStatus());
			goal.setStatus(Status.ACTIVE);
			Goal createdGoal = goalRepository.save(goal);
			CommonUtils.copyNonNullProperties(createdGoal, goalVO);
		} else {
			LOGGER.error("goal validation failed. Error list: " + validationErrorList);
			throw new ValidationException(validationErrorList);

		}
		return goalVO;
	}

	public void addMultipleGoal(List<GoalVO> goals) {
		LOGGER.debug("adding multiple new goals");
		goals.forEach(goal -> {
			this.addGoal(goal);
		});
	}

	/**
	 * @param empId
	 * @param performanceCycle
	 * @param year
	 * @return GoalListVO
	 */
	public GoalListVO getGoals(String empId, String performanceCycle, Integer year) {
		LOGGER.debug("fetching goals for: " + empId + " for cycle: " + performanceCycle + "for year: " + year);
		Employee employee = employeeRepository.findByEmpId(empId);
		if (null != employee) {
			List<Goal> goals = new ArrayList<Goal>();
			goals = goalRepository.findAllByEmpIdAndPerformanceCycleAndYear(empId, performanceCycle, year);
			List<GoalVO> goalVOList = goals.stream()
					.filter(goal -> (goal.getStatus() == null || !goal.getStatus().equals(Status.INACTIVE)))
					.map(goal -> {
						GoalVO goalVO = new GoalVO();
						CommonUtils.copyNonNullProperties(goal, goalVO);
						return goalVO;
					}).collect(Collectors.toList());
			GoalListVO goalListVO = new GoalListVO();
			goalListVO.setGoals(goalVOList);
			return goalListVO;
		} else {
			LOGGER.error("fetch goals: employee not found");
			throw new NotFoundException(listOfErrors("employee"));
		}

	}

	/**
	 * @param goalVO
	 * @return GoalVO
	 */
	public GoalVO editGoal(GoalVO goalVO) {
		LOGGER.debug("update goal");
		Goal goal = goalRepository.findOne(goalVO.getId());
		if (null != goal) {
			LOGGER.debug("updating goal with id: " + goal.getId());
			GoalVO goalsVO = new GoalVO();
			CommonUtils.copyNonNullProperties(goalVO, goal);
			Goal editedGoal = goalRepository.save(goal);
			CommonUtils.copyNonNullProperties(editedGoal, goalsVO);
			return goalsVO;
		} else {
			LOGGER.error("edit goals: goal not found");
			throw new NotFoundException(listOfErrors("goal"));
		}

	}

	/**
	 * @param goalId
	 */
	public void deleteGoals(String goalId) {
		LOGGER.debug("delete goal");
		Goal goal = goalRepository.findOne(goalId);
		if (null != goal) {
			LOGGER.debug("deleting goal with id: " + goalId);
			goal.setStatus(Status.INACTIVE);
			goalRepository.save(goal);
		} else {
			LOGGER.error("delete goals: goal not found");
			throw new NotFoundException(listOfErrors("goal"));
		}
	}

	public List<String> addPreviousGoals(MultipartFile file) {
		List<String> unProcessedEmpIds = new LinkedList<>();

		List<Errors> errorList = validateFile(file);
		if (!errorList.isEmpty()) {
			LOGGER.debug("Error occurred while validating input file");
			throw new ValidationException(errorList);
		}
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));) {
			bufferedReader.lines().skip(1).forEach(line -> {
				String[] records = parseRecord(line);

				try {
					for (int i = 5; i < records.length; i++) {
						if (StringUtils.isNotBlank(records[i])) {
							GoalVO goalVO = createGoalVO(records[0], PerformanceCycle.valueOf(records[1].toUpperCase()),
									Integer.parseInt(records[2]), records[i], GoalType.valueOf(records[3].toUpperCase()), Integer.parseInt(records[4]));
							addGoal(goalVO);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred while processing employee with id {}", records[0], e);
					unProcessedEmpIds.add(records[0]);
				}
			});
		} catch (IOException e) {
			LOGGER.error("IO Error occurred while processing input file: {}", file.getName(), e);
			Errors errors = new Errors("IO", RESOURCE_NOT_FOUND.getErrorMessage(),
					"IO Error occurred while processing input file");
			throw new PMSAppException(Arrays.asList(errors));
		}

		LOGGER.debug("Unprocessed records: {}", unProcessedEmpIds);
		return unProcessedEmpIds;
	}

	private GoalVO createGoalVO(String empId, PerformanceCycle performanceCycle, Integer year, String description, GoalType goalType, Integer duration) {
		GoalVO goalVO = new GoalVO();
		goalVO.setEmpId(empId);
		goalVO.setDescription(description);
		goalVO.setPerformanceCycle(performanceCycle);
		goalVO.setYear(year);
		goalVO.setCompletionStatus(CompletionStatus.ASSIGNED);
		goalVO.setDuration(duration);
		goalVO.setGoalType(goalType);
		return goalVO;
	}

	private List<Errors> validateFile(MultipartFile file) {
		List<Errors> errorList = new ArrayList<>();

		if (null != file && !file.isEmpty()) {
			if (!file.getOriginalFilename().contains(".tsv")) {
				createErrorList("Input file extention", ErrorType.BAD_REQUEST.getErrorMessage(), INVALID_STATUS,
						errorList);
			}

		} else {
			createErrorList("Input file", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED, errorList);
		}
		return errorList;
	}

	private String[] parseRecord(String line) {
		String[] records = new String[0];
		if (StringUtils.isNotBlank(line))
			records = line.split(Constants.PIPE);
		LOGGER.debug("Processed line records: {}", Arrays.toString(records));
		return records;
	}

}