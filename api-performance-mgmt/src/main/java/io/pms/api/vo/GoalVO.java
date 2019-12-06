package io.pms.api.vo;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.INVALID_PERFORMANCE_CYCLE;
import static io.pms.api.common.Constants.INVALID_DURATION;
import static io.pms.api.common.Constants.SOURCE_REQUIRED;
import static io.pms.api.common.Constants.INVALID_GOAL_TYPE;
import static io.pms.api.common.ErrorType.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import io.pms.api.common.CompletionStatus;
import io.pms.api.common.GoalType;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.exception.Errors;
import lombok.Data;
import lombok.Getter;

@Data
public class GoalVO {
	private String description;
	private String reviewerComments;
	private CompletionStatus completionStatus;
	private Integer rating;
	private Integer year;
	private String empId;
	@Getter
	private String id;
	private GoalType goalType;
	private Integer duration;
	private PerformanceCycle performanceCycle;

	public List<Errors> validateCreateGoal() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = BAD_REQUEST.getErrorMessage();
		if (StringUtils.isEmpty(this.getEmpId()))
			createErrorList("employee id", errorMessage, SOURCE_REQUIRED, errorList);
		if (StringUtils.isEmpty(this.getDescription()))
			createErrorList("goal description", errorMessage, SOURCE_REQUIRED, errorList);
		if (null == this.getYear())
			createErrorList("year of goal", errorMessage, SOURCE_REQUIRED, errorList);
		if (null == this.getPerformanceCycle())
			createErrorList("emoloyee performance cycle", errorMessage, SOURCE_REQUIRED, errorList);
		else if (!EnumUtils.isValidEnum(PerformanceCycle.class, this.getPerformanceCycle().toString().toUpperCase()))
			createErrorList("employee performance cycle", errorMessage, INVALID_PERFORMANCE_CYCLE, errorList);
		if (null == this.getDuration()) {
			createErrorList("goal duration", errorMessage, SOURCE_REQUIRED, errorList);
		} else if (this.getDuration() < 1 || this.getDuration() > 6) {
			createErrorList("goal duration", errorMessage, INVALID_DURATION, errorList);
		}
		if (null == this.getGoalType()) {
			createErrorList("goal type", errorMessage, SOURCE_REQUIRED, errorList);
		} else if (!EnumUtils.isValidEnum(GoalType.class, this.getGoalType().toString().toUpperCase())) {
			createErrorList("goal type", errorMessage, INVALID_GOAL_TYPE, errorList);
		}
		return errorList;
	}
}
