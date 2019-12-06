package io.pms.api.exception;

import java.util.List;

/**
 * The class {@code NotFoundException} indicates condition that resource
 * for provided parameters not found.
 */
public class NotFoundException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param errorList
	 */
	public NotFoundException(List<Errors> errorList) {
		super(errorList);
	}
}
