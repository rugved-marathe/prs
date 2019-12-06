package io.pms.api.exception;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides template to return error response when exception occurs.
 * 
 */
@Getter
@Setter
public class ClientErrorResponse {
	private List<Errors> errors;
}
