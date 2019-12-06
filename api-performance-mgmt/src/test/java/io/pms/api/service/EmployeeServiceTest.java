package io.pms.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.test.context.junit4.SpringRunner;

import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import io.pms.api.exception.AlreadyExistsException;
import io.pms.api.exception.Errors;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.services.EmployeeService;
import io.pms.api.vo.EmployeeListVO;
import io.pms.api.vo.EmployeeReviewForm;
import io.pms.api.vo.EmployeeVO;
import io.pms.api.vo.ManagerVO;

@RunWith(SpringRunner.class)
public class EmployeeServiceTest {

	@MockBean
	private MappingMongoConverter mappingMongoConverter;

	@SpyBean
	private EmployeeService employeeService;

	@MockBean
	private EmployeeRepository employeeRepository;

	@MockBean
	private AuthorizationServerTokenServices tokenServices;

	@MockBean
	private AuditingHandler auditingHandler;

	@MockBean
	private MongoTemplate mongoTemplate;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private Employee employee() {
		Employee employee = new Employee();
		employee.setEmpId("AFT00396");
		employee.setEmpName("Lionel Messi");
		employee.setCurrentMgrId("AFT00002");
		employee.setCurrentDUHeadId("AFT00001");
		employee.setDateOfJoining(new DateTime().withDate(2024, 1, 1));
		employee.setStatus(Status.ACTIVE);
		employee.setPerformanceCycle(PerformanceCycle.APRIL);
		employee.setCurrentProject(Arrays.asList("PMS"));
		return employee;
	}

	private EmployeeVO employeeVO() {
		EmployeeVO employeeVO = new EmployeeVO();
		employeeVO.setEmpId("AFT00396");
		employeeVO.setEmail("abc@abc.com");
		employeeVO.setEmpName("Lionel Messi");
		employeeVO.setCurrentMgrId("AFT00002");
		employeeVO.setCurrentDUHeadId("AFT00001");
		employeeVO.setDateOfJoining("01/01/2001");
		employeeVO.setStatus(Status.ACTIVE.toString());
		employeeVO.setPerformanceCycle(PerformanceCycle.APRIL);
		employeeVO.setCurrentProject(Arrays.asList("PMS"));
		employeeVO.setDateOfLeaving(new DateTime().withDate(2024, 1, 1).toString());
		return employeeVO;
	}

	private EmployeeReviewForm employeeReviewForm() {

		EmployeeReviewForm employeeReviewForm = new EmployeeReviewForm();
		employeeReviewForm.setEmpId("AFT00396");
		employeeReviewForm.setEmpName("Virat Kohli");
		employeeReviewForm.setCurrentMgrId("AFT00002");
		employeeReviewForm.setCurrentMgrName("M S Dhoni");
		employeeReviewForm.setDesignation("Associate");
		employeeReviewForm.setCycle(PerformanceCycle.APRIL);
		employeeReviewForm.setFormId(UUID.randomUUID().toString());
		employeeReviewForm.setFormStatus(FormStatus.UNDER_REVIEW);
		return employeeReviewForm;
	}

	private List<EmployeeReviewForm> listOfEmployeeReviewForm() {
		List<EmployeeReviewForm> employeeReviewForms = new ArrayList<EmployeeReviewForm>();
		employeeReviewForms.add(employeeReviewForm());
		return employeeReviewForms;
	}

	@Test
	public void testGetEmpByEmpId() throws Exception {
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee());
		EmployeeListVO employeeVOList = employeeService.getEmployeeByEmpId(Mockito.anyString());
		assertEquals(employeeVOList.getEmployees().get(0).getEmpId(), employeeVO().getEmpId());
	}

	@Test(expected = NotFoundException.class)
	public void testGetEmpByEmpId_whenNullEmployee() throws Exception {
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);
		employeeService.getEmployeeByEmpId(Mockito.anyString());
	}

	@Test
	public void testGetEmpByEmpId_whenNullEmpId() throws Exception {
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setEmpId(null);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);

		exceptionRule.expect(ValidationException.class);
		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testGetAllEmployees_ByManagerId() throws Exception {
		List<Employee> employees = new ArrayList<>();
		employees.add(employee());
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee());
		Mockito.when(employeeRepository.findAllByCurrentMgrId(Mockito.anyString())).thenReturn(employees);

		EmployeeListVO employeesUnderManager = employeeService.getAllEmployees("managerId", null, null);
		assertEquals(employeesUnderManager.getEmployees().get(0).getEmpId(), employeeVO().getEmpId());

	}

	@Test
	public void testGetAllEmployees_ByDUHeadId() throws Exception {
		List<Employee> employees = new ArrayList<>();
		employees.add(employee());
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee());
		Mockito.when(employeeRepository.findAllByCurrentDUHeadId(Mockito.anyString())).thenReturn(employees);
		EmployeeListVO employeesUnderDUHead = employeeService.getAllEmployees(null, "duheadId", null);
		assertEquals(employeesUnderDUHead.getEmployees().get(0).getEmpId(), employeeVO().getEmpId());

	}

	@Test
	public void testGetAllEmployees_ByDUHeadId_whenInvalidDuheadId() throws Exception {
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);

		exceptionRule.expect(NotFoundException.class);
		employeeService.getAllEmployees(null, "invalidDUHeadId", null);
	}

	@Test
	public void testGetAllEmployees_ByManagerId_whenInvalidManagerId() throws Exception {
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);
		exceptionRule.expect(NotFoundException.class);

		employeeService.getAllEmployees("invalidManagerId", null, null);
	}

	@Test
	public void testGetAllEmployees_ByPerformanceCyclewhenInvalidPerformanceCycle() throws Exception {
		exceptionRule.expect(NotFoundException.class);

		employeeService.getAllEmployees(null, null, "invalidPerformanceCycle");
	}

	@Test
	public void testGetAllEmployees_ByPerformanceCycle() throws Exception {

		PerformanceCycle arr[] = PerformanceCycle.values();
		List<Employee> employees = new ArrayList<>();
		employees.add(employee());
		Mockito.when(employeeRepository.findAllByPerformanceCycle(Mockito.anyString())).thenReturn(employees);
		EmployeeListVO employeesUnderManager = employeeService.getAllEmployees(null, null, arr[0].toString());
		assertEquals(employeesUnderManager.getEmployees().get(0).getEmpId(), employeeVO().getEmpId());

	}

	@Test
	public void testAddEmployee() throws Exception {

		Employee employee = employee();
		EmployeeVO employeeVO = employeeVO();
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);
		Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
		EmployeeListVO savedEmployeeVOList = employeeService.addEmployee(employeeVO);
		assertEquals(savedEmployeeVOList.getEmployees().get(0).getEmpId(), employeeVO().getEmpId());

	}

	@Test
	public void testAddEmployee_whenDuplicateEmailId() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(AlreadyExistsException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullEmpId() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setEmpId(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullEmail() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setEmail(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullDUHeadId() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setCurrentDUHeadId(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullManagerId() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setCurrentMgrId(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullJoiningDate() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setDateOfJoining(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullEmpName() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setEmpName(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullStatus() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setStatus(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenInvalidStatus() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setStatus("invalidStatus");
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenNullPerformanceCycle() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(NullPointerException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setPerformanceCycle(null);
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testAddEmployee_whenPerformanceCycle() throws Exception {

		Employee employee = employee();
		exceptionRule.expect(ValidationException.class);
		List<Errors> errorList = new ArrayList<Errors>();
		Errors errors = new Errors("source", "errorMessage", "detailedMessage");
		errorList.add(errors);
		EmployeeVO employeeVO = employeeVO();
		employeeVO.setStatus("invalidPerformanceCycle");
		Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(employee);

		employeeService.addEmployee(employeeVO);
	}

	@Test
	public void testEditEmployeeByEmpId() throws Exception {

		Employee employee = employee();
		EmployeeVO employeeVO = employeeVO();

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee);
		Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
		EmployeeListVO savedEmployeeVOList = employeeService.editEmployeeByEmpId(employeeVO);

		assertEquals(savedEmployeeVOList.getEmployees().get(0).getEmpId(), employeeVO().getEmpId());

	}

	@Test(expected = NotFoundException.class)
	public void testEditEmployeeByEmpId_whenEmployeeNotFound() throws Exception {

		Employee employee = employee();
		EmployeeVO employeeVO = employeeVO();

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);
		Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
		employeeService.editEmployeeByEmpId(employeeVO);
	}

	@Test
	public void testGetAllEmployees_whenDuheadIdIsNullManagerIdIsNullAndPerformanceCycleIsNull() throws Exception {

		List<Employee> employees = new ArrayList<>();
		employees.add(employee());

		Mockito.when(employeeRepository.findAll()).thenReturn(employees);
		EmployeeListVO allEmployees = employeeService.getAllEmployees(null, null, null);

		assertNotNull(allEmployees);
		assertEquals(allEmployees.getEmployees().get(0).getEmpId(), employeeVO().getEmpId());

	}

	@Test
	public void testGetAllAllEmployeeByRoles_whenRoleIsDUhead() {

		List<Employee> managers = new ArrayList<>();
		managers.add(employee());

		Mockito.when(employeeRepository.findAllByRoles(Mockito.eq(Role.DU_HEAD))).thenReturn(managers);
		List<ManagerVO> allDUHeads = employeeService.getAllEmployeeByRole(Role.DU_HEAD);

		assertNotNull(allDUHeads);
		assertEquals(allDUHeads.get(0).getId(), employee().getEmpId());

	}

	@Test
	public void testGetAllAllEmployeeByRoles_whenRoleIsManager() {

		List<Employee> managers = new ArrayList<>();
		managers.add(employee());

		Mockito.when(employeeRepository.findAllByRoles(Mockito.eq(Role.MANAGER))).thenReturn(managers);
		List<ManagerVO> allManagers = employeeService.getAllEmployeeByRole(Role.MANAGER);

		assertNotNull(allManagers);
		assertEquals(allManagers.get(0).getId(), employee().getEmpId());

	}

	@Test(expected = PMSAppException.class)
	public void testGetAllAllEmployeeByRoles_whenEmployeeIsNotFound() {

		Mockito.when(employeeRepository.findAllByRoles(Mockito.eq(Role.MANAGER))).thenReturn(null);
		employeeService.getAllEmployeeByRole(Role.MANAGER);

	}

}
