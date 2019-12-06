
package io.pms.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.pms.api.services.UploadCSVService;
import io.pms.api.vo.GenericResponse;

@RestController
public class UploadCSVController {
	
	@Autowired
	UploadCSVService uploadCsvService;
	
	@PostMapping(value="/upload")
	public ResponseEntity<GenericResponse> upload(@RequestParam("file") MultipartFile file) { 
		List<String> unProcessedEmpIds = uploadCsvService.processFile(file);
		
		if(!unProcessedEmpIds.isEmpty()) {
			GenericResponse response = new GenericResponse();
			response.setMessage("Following Employee Ids are not processed: " + unProcessedEmpIds.toString());
			return new ResponseEntity<GenericResponse>(response, HttpStatus.OK);
		}
			
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
