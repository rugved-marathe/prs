package io.pms.api.exception;

import java.util.List;

/**
 * The class {@code AlreadyExistsException} indicates condition that a resource
 * already exists for given parameters.
 */

public class AlreadyExistsException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyExistsException(List<Errors> errorList) {
		super(errorList);
	}
}
