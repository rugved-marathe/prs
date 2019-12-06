package io.pms.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.services.RoleTagService;
import io.pms.api.vo.RoleTagListVO;
import io.pms.api.vo.RoleTagVO;

@RestController
@RequestMapping(value = "/roleTag")
public class RoleTagController {
	@Autowired
	private RoleTagService roleTagService;

	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<RoleTagListVO> getAllExisitingTags() {
		RoleTagListVO tagListVO = roleTagService.getAllExistingTags();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("X-Total-Count", String.valueOf(tagListVO.getRoleTags().size()));
		return new ResponseEntity<>(tagListVO, responseHeaders, HttpStatus.OK);
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<RoleTagVO> getRoleTag(@RequestParam("tagName") String tagName) {
		return new ResponseEntity<>(roleTagService.getRoleTag(tagName), HttpStatus.OK);
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<RoleTagVO> addRoleTag(@RequestBody RoleTagVO roleTagVO) {
		return new ResponseEntity<>(roleTagService.addRoleTag(roleTagVO), HttpStatus.CREATED);
	}

	@PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<RoleTagVO> updateRoleTag(@RequestBody RoleTagVO roleTagVO) {
		return new ResponseEntity<>(roleTagService.updateRoleTag(roleTagVO), HttpStatus.OK);
	}

	@DeleteMapping(produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<RoleTagVO> deleteRoleTag(@RequestParam("tagId") String tagId,
			@RequestParam(value = "disabled", required = false) boolean status) {
		return new ResponseEntity<>(roleTagService.deleteRoleTag(tagId, status), HttpStatus.ACCEPTED);
	}
}
