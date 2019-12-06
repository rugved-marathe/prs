package io.pms.api.model;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import io.pms.api.common.CompletionStatus;
import io.pms.api.common.GoalType;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Status;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@NoArgsConstructor
@Document(collection = "Goal")
public class Goal {
	private CompletionStatus completionStatus;
	@CreatedBy
	private String createdBy;
	@CreatedDate
	private String createdDate;
	@NotNull
	private String description;
	@NonNull
	private String empId;
	@Id
	@Setter(AccessLevel.NONE)
	private String id;
	@LastModifiedBy
	private String lastModifiedBy;
	@LastModifiedDate
	private DateTime lastModifiedDate;
	@NonNull
	private PerformanceCycle performanceCycle;
	private Integer rating;
	private String reviewerComments;
	@NonNull
	private Integer year;
	private Status status;
	@NonNull
	private GoalType goalType;
	@NonNull
	private Integer duration;
}
