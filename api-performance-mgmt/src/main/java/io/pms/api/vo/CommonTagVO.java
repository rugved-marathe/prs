package io.pms.api.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CommonTagVO {
	@NotNull
	private String attribute;
	private String selfComments;
	private String reviewerComments;
	private Integer rating;

}
