package io.pms.api.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

import io.pms.api.services.GoalService;
import io.pms.api.vo.GenericResponse;
import io.pms.api.vo.GoalListVO;
import io.pms.api.vo.GoalVO;

@RestController
@RequestMapping(value = "/goal")
public class GoalController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GoalController.class);
	@Autowired
	private GoalService goalService;

	/**
	 * @param goalVOList
	 * @return GoalVO
	 */

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<GoalVO> addGoals(@RequestBody GoalVO goalVO) {
		LOGGER.debug("add goal controller");
		GoalVO createdGoalVO = goalService.addGoal(goalVO);
		return new ResponseEntity<>(createdGoalVO, HttpStatus.CREATED);
	}

	@PostMapping(value = "/addMultipleGoal", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> addBulkGoalData(@RequestBody List<GoalVO> goals) {
		LOGGER.debug("Multiple goal added at a time");
		goalService.addMultipleGoal(goals);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * @param empId
	 * @param performanceCycle
	 * @param year
	 * @return GoalListVO
	 */
	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<GoalListVO> getGoals(@RequestParam(required = true) String empId,
			@RequestParam(required = true) String performanceCycle, @RequestParam(required = true) Integer year) {
		LOGGER.debug("fetch goal controller");
		GoalListVO goalListVO = goalService.getGoals(empId, performanceCycle, year);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("X-Total-Count", String.valueOf(goalListVO.getGoals().size()));
		return new ResponseEntity<>(goalListVO, HttpStatus.OK);
	}

	/**
	 * @param goalId
	 * @return HttpStatus.NO_CONTENT only
	 */
	@DeleteMapping
	public ResponseEntity<?> deleteGoal(@RequestParam(required = true) String goalId) {
		LOGGER.debug("delete goal controller");
		goalService.deleteGoals(goalId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * @param goalVO
	 * @return GoalVO
	 */
	@PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<GoalVO> editGoal(@RequestBody GoalVO goalVO) {
		LOGGER.debug("edit goal controller");
		GoalVO editedGoalVO = goalService.editGoal(goalVO);
		return new ResponseEntity<>(editedGoalVO, HttpStatus.CREATED);
	}

	@PostMapping(value = "/addPreviousGoals")
	public ResponseEntity<GenericResponse> addPreviousGoals(@RequestParam("file") MultipartFile file) {
		List<String> unProcessedEmpIds = goalService.addPreviousGoals(file);

		if (!unProcessedEmpIds.isEmpty()) {
			GenericResponse response = new GenericResponse();
			response.setMessage("Following Employee Ids are not processed: " + unProcessedEmpIds.toString());
			return new ResponseEntity<GenericResponse>(response, HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
