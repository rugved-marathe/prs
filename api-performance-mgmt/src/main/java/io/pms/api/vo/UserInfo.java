package io.pms.api.vo;

import java.util.List;

import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Scope("session")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {
	
	private String userName;
	
	private String emailId;
	
	private String imageUrl;
	
	private List<String> groups;

	@Builder
	public UserInfo(String userName, String emailId, String imageUrl, List<String> groups) {
		super();
		this.userName = userName;
		this.emailId = emailId;
		this.imageUrl = imageUrl;
		this.groups = groups;
	}

}
