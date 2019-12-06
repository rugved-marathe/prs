package io.pms.api.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import lombok.Data;

@Data
@Document(collection = "ReviewForm")
public class ReviewForm {
	
	@Id
	private String id;
	@NotNull
	private String empId;
	private PerformanceCycle cycle;
	private Integer year;
	private FormStatus formStatus;
	private Double rating;
	private DateTime finalDiscussionDate;
	@CreatedBy
	private String createdBy;
	@CreatedDate
	private String createdDate;
	@LastModifiedBy
	private String lastModifiedBy;
	@LastModifiedDate
	private DateTime lastModifiedDate;
	private List<Accomplishment> accomplishments;
	private List<TrainingCertification> trainingCertification;
	private List<CommonTag> roleResponsibilities;
	private List<CommonTag> commonCompetencies;
	private List<CommonTag> businessPhilosophies;
	private String failureMsg;
	private Double avgRoleSpecificRating;
	private Double avgCommonCompetencyRating;
	private Double avgGoalRating;
}
