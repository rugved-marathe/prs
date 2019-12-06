package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.pms.api.exception.NotFoundException;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.vo.EmployeeVO;

@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private EmployeeRepository employeeRepository;

	public EmployeeVO getUserProfile(Authentication authentication) {
		String email = authentication.getName();
		Employee employee = employeeRepository.findByEmail(email);
		
		if (null == employee) {
			LOGGER.debug("employee not found for the email id : {}", email);
			throw new NotFoundException(listOfErrors("Failed to find information for " + email));
		}
		
		EmployeeVO employeeVO = new EmployeeVO();
		employeeVO.setEmpId(employee.getEmpId());
		employeeVO.setEmpName(employee.getEmpName());
		employeeVO.setEmail(employee.getEmail());
		employeeVO.setProfileImage(employee.getProfileImage());
		employeeVO.setRoles(employee.getRoles());
		
		return employeeVO;
	}
}
