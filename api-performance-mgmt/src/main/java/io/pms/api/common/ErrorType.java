package io.pms.api.common;

import org.springframework.http.HttpStatus;

/**
 * Enum representing custom error types with associated error messages.
 *
 */

public enum ErrorType {

	BAD_REQUEST("Bad request, unable to parse field", HttpStatus.BAD_REQUEST), RESOURCE_NOT_FOUND(
			"Requested Resource Not Found", HttpStatus.NOT_FOUND), ALREADY_EXIST("Entity already exist",
					HttpStatus.CONFLICT), RESOURCE_NULL("Resource is Null", HttpStatus.NOT_MODIFIED);
	private String errorMessage;

	ErrorType(String errorMessage, HttpStatus httpStatus) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
