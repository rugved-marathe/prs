package io.pms.api.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import io.pms.api.common.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "RoleTag")
@NoArgsConstructor
public class RoleTag {
	
	@Id
	private String id;
	@NotNull
	private String tagName;
	private String description;
	private List<CommonTag> roleResponsibilities;
	private List<CommonTag> commonCompetencies;
	private List<CommonTag> businessPhilosophies;
	@LastModifiedDate
	private DateTime modifiedDate;
	@LastModifiedBy
	private String modifiedBy;
	private Status status;
}
