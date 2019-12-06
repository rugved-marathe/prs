package io.pms.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.services.UserService;
import io.pms.api.vo.EmployeeVO;

@RestController
@RequestMapping("/getUserProfile")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<EmployeeVO> getUserProfile(Authentication authentication) {
		return new ResponseEntity<>(userService.getUserProfile(authentication), HttpStatus.OK);
	}
}
