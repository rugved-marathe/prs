package io.pms.api.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.services.GoogleCredentialService;
import io.pms.api.vo.UserInfo;

@RestController
public class GoogleCredentialsController {

	@Autowired
	private GoogleCredentialService credentialService;

	@PostMapping(value = "/pmslogin", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<String> loginProcess(@RequestBody UserInfo userInfo) {
		String response = credentialService.createRequestForAccessToken(userInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/logout")
	public ResponseEntity<String> logoutProcess(HttpServletRequest httpRequest) {
		String response = credentialService.createRequestForLogout(httpRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}