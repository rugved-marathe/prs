package io.pms.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Status;
import io.pms.api.exception.NotFoundException;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.services.UserService;
import io.pms.api.vo.EmployeeVO;

@RunWith(SpringRunner.class)
public class UserServiceTest {

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private AuditingHandler auditingHandler;

	@MockBean
	private EmployeeRepository employeeRepository;

	@InjectMocks
	private UserService userService;
	
	@MockBean
	private Authentication authentication;
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	private Employee employee() {
		Employee employee = new Employee();
		employee.setEmpId("AFT00396");
		employee.setEmpName("Lionel Messi");
		employee.setEmail("abc@abc.com");
		employee.setCurrentMgrId("AFT00002");
		employee.setCurrentDUHeadId("AFT00001");
		employee.setDateOfJoining(new DateTime().withDate(2024, 1, 1));
		employee.setStatus(Status.ACTIVE);
		employee.setPerformanceCycle(PerformanceCycle.APRIL);
		employee.setCurrentProject(Arrays.asList("PMS"));
		return employee;
	}

	private Employee mockEmployee() {
		Employee employee = new Employee();
		employee.setEmpId("AFT00003");
		employee.setEmpName("John Doe");
		employee.setEmail("john.doe@afourtech.com");
		employee.setCurrentMgrId("AFT00002");
		employee.setCurrentDUHeadId("AFT00001");
		employee.setDateOfJoining(new DateTime().withDate(2024, 1, 1));
		employee.setStatus(Status.ACTIVE);
		employee.setPerformanceCycle(PerformanceCycle.APRIL);
		employee.setCurrentProject(Arrays.asList("PMS"));
		employee.setDateOfLeaving(new DateTime().withDate(2024, 1, 1));
		return employee;
	}

	private EmployeeVO employeeVO() {
		EmployeeVO employeeVO = new EmployeeVO();
		employeeVO.setEmpId("AFT00396");
		employeeVO.setEmail("abc@abc.com");
		employeeVO.setEmpName("Lionel Messi");
		employeeVO.setCurrentMgrId("AFT00002");
		employeeVO.setCurrentDUHeadId("AFT00001");
		employeeVO.setDateOfJoining(new DateTime().withDate(2024, 1, 1).toString());
		employeeVO.setStatus(Status.ACTIVE.toString());
		employeeVO.setPerformanceCycle(PerformanceCycle.APRIL);
		employeeVO.setCurrentProject(Arrays.asList("PMS"));
		employeeVO.setDateOfLeaving(new DateTime().withDate(2024, 1, 1).toString());
		return employeeVO;
	}
	
	@Test
	public void whenGetUserProfileIsTriggered_thenReturnProfileInformation() {
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(authentication.getName()).thenReturn("john.doe@afourtech.com");
		
		EmployeeVO employeeVO = userService.getUserProfile(authentication);
		
		assertNotNull(employeeVO);
		assertEquals("AFT00003", employeeVO.getEmpId());
		assertEquals("John Doe", employeeVO.getEmpName());
		assertEquals("john.doe@afourtech.com", employeeVO.getEmail());
	}

	@Test
	public void testGetUserProfile() {
		
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee());
		
		EmployeeVO actualEmployeeVO = userService.getUserProfile(authentication);
		
		assertNotNull(actualEmployeeVO);
		assertEquals(employeeVO().getEmpId(), actualEmployeeVO.getEmpId());
	}
	@Test(expected = NotFoundException.class)
	public void whenGetUserProfileIsTriggered_thenReturnEmployeeNotFound() {
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(null);
		Mockito.when(authentication.getName()).thenReturn("john.doe@afourtech.com");

		userService.getUserProfile(authentication);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetUserProfile_whenEmployeeNotPresent() {
		
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(null);
		
		userService.getUserProfile(authentication);
		
	}
}
