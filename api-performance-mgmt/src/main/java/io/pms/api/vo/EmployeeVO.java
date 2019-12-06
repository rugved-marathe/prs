package io.pms.api.vo;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.INVALID_JOIN_DATE;
import static io.pms.api.common.Constants.INVALID_LEAVE_DATE;
import static io.pms.api.common.Constants.INVALID_PERFORMANCE_CYCLE;
import static io.pms.api.common.Constants.INVALID_STATUS;
import static io.pms.api.common.Constants.MISSING_JOIN_DATE;
import static io.pms.api.common.Constants.SOURCE_EXISTS;
import static io.pms.api.common.Constants.SOURCE_REQUIRED;
import static io.pms.api.common.ErrorType.ALREADY_EXIST;
import static io.pms.api.common.ErrorType.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import io.pms.api.exception.Errors;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EmployeeVO {

	private String status;
	private String address;
	private String coverImage;
	private String currentDUHeadId;
	private String duHeadName;
	private String currentMgrId;
	private String managerName;
	private List<String> currentProject;
	private String dateOfJoining;
	private String dateOfLeaving;
	private String designation;
	private String email;
	private String empId;
	private String empName;
	private String fatherName;
	private String highestEducation;
	private String motherName;
	private PerformanceCycle performanceCycle;
	private String profileImage;
	private Set<Role> roles;
	private Period experience;

	public List<Errors> validateCreateEmployee() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = BAD_REQUEST.getErrorMessage();

		if (StringUtils.isEmpty(this.getEmpName()))
			createErrorList("emoloyee name", errorMessage, SOURCE_REQUIRED, errorList);
		if (StringUtils.isEmpty(this.getCurrentDUHeadId()))
			createErrorList("current DU head id", errorMessage, SOURCE_REQUIRED, errorList);
		if (StringUtils.isEmpty(this.getDateOfJoining()))
			createErrorList("date of joining", errorMessage, SOURCE_REQUIRED, errorList);
		else {
			try {
				DateTime joiningDate = DateTime.parse(this.getDateOfJoining(), DateTimeFormat.forPattern("dd/MM/yyyy"));
			} catch (Exception e) {
				createErrorList("invalid joining date format", errorMessage, INVALID_STATUS, errorList);
			}
		}
		if (StringUtils.isEmpty(this.getCurrentMgrId()))
			createErrorList("current manager id", errorMessage, SOURCE_REQUIRED, errorList);
		if (StringUtils.isEmpty(this.getEmail()))
			createErrorList("employee email", errorMessage, SOURCE_REQUIRED, errorList);
		if (StringUtils.isEmpty(this.getEmpId()))
			createErrorList("emoloyee id", errorMessage, SOURCE_REQUIRED, errorList);
		if (StringUtils.isEmpty(this.getStatus()))
			createErrorList("emoloyee status", errorMessage, SOURCE_REQUIRED, errorList);
		else if (!EnumUtils.isValidEnum(Status.class, this.getStatus().toUpperCase()))
			createErrorList("employee status", errorMessage, INVALID_STATUS, errorList);
//		if (StringUtils.isEmpty(this.getPerformanceCycle()))
//			createErrorList("emoloyee performance cycle", errorMessage, SOURCE_REQUIRED, errorList);
//		else
			if (!EnumUtils.isValidEnum(PerformanceCycle.class, this.getPerformanceCycle().toString().toUpperCase()))
			createErrorList("employee performance cycle", errorMessage, INVALID_PERFORMANCE_CYCLE, errorList);

		return errorList;
	}

	public List<Errors> validateDuplicateEmployee() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = ALREADY_EXIST.getErrorMessage();
		createErrorList("employee", errorMessage, SOURCE_EXISTS, errorList);
		return errorList;
	}

	public List<Errors> dateOfLeavingError() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = BAD_REQUEST.getErrorMessage();
		createErrorList("Date of Leaving", errorMessage, INVALID_LEAVE_DATE, errorList);
		return errorList;
	}

	public List<Errors> dateOfJoiningError() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = BAD_REQUEST.getErrorMessage();
		createErrorList("Date of Joining", errorMessage, INVALID_JOIN_DATE, errorList);
		return errorList;
	}

	public List<Errors> dateOfJoiningMissing() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = BAD_REQUEST.getErrorMessage();
		createErrorList("Date of Joining", errorMessage, MISSING_JOIN_DATE, errorList);
		return errorList;
	}

}
