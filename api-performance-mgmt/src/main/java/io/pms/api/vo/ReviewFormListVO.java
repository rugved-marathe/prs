package io.pms.api.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ReviewFormListVO {
	private List<ReviewFormVO> reviewForms;
}
