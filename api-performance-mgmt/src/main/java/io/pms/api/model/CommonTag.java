package io.pms.api.model;

import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
public class CommonTag {
	@NotNull
	String attribute;
	String selfComments;
	String reviewerComments;
	Integer rating;

}
