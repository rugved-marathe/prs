package io.pms.api.exception;

import java.util.List;

public class DocumentCreationException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DocumentCreationException(List<Errors> errorList) {
		super(errorList);
	}

}
