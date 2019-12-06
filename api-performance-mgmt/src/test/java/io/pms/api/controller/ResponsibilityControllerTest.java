package io.pms.api.controller;
//package io.pms.api.test;
//
//import io.pms.api.controllers.ResponsibilityController;
//import io.pms.api.model.Responsibility;
//import io.pms.api.repositories.CommonRepository;
//import io.pms.api.repositories.EmployeeRepository;
//import io.pms.api.repositories.GoalCategoryRepository;
//import io.pms.api.repositories.ResponsibilityRepository;
//import io.pms.api.repositories.RoleTagRepository;
//import io.pms.api.services.GoalCategoryService;
//import io.pms.api.services.RoleTagService;
//import io.pms.api.vo.ResponsibilityVO;
//import org.joda.time.DateTime;
//import org.json.JSONObject;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.http.MediaType;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static io.pms.api.commontest.Util.asJsonString;
//import static io.pms.api.commontest.Util.checkErrorJson;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(value = ResponsibilityController.class, secure = false)
//@ComponentScan({ "io.pms.api.services", "io.pms.api.repositories", "io.pms.api.vo" })
//public class ResponsibilityTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@MockBean
//	MappingMongoConverter mappingMongoConverter;
//
//	@MockBean
//	GoalCategoryRepository goalCategoryRepository;
//
//	@MockBean
//	EmployeeRepository employeeRepository;
//
//	@MockBean
//	RoleTagService roleTagService;
//
//	@MockBean
//	RoleTagRepository roleRepo;
//
//	@MockBean
//	AuthorizationServerEndpointsConfiguration authorizationServerEndpointsConfiguration;
//
//	@MockBean
//	CommonRepository commonRepository;
//
//	@MockBean
//	ResponsibilityRepository responsibilityRepository;
//
//	@MockBean
//	RestTemplateBuilder restTemplateBuilder;
//
//	@InjectMocks
//	GoalCategoryService goalCategoryService = new GoalCategoryService();
//
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//	}
//
//	private Responsibility responsibility() {
//		Responsibility responsibility = Responsibility.builder().responsibilityDescription("responsibility description")
//				.roleId("role ID").build();
//		responsibility.setResponsibilityId("123");
//		responsibility.setModifiedDate(new DateTime().withDate(2024, 1, 1));
//		return responsibility;
//	}
//
//	@Test
//	public void testCreateResponsibility() throws Exception {
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().roleId("123")
//				.responsibilityDescription("description").build();
//		Responsibility responsibility = responsibility();
//
//		Mockito.when(responsibilityRepository.findByRoleIdAndResponsibilityDescription(responsibilityVO.getRoleId(),
//				responsibilityVO.getResponsibilityDescription())).thenReturn(null);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/responsibility")
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();
//		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//		assertEquals("description", resultObject.get("responsibilityDescription"));
//		assertEquals("123", resultObject.get("roleId"));
//	}
//
//	@Test
//	public void testCreateResponsibilityAlreadyExists() throws Exception {
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().roleId("123")
//				.responsibilityDescription("description").build();
//		Responsibility responsibility = responsibility();
//
//		Mockito.when(responsibilityRepository.findByRoleIdAndResponsibilityDescription(responsibilityVO.getRoleId(),
//				responsibilityVO.getResponsibilityDescription())).thenReturn(responsibility);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/responsibility")
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//		checkErrorJson(result, "responsibility", "Bad request, unable to parse field", "responsibility already exists");
//	}
//
//	@Test
//	public void testCreateResponsibilityNoRoleId() throws Exception {
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().responsibilityDescription("description").build();
//		Responsibility responsibility = responsibility();
//
//		Mockito.when(responsibilityRepository.findByRoleIdAndResponsibilityDescription(responsibilityVO.getRoleId(),
//				responsibilityVO.getResponsibilityDescription())).thenReturn(null);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/responsibility")
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//		checkErrorJson(result, "roleId", "Bad request, unable to parse field", "roleId is required");
//	}
//
//	@Test
//	public void testCreateResponsibilityNoRoleDescription() throws Exception {
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().roleId("roleId").build();
//		Responsibility responsibility = responsibility();
//
//		Mockito.when(responsibilityRepository.findByRoleIdAndResponsibilityDescription(responsibilityVO.getRoleId(),
//				responsibilityVO.getResponsibilityDescription())).thenReturn(null);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/responsibility")
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//		checkErrorJson(result, "responsibilityDescription", "Bad request, unable to parse field",
//				"responsibilityDescription is required");
//	}
//
//	@Test
//	public void testGetAllResponsibilities() throws Exception {
//		Responsibility responsibility = responsibility();
//		List<Responsibility> responsibilities = new ArrayList<>();
//		responsibilities.add(responsibility);
//
//		Mockito.when(responsibilityRepository.findAll()).thenReturn(responsibilities);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/responsibility")
//				.contentType(MediaType.APPLICATION_JSON);
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//		assertNotNull(resultObject.get("responsibilities"));
//		assertEquals("1", result.getResponse().getHeader("X-Total-Count"));
//	}
//
//	@Test
//	public void testGetAllResponsibilitiesForRole() throws Exception {
//		Responsibility responsibility = responsibility();
//		List<Responsibility> responsibilities = new ArrayList<>();
//		responsibilities.add(responsibility);
//
//		Mockito.when(responsibilityRepository.findByRoleId(responsibilities.get(0).getRoleId()))
//				.thenReturn(responsibilities);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders
//				.get("/responsibility/role/".concat(responsibilities.get(0).getRoleId()))
//				.contentType(MediaType.APPLICATION_JSON);
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//		assertNotNull(resultObject.get("responsibilities"));
//		assertEquals("1", result.getResponse().getHeader("X-Total-Count"));
//	}
//
//	@Test
//	public void testEditResponsibility() throws Exception {
//		Responsibility responsibility = responsibility();
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().roleId("123")
//				.responsibilityDescription("description").build();
//
//		String responsibilityToBeEdited = "1";
//
//		Mockito.when(responsibilityRepository.findOne(responsibilityToBeEdited)).thenReturn(responsibility);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/responsibility/" + responsibilityToBeEdited)
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//		assertEquals("description", resultObject.get("responsibilityDescription"));
//		assertEquals("123", resultObject.get("roleId"));
//	}
//
//	@Test
//	public void testEditInvalidResponsibility() throws Exception {
//		Responsibility responsibility = responsibility();
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().roleId("123")
//				.responsibilityDescription("description").build();
//
//		String responsibilityToBeEdited = "1";
//
//		Mockito.when(responsibilityRepository.findOne(responsibilityToBeEdited)).thenReturn(null);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/responsibility/" + responsibilityToBeEdited)
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();
//		checkErrorJson(result, "responsibility", "Requested Resource Not Found", "responsibility not found");
//	}
//
//	@Test
//	public void testEditResponsibilityNoDescription() throws Exception {
//		Responsibility responsibility = responsibility();
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().roleId("roleId").build();
//
//		String responsibilityToBeEdited = "1";
//
//		Mockito.when(responsibilityRepository.findOne(responsibilityToBeEdited)).thenReturn(responsibility);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/responsibility/" + responsibilityToBeEdited)
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//		checkErrorJson(result, "responsibilityDescription", "Bad request, unable to parse field",
//				"responsibilityDescription is required");
//	}
//
//	@Test
//	public void testEditResponsibilityNoRoleId() throws Exception {
//		Responsibility responsibility = responsibility();
//		ResponsibilityVO responsibilityVO = ResponsibilityVO.builder().responsibilityDescription("description").build();
//
//		String responsibilityToBeEdited = "1";
//
//		Mockito.when(responsibilityRepository.findOne(responsibilityToBeEdited)).thenReturn(responsibility);
//		Mockito.when(responsibilityRepository.save(responsibility)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/responsibility/" + responsibilityToBeEdited)
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(responsibilityVO));
//
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//		checkErrorJson(result, "roleId", "Bad request, unable to parse field", "roleId is required");
//	}
//
//	@Test
//	public void testDeleteResponsibility() throws Exception {
//		Responsibility responsibility = responsibility();
//		String responsibilityToDelete = "1L";
//
//		Mockito.when(responsibilityRepository.findOne(responsibilityToDelete)).thenReturn(responsibility);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/responsibility/" + responsibilityToDelete)
//				.contentType(MediaType.APPLICATION_JSON);
//		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//	}
//
//	@Test
//	public void testDeleteInvalidResponsibility() throws Exception {
//		String responsibilityToDelete = "1L";
//
//		Mockito.when(responsibilityRepository.findOne(responsibilityToDelete)).thenReturn(null);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/responsibility/" + responsibilityToDelete)
//				.contentType(MediaType.APPLICATION_JSON);
//		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();
//		checkErrorJson(result, "responsibility", "Requested Resource Not Found", "responsibility not found");
//	}
//}
