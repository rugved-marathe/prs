package io.pms.api.common;

/**
 * {@code Constants} Provides application constants.
 * 
 */

public interface Constants {
	String SOURCE_REQUIRED = " is required";
	String SOURCE_EXISTS = " already exists";
	String NOT_FOUND = " not found";
	String INVALID_LEAVE_DATE = " cannot be on or before Joining Date";
	String INVALID_DURATION = " duration is invalid";
	String INVALID_GOAL_TYPE = " goal type is not valid";
	String INVALID_PERFORMANCE_CYCLE = " is invalid";
	String INVALID_STATUS = " is invalid";
	String INVALID_JOIN_DATE = " cannot be after today's date";
	String MISSING_JOIN_DATE = " cannot be empty";
	String TOKEN_GENERATION_SUCCESS = "Success";
	String ROLE_TRUSTED_CLIENT = "ROLE_TRUSTED_CLIENT";
	String CLIENT_ID = "my-trusted-client";
	String UNAUTHORIZED_MESSAGE = "user is not registerd with the application";
	int TOKEN_VALIDITY = 36000;
	String COMMA = ",";
	String PIPE = "\\|";
	String SEMICOLON = ";";
	String ONE = "1";
	String ZERO = "0";
	String EMAIL_SUBJECT_FOR_FORM_DISPATCH_REPORT = "Review forms dispatch report";
	String EMAIL_ALL_RECORDS_SUCCESS_MESSAGE = "All the review forms dispatched successfully!";
	
	String EMAIL_SUBJECT_UNDER_REVIEW = "Review form received for managerial review";
	String EMAIL_SUBJECT_FORM_RECIEVED = "Review form received for DU approval";
	String EMAIL_SUBJECT_APPROVED = "Review form has been approved by DU";
	String EMAIL_SUBJECT_PENDING = "Review form has been generated";
	String EMAIL_SUBJECT_FINISHED = "Review form has been approved";
	String EMAIL_SUBJECT_COMPLETED = "Review process completed";
	String EMAIL_SUBJECT_UNDER_REVIEW_EMPLOYEE = "Review form sent for managerial review";
	String EMAIL_SUBJECT_FINISHED_EMPLOYEE = "Review process finished";
	
	String FINAL_RATING_REPORT_HEADER = "Final rating report" ;
	
}
