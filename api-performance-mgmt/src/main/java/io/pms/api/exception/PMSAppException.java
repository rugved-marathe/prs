package io.pms.api.exception;

import java.util.List;

public class PMSAppException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PMSAppException(List<Errors> errorList) {
		super(errorList);
	}
}
