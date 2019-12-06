package io.pms.api.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Provide advice to controller to handle all ruleset management related
 * exceptions.
 * 
 */

@ControllerAdvice
public class ExceptionControllerAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ClientErrorResponse> validationExceptionExceptionHandler(
			ValidationException validationException) {
		LOGGER.error("Validation failed", validationException);
		return getClientErrorResponse(HttpStatus.BAD_REQUEST, validationException.getErrorList());
	}

	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<ClientErrorResponse> alreadyExistsExceptionHandler(AlreadyExistsException alreadyExistsException) {
		LOGGER.error("Validation failed", alreadyExistsException);
		return getClientErrorResponse(HttpStatus.CONFLICT, alreadyExistsException.getErrorList());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ClientErrorResponse> notFoundExceptionHandler(NotFoundException notFoundException) {
		LOGGER.error("Validation failed", notFoundException);
		return getClientErrorResponse(HttpStatus.NOT_FOUND, notFoundException.getErrorList());
	}

	@ExceptionHandler(AlreadyDeletedEntityException.class)
	public ResponseEntity<ClientErrorResponse> alreadyDeletedEntityException(AlreadyDeletedEntityException alreadyDeletedEntityException){
		LOGGER.error("Already Deleted Entity", alreadyDeletedEntityException);
		return getClientErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, alreadyDeletedEntityException.getErrorList());
	}
	
	@ExceptionHandler(PMSAppException.class)
	public ResponseEntity<ClientErrorResponse> pmsAppException(PMSAppException pmsAppException){
		LOGGER.error(pmsAppException.getMessage(), pmsAppException);
		return getClientErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, pmsAppException.getErrorList());
	}
	
	public ResponseEntity<ClientErrorResponse> getClientErrorResponse(HttpStatus httpStatus, List<Errors> errors) {
		ClientErrorResponse clientErrorResponse = new ClientErrorResponse();
		clientErrorResponse.setErrors(errors);
		return new ResponseEntity<>(clientErrorResponse, httpStatus);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ClientErrorResponse> unauthorizedExceptionExceptionHandler(
			UnauthorizedException unauthorizedException) {
		LOGGER.error("Validation failed", unauthorizedException);
		return getClientErrorResponse(HttpStatus.UNAUTHORIZED,unauthorizedException.getErrorList());
	}
	
	
	
}