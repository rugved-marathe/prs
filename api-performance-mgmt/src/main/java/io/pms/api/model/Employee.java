package io.pms.api.model;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import lombok.Data;

@Data
@Document(collection = "Employees")
public class Employee implements Cloneable {
	@NotNull
	private Status status;
	private String address;
	private String coverImage;
	@NotNull
	private String currentDUHeadId;
	@NotNull
	private String currentMgrId;
	private List<String> currentProject;
	@NotNull
	private DateTime dateOfJoining;
	private DateTime dateOfLeaving;
	private String designation;
	@NotNull
	@Indexed(unique = true)
	private String email;
	@Indexed(unique = true)
	private String empId;
	@NotNull
	private String empName;
	private String fatherName;
	private String highestEducation;
	@Id
	private String id;
	@CreatedBy
	private String createdBy;
	@CreatedDate
	private DateTime createdDate;
	@LastModifiedBy
	private String modifiedBy;
	@LastModifiedDate
	private DateTime modifiedDate;
	private String motherName;
	@NotNull
	private PerformanceCycle performanceCycle;
	private String profileImage;
	@NotNull
	private Set<Role> roles;
}
