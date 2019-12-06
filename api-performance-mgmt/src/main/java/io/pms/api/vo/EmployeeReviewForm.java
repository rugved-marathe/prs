package io.pms.api.vo;

import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import lombok.Data;

@Data
public class EmployeeReviewForm {

	private String empId;
	private String empName;
	private String currentMgrId;
	private String currentMgrName;
	private String designation;
	private String profileImage;
	private PerformanceCycle cycle;
	private FormStatus formStatus;
	private String formId;
	private Integer year;
}
