package io.pms.api.vo;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.SOURCE_REQUIRED;
import static io.pms.api.common.ErrorType.RESOURCE_NULL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.pms.api.common.ErrorType;
import io.pms.api.common.Status;
import io.pms.api.exception.Errors;
import io.pms.api.model.CommonTag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RoleTagVO {

	String id;
	String tagName;
	String description;
	List<CommonTag> roleResponsibilities;
	List<CommonTag> commonCompetencies;
	List<CommonTag> businessPhilosophies;
	private DateTime modifiedDate;
	private String modifiedBy;
	private Status status;

	public List<Errors> validateRoleTag() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = RESOURCE_NULL.getErrorMessage();

		if (null == this.getTagName() || StringUtils.isEmpty(this.getTagName()))
			createErrorList("Tag name must not be null", errorMessage, SOURCE_REQUIRED, errorList);

		checkMinimumRequiredForTag(errorList);

		return errorList;
	}

	private void checkMinimumRequiredForTag(List<Errors> errorList) {
		if (null == roleResponsibilities || roleResponsibilities.isEmpty()) {
			createErrorList("Role specific responsibility", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED,
					errorList);
		}
		if (null == commonCompetencies || commonCompetencies.isEmpty()) {
			createErrorList("Common competency", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED,
					errorList);
		}
		if (null == businessPhilosophies || businessPhilosophies.isEmpty()) {
			createErrorList("Business philosophy", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED,
					errorList);
		}
	}
}
