package io.pms.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.common.Role;
import io.pms.api.services.EmployeeService;
import io.pms.api.vo.EmployeeListVO;
import io.pms.api.vo.EmployeeReviewForm;
import io.pms.api.vo.EmployeeVO;
import io.pms.api.vo.ManagerVO;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping(value = "/{empId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<EmployeeListVO> getEmployee(@PathVariable("empId") String empId) {
		EmployeeListVO employeeListVO = employeeService.getEmployeeByEmpId(empId);
		return new ResponseEntity<>(employeeListVO, HttpStatus.OK);
	}

	@GetMapping(value = "/getAllEmployees", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<EmployeeListVO> getAllEmployee(
			@RequestParam(value = "managerId", required = false) String managerId,
			@RequestParam(value = "duHeadId", required = false) String duHeadId,
			@RequestParam(value = "performanceCycle", required = false) String performanceCycle) throws Exception {
		EmployeeListVO employeeListVO = employeeService.getAllEmployees(managerId, duHeadId, performanceCycle);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("X-Total-Count", String.valueOf(employeeListVO.getEmployees().size()));
		return new ResponseEntity<>(employeeListVO, responseHeaders, HttpStatus.OK);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<EmployeeListVO> editEmployee(@RequestBody EmployeeVO employeeVO) {
		EmployeeListVO editedEmployee = employeeService.editEmployeeByEmpId(employeeVO);
		return new ResponseEntity<>(editedEmployee, HttpStatus.OK);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<EmployeeListVO> addEmployee(@RequestBody EmployeeVO employeeVO) {
		EmployeeListVO successfullEditForEmployees = employeeService.addEmployee(employeeVO);
		return new ResponseEntity<>(successfullEditForEmployees, HttpStatus.CREATED);
	}

	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<EmployeeReviewForm>> allEmployee(
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "managerId", required = false) String managerId,
			@RequestParam(value = "duHeadId", required = false) String duHeadId,
			@RequestParam(value = "formStatus", required = false) String formStatus) throws Exception {
		List<EmployeeReviewForm> employeeListVO = employeeService.allEmployees(year, managerId, duHeadId, formStatus);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("X-Total-Count", String.valueOf(employeeListVO.size()));
		return new ResponseEntity<>(employeeListVO, responseHeaders, HttpStatus.OK);
	}

	@GetMapping(value = "/allDUHeads", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<ManagerVO>> getAllDUHeads() {
		List<ManagerVO> duHeads = employeeService.getAllEmployeeByRole(Role.DU_HEAD);
		return new ResponseEntity<>(duHeads, HttpStatus.OK);
	}

	@GetMapping(value = "/allManagers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<ManagerVO>> getAllManagers() {
		List<ManagerVO> managers = employeeService.getAllEmployeeByRole(Role.MANAGER);
		return new ResponseEntity<>(managers, HttpStatus.OK);
	}

}
