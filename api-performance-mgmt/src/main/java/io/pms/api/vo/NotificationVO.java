package io.pms.api.vo;

import lombok.Data;

@Data
public class NotificationVO {

	private String apiKey;
	private String email;
	private String to;
	private String subject;
	private String html;
}
