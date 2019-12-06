package io.pms.api.exception;

import java.util.List;

public class AlreadyDeletedEntityException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param errorList
	 */
	public AlreadyDeletedEntityException(List<Errors> errorList) {
		super(errorList);
	}
}
