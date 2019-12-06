package io.pms.api.exception;

import java.util.List;

public class UnauthorizedException  extends BaseException{
	private static final long serialVersionUID = 1L;

	public UnauthorizedException(List<Errors> errorList) {
		super(errorList);
	}

}
