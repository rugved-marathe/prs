package io.pms.api.exception;

import java.util.List;

public class ValidationException extends BaseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException(List<Errors> errorsList) {
        super(errorsList);
    }

}
