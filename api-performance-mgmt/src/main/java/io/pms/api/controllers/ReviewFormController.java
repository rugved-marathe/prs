package io.pms.api.controllers;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.exception.ValidationException;
import io.pms.api.services.ReviewFormService;
import io.pms.api.vo.GenericResponse;
import io.pms.api.vo.ReviewFormListVO;
import io.pms.api.vo.ReviewFormVO;

@RestController
@RequestMapping("/review")
public class ReviewFormController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewFormController.class);
	@Autowired
	private ReviewFormService reviewFormService;
	

	@PostMapping(value = "/swapCycle", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<GenericResponse> swapReviewCycle(@RequestBody List<String> empIds) {
		return new ResponseEntity<>(reviewFormService.swapReviewCycle(empIds), HttpStatus.OK);
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ReviewFormListVO> getReviewForm(@RequestParam("employeeId") String employeeId,
			@RequestParam(value = "performanceCycle", required = false) String performanceCycle,
			@RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "role", required = false) Role role) {
		ReviewFormListVO reviewFormList = reviewFormService.getReviewForm(employeeId, performanceCycle, year, role);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("X-Total-Count", String.valueOf(reviewFormList.getReviewForms().size()));
		return new ResponseEntity<>(reviewFormList, HttpStatus.OK);
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ReviewFormVO> createReviewForm(@RequestBody ReviewFormVO reviewFormVO)
			throws InterruptedException {
		ReviewFormVO addedReviewFormVO = reviewFormService.createReviewForm(reviewFormVO);
		return new ResponseEntity<>(addedReviewFormVO, HttpStatus.CREATED);
	}

	@PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ReviewFormVO> updateReviewForm(@RequestBody ReviewFormVO reviewFormVO) {
		ReviewFormVO updatedReviewFormVO = reviewFormService.updateReviewForm(reviewFormVO);
		return new ResponseEntity<>(updatedReviewFormVO, HttpStatus.OK);
	}

	@RequestMapping(value = "/dispatchReviewForms", method = RequestMethod.POST)
	public ResponseEntity<?> bulk(@RequestParam(required = true) PerformanceCycle cycle,
			@RequestParam(required = true) Integer year, @RequestBody(required = false) ArrayList<String> empIdList)
			throws Exception {
		if (null == empIdList || empIdList.isEmpty()) {
			LOGGER.error("Employee Id list not received in request body");
			throw new ValidationException(listOfErrors("employee id list"));
		}
		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@GetMapping(value = "{formId}", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ReviewFormVO> getReviewForm(@PathVariable("formId") String formId) {
		ReviewFormVO reviewFormVO = reviewFormService.getReviewFormById(formId);
		return new ResponseEntity<>(reviewFormVO, HttpStatus.OK);
	}

	@GetMapping(value = "/finalRatingReport")
	public Resource finalRatingReport(
			@RequestParam(value = "performanceCycle", required = true) PerformanceCycle performanceCycle,
			@RequestParam(value = "year", required = true) Integer year) throws Exception {

//		Path reportFilePath = reviewFormService.getFinalRatingReport(performanceCycle, year);
	
		try {
			Path filePath = reviewFormService.getFinalRatingReport(performanceCycle, year);
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new Exception("File not found");
			}
		} catch (MalformedURLException ex) {
			throw new Exception("File not found " + ex);
		}

	}

	/*
	 * @GetMapping(value = "/finalRatingReport") public ResponseEntity<Object>
	 * finalRatingReport(@RequestParam(value = "performanceCycle", required = true)
	 * String performanceCycle,
	 * 
	 * @RequestParam(value = "year", required = true) String year) throws
	 * IOException {
	 * 
	 * File reportFile =
	 * reviewFormService.getFinalRatingReport(performanceCycle,year);
	 * InputStreamResource resource = new InputStreamResource(new
	 * FileInputStream(reportFile));
	 * 
	 * return ResponseEntity.ok() .header(HttpHeaders.CONTENT_DISPOSITION,
	 * "attachment;filename=" + reportFile.getName())
	 * .contentType(MediaType.APPLICATION_OCTET_STREAM)
	 * .contentLength(reportFile.length()) .body(resource);
	 * 
	 * }
	 */
}
