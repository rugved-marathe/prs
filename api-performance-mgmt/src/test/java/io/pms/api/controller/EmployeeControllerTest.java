package io.pms.api.controller;

import static io.pms.api.commontest.Util.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import io.pms.api.controllers.EmployeeController;
import io.pms.api.exception.AlreadyExistsException;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.services.EmployeeService;
import io.pms.api.vo.EmployeeListVO;
import io.pms.api.vo.EmployeeReviewForm;
import io.pms.api.vo.EmployeeVO;
import io.pms.api.vo.ManagerVO;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EmployeeController.class, secure = false)
public class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MappingMongoConverter mappingMongoConverter;

	@MockBean
	private EmployeeService employeeService;

	@MockBean
	private EmployeeRepository employeeRepository;

	@MockBean
	private AuditingHandler auditingHandler;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

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
		employee.setDateOfLeaving(new DateTime().withDate(2024, 1, 1));
		return employee;
	}

	private EmployeeVO employeeVO() {
		EmployeeVO employeeVO = new EmployeeVO();
		employeeVO.setEmpId("AFT00396");
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

	private EmployeeReviewForm employeeReviewForm() {

		EmployeeReviewForm employeeReviewForm = new EmployeeReviewForm();
		employeeReviewForm.setEmpId("AFT00396");
		employeeReviewForm.setEmpName("Virat Kohli");
		employeeReviewForm.setCurrentMgrId("AFT00002");
		employeeReviewForm.setCurrentMgrName("M S Dhoni");
		employeeReviewForm.setDesignation("Associate");
		employeeReviewForm.setCycle(PerformanceCycle.APRIL);
		employeeReviewForm.setFormId("AFT00553");
		employeeReviewForm.setFormStatus(FormStatus.UNDER_REVIEW);
		return employeeReviewForm;
	}

	private List<EmployeeReviewForm> listOfEmployeeReviewForm() {
		List<EmployeeReviewForm> employeeReviewForms = new ArrayList<EmployeeReviewForm>();
		employeeReviewForms.add(employeeReviewForm());
		return employeeReviewForms;
	}
	
	private EmployeeListVO employeeVOList() {
		List<EmployeeVO> employeeVOs = new ArrayList<EmployeeVO>();
		employeeVOs.add(employeeVO());
		EmployeeListVO employeeListVO = new EmployeeListVO();
		employeeListVO.setEmployees(employeeVOs);
		return employeeListVO;
	}

	private ManagerVO managerVO() {
		ManagerVO managerVO = new ManagerVO();
		managerVO.setId("AFT00396");
		managerVO.setName("Lionel Messi");
		return managerVO;
	}

	@Test
	public void testGetEmpByIdEmployeeController() throws Exception {
		Mockito.when(employeeService.getEmployeeByEmpId(Mockito.anyString())).thenReturn(employeeVOList());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/employee/AFT00396")
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
		assertNotNull(resultObject.getJSONArray("employees").get(0));
		JSONObject employeeObject = (JSONObject) resultObject.getJSONArray("employees").get(0);
		assertNotNull(employeeObject.get("empName"));
		assertEquals("Lionel Messi", employeeObject.get("empName"));
		assertEquals("AFT00396", employeeObject.get("empId"));

	}

	@Test
	public void testEditEmployeeController() throws Exception {
		Mockito.when(employeeService.editEmployeeByEmpId(Mockito.any(EmployeeVO.class))).thenReturn(employeeVOList());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/employee").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(employeeVO()));
		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
		JSONObject employeeObject = (JSONObject) resultObject.getJSONArray("employees").get(0);
		assertEquals("Lionel Messi", employeeObject.get("empName"));
		assertEquals("AFT00396", employeeObject.get("empId"));
	}

	@Test
	public void testGetAllEmployeesController() throws Exception {
		List<Employee> employees = new ArrayList<>();
		employees.add(employee());
		Mockito.when(employeeService.getAllEmployees(null, null, null)).thenReturn(employeeVOList());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/employee/getAllEmployees")
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
		assertNotNull(resultObject.getJSONArray("employees").get(0));
	}

	@Test
	public void testAddEmployeeController() throws Exception {
		Mockito.when(employeeService.addEmployee(Mockito.any(EmployeeVO.class))).thenReturn(employeeVOList());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/employee").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(employeeVO()));
		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();
		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
		assertNotNull(resultObject.getJSONArray("employees").get(0));
		JSONObject employeeObject = (JSONObject) resultObject.getJSONArray("employees").get(0);
	}

	@Test
	public void testGetAllDUHeads() throws Exception {

		Mockito.when(employeeService.getAllEmployeeByRole(Mockito.any())).thenReturn(Arrays.asList(managerVO()));
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/employee/allDUHeads")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is("AFT00396")))
				.andExpect(jsonPath("$[0].name", is("Lionel Messi")));

	}

	@Test
	public void testGetAllManagers() throws Exception {
		List<ManagerVO> managers = new ArrayList<ManagerVO>();
		managers.add(managerVO());

		Mockito.when(employeeService.getAllEmployeeByRole(Mockito.eq(Role.MANAGER))).thenReturn(managers);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/employee/allManagers")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is("AFT00396")))
				.andExpect(jsonPath("$[0].name", is("Lionel Messi")));
	}

	@Test
	public void testAddEmployeeController_whenEmployeeAlreadyExists() throws Exception {
		Mockito.when(employeeService.addEmployee(Mockito.any(EmployeeVO.class)))
				.thenThrow(AlreadyExistsException.class);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/employee").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(employeeVO()));
		mockMvc.perform(requestBuilder).andExpect(status().isConflict()).andReturn();
	}

	@Test
	public void testGetAllController() throws Exception {

		Mockito.when(employeeService.allEmployees(2018, "AFT00021", "AFT00129", FormStatus.COMPLETED.toString()))
				.thenReturn(listOfEmployeeReviewForm());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/employee/all")
				.contentType(MediaType.APPLICATION_JSON).param("year", "2018").param("managerId", "AFT00021").param("duHeadId", "AFT00129").param("formStatus", FormStatus.COMPLETED.toString());
		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		JSONArray jsonarray = new JSONArray(result.getResponse().getContentAsString());
		assertNotNull(jsonarray.get(0));
	}

}
