package io.pms.api.services;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.*;
import static io.pms.api.common.ErrorType.RESOURCE_NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.pms.api.common.Constants;
import io.pms.api.common.ErrorType;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.exception.Errors;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.vo.EmployeeVO;

@Service
public class UploadCSVService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadCSVService.class);
	
	@Autowired
	private EmployeeService employeeService;
	
	public List<String> processFile(MultipartFile file) {
		List<String> unProcessedEmpIds = new LinkedList<>();

		List<Errors> errorList = validateFile(file);
		if (!errorList.isEmpty()) {
			LOGGER.debug("Error occurred while validating input file");
			throw new ValidationException(errorList);
		}
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));) {
			bufferedReader.lines().skip(1).forEach(line -> {
				String[] records = parseRecord(line);
				
				if (23 == records.length) {
					try {
						EmployeeVO employeeVO = createEmployeeVO(records);
						if (0 == Integer.valueOf(records[22]))
							employeeService.addEmployee(employeeVO);
						else
							employeeService.editEmployeeByEmpId(employeeVO);

					} catch (Exception e) {
						LOGGER.error("Exception occurred while processing employee with id {}", records[0], e);
						unProcessedEmpIds.add(records[0]);
					}
				} else
					unProcessedEmpIds.add(records[0]);
			});
		} catch (IOException e) {
			LOGGER.error("IO Error occurred while processing input file: {}", file.getName(), e);
			Errors errors = new Errors("IO", RESOURCE_NOT_FOUND.getErrorMessage(),
					"IO Error occurred while processing input file");
			throw new PMSAppException(Arrays.asList(errors));
		}

		LOGGER.debug("Unprocessed records: {}", unProcessedEmpIds);
		return unProcessedEmpIds;
	}

	
	private List<Errors> validateFile(MultipartFile file) {
		List<Errors> errorList = new ArrayList<>();
		
		if(null != file && !file.isEmpty()) {
			if (!file.getOriginalFilename().contains(".csv")) {
				createErrorList("Input file extention", ErrorType.BAD_REQUEST.getErrorMessage(), INVALID_STATUS, errorList);
			}
			 
		}else {
			createErrorList("Input file", ErrorType.BAD_REQUEST.getErrorMessage(), SOURCE_REQUIRED, errorList);
		}
		return errorList;
	}
	
	private String[] parseRecord(String line) {
		String[] records = new String[0];
		if(StringUtils.isNotBlank(line))
			records = line.split(Constants.COMMA);
		LOGGER.debug("Processed line records: {}", Arrays.toString(records));
		return records;
	}
	
	
	private EmployeeVO createEmployeeVO(String[] records)
	{
		//Parse current projects
		List<String> currentProjects = new LinkedList<>();
		if(StringUtils.isNotBlank(records[7]))
			currentProjects = Arrays.stream(records[7].split(Constants.SEMICOLON)).collect(Collectors.toList());
		
		//Parse Roles
		Set<Role> employeeRoles = new HashSet<>();
		employeeRoles.add(Role.EMPLOYEE);
		
		if(Constants.ONE.equals(records[19]))
			employeeRoles.add(Role.DU_HEAD);
		if(Constants.ONE.equals(records[20]))
			employeeRoles.add(Role.MANAGER);
		if(Constants.ONE.equals(records[21]))
			employeeRoles.add(Role.HR);
		
		EmployeeVO employeeVO = new EmployeeVO();
		employeeVO.setEmpId(records[0]);
		employeeVO.setEmpName(records[1]);
		employeeVO.setStatus("ACTIVE");
		employeeVO.setCoverImage(records[4]);
		employeeVO.setProfileImage(records[17]);
		employeeVO.setCurrentDUHeadId(records[5]);
		employeeVO.setCurrentMgrId(records[6]);
		employeeVO.setCurrentProject(currentProjects);
		employeeVO.setDateOfJoining(records[8]);
		employeeVO.setDateOfLeaving(records[9]);
		employeeVO.setDesignation(records[10]);
		employeeVO.setEmail(records[11]);
		employeeVO.setHighestEducation(records[13]);
		employeeVO.setFatherName(records[12]);
		employeeVO.setMotherName(records[14]);
//		employeeVO.setPerformanceCycle(records[16]);
		employeeVO.setPerformanceCycle(PerformanceCycle.valueOf(records[16]));
		employeeVO.setRoles(employeeRoles);
		employeeVO.setAddress(records[3]);
		return employeeVO;
	}

}