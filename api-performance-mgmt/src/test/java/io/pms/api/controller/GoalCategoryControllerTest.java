package io.pms.api.controller;
//package io.pms.api.test;
//
//import io.pms.api.controllers.GoalCategoryController;
//import io.pms.api.model.GoalCategory;
//import io.pms.api.repositories.CommonRepository;
//import io.pms.api.repositories.EmployeeRepository;
//import io.pms.api.repositories.GoalCategoryRepository;
//import io.pms.api.repositories.ResponsibilityRepository;
//import io.pms.api.repositories.RoleTagRepository;
//import io.pms.api.services.GoalCategoryService;
//import io.pms.api.services.RoleTagService;
//import io.pms.api.vo.GoalCategoryVO;
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
//@WebMvcTest(value = GoalCategoryController.class, secure = false)
//@ComponentScan({"io.pms.api.services", "io.pms.api.repositories", "io.pms.api.vo"})
//public class GoalCategoryTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    MappingMongoConverter mappingMongoConverter;
//
//    @MockBean
//    GoalCategoryRepository goalCategoryRepository;
//    
//    @MockBean
//    EmployeeRepository employeeRepository;
//    
//    @MockBean
//    RoleTagService roleTagService;
//    
//    @MockBean
//    RoleTagRepository roleRepo;
//    
//    @MockBean
//    AuthorizationServerEndpointsConfiguration authorizationServerEndpointsConfiguration;
//
//    @MockBean
//    CommonRepository commonRepository;
//
//    @MockBean
//    ResponsibilityRepository responsibilityRepository;
//
//    @MockBean
//    RestTemplateBuilder restTemplateBuilder;
//
//    @InjectMocks
//    GoalCategoryService goalCategoryService = new GoalCategoryService();
//
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    private GoalCategory goalCategory() {
//        GoalCategory goalCategory = GoalCategory.builder()
//                .goalCategoryId("1234")
//                .goalCategory("new goal category")
//                .build();
//        goalCategory.setModifiedDate(new DateTime().withDate(2024, 1,1));
//        return goalCategory;
//    }
//
//    @Test
//    public void testCreateGoalCategory() throws Exception {
//        GoalCategoryVO goalCategoryVO = GoalCategoryVO.builder().goalCategory("new goal category").build();
//        GoalCategory goalCategory = goalCategory();
//
//        Mockito.when(goalCategoryRepository.save(goalCategory)).thenReturn(goalCategory);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/goal-categories")
//                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(goalCategoryVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertEquals("new goal category", resultObject.get("goalCategory"));
//    }
//
//    @Test
//    public void testCreateGoalCategoryNoName() throws Exception {
//        GoalCategoryVO goalCategoryVO = new GoalCategoryVO();
//        goalCategoryVO.setGoalCategory(null);
//        GoalCategory goalCategory = goalCategory();
//
//        Mockito.when(goalCategoryRepository.save(goalCategory)).thenReturn(goalCategory);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/goal-categories")
//                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(goalCategoryVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//        checkErrorJson(result,"goalCategory", "Bad request, unable to parse field", "goalCategory is required");
//    }
//
//    @Test
//    public void testCreateGoalCategoryAlreadyExists() throws Exception {
//        GoalCategoryVO goalCategoryVO = new GoalCategoryVO();
//        goalCategoryVO.setGoalCategory("goal catedory");
//        GoalCategory goalCategory = goalCategory();
//
//        Mockito.when(goalCategoryRepository.findByGoalCategory(goalCategoryVO.getGoalCategory())).thenReturn(goalCategory);
//        Mockito.when(goalCategoryRepository.save(goalCategory)).thenReturn(goalCategory);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/goal-categories")
//                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(goalCategoryVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//        checkErrorJson(result,"goalCategory", "Bad request, unable to parse field", "goalCategory already exists");
//    }
//
//    @Test
//    public void testGetConditions() throws Exception {
//        GoalCategory goalCategory = goalCategory();
//        List<GoalCategory> goalCategories = new ArrayList<>();
//        goalCategories.add(goalCategory);
//
//        Mockito.when(goalCategoryRepository.findAll()).thenReturn(goalCategories);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/goal-categories").contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertNotNull(resultObject.get("goalCategories"));
//        assertEquals("1", result.getResponse().getHeader("X-Total-Count"));
//    }
//
//    @Test
//    public void testEditGoalCategory() throws Exception {
//        GoalCategory goalCategory = goalCategory();
//
//        GoalCategoryVO goalCategoryVO = new GoalCategoryVO();
//        goalCategoryVO.setGoalCategory("edited goal category");
//
//        String goalCategoryToEdit = "1";
//
//        Mockito.when(goalCategoryRepository.findOne(goalCategoryToEdit)).thenReturn(goalCategory);
//        Mockito.when(goalCategoryRepository.save(goalCategory)).thenReturn(goalCategory);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/goal-categories/" + goalCategoryToEdit)
//                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(goalCategoryVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertEquals("edited goal category", resultObject.get("goalCategory"));
//    }
//
//    @Test
//    public void testEditGoalCategoryInvalidId() throws Exception {
//        GoalCategory goalCategory = goalCategory();
//
//        GoalCategoryVO goalCategoryVO = new GoalCategoryVO();
//        goalCategoryVO.setGoalCategory("edited goal category");
//
//        String goalCategoryToEdit = "1";
//
//        Mockito.when(goalCategoryRepository.findOne(goalCategoryToEdit)).thenReturn(null);
//        Mockito.when(goalCategoryRepository.save(goalCategory)).thenReturn(goalCategory);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/goal-categories/" + goalCategoryToEdit)
//                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(goalCategoryVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();
//        checkErrorJson(result, "goalCategory", "Requested Resource Not Found", "goalCategory not found");
//    }
//
//    @Test
//    public void testEditGoalCategoryInvalidRequest() throws Exception {
//        GoalCategory goalCategory = goalCategory();
//
//        GoalCategoryVO goalCategoryVO = new GoalCategoryVO();
//        goalCategoryVO.setGoalCategory(null);
//
//        String goalCategoryToEdit = "1";
//
//        Mockito.when(goalCategoryRepository.findOne(goalCategoryToEdit)).thenReturn(goalCategory);
//        Mockito.when(goalCategoryRepository.save(goalCategory)).thenReturn(goalCategory);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/goal-categories/" + goalCategoryToEdit)
//                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(goalCategoryVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//        checkErrorJson(result, "goalCategory", "Bad request, unable to parse field", "goalCategory is required");
//    }
//
//    @Test
//    public void testDeleteGoalCategory() throws Exception {
//        GoalCategory goalCategory = goalCategory();
//        String goalCategoryToDelete = "1L";
//
//        Mockito.when(goalCategoryRepository.findOne(goalCategoryToDelete)).thenReturn(goalCategory);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/goal-categories/" + goalCategoryToDelete).contentType(MediaType.APPLICATION_JSON);
//        mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//    }
//
//    @Test
//    public void testDeleteInvalidGoalCategory() throws Exception {
//        String goalCategoryToDelete = "1L";
//
//        Mockito.when(goalCategoryRepository.findOne(goalCategoryToDelete)).thenReturn(null);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/goal-categories/" + goalCategoryToDelete).contentType(MediaType.APPLICATION_JSON);
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();
//        checkErrorJson(result, "goalCategory", "Requested Resource Not Found", "goalCategory not found");
//    }
//}
