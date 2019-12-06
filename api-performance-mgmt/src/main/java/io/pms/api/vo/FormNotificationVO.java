package io.pms.api.vo;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.SOURCE_REQUIRED;
import static io.pms.api.common.ErrorType.RESOURCE_NULL;

import java.util.ArrayList;
import java.util.List;

import io.pms.api.common.FormStatus;
import io.pms.api.common.Role;
import io.pms.api.exception.Errors;
import lombok.Data;

@Data
public class FormNotificationVO {

	private Role role;
	private FormStatus formStatus;
	private String employeeId;

	public List<Errors> validateFormNotificationVO() {
		List<Errors> errorList = new ArrayList<>();
		String errorMessage = RESOURCE_NULL.getErrorMessage();

		if (null == this.role || null == this.formStatus) {
			createErrorList("Employee Role or Form Status ", errorMessage, SOURCE_REQUIRED, errorList);
		}

		return errorList;
	}
}
