package io.pms.api.controller;
//package io.pms.api.test;
//
//import static io.pms.api.commontest.Util.asJsonString;
//import static io.pms.api.commontest.Util.checkErrorJson;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONObject;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.mock.mockito.SpyBean;
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
//import io.pms.api.controllers.BusinessPhilosophyController;
//import io.pms.api.model.Common;
//import io.pms.api.repositories.CommonRepository;
//import io.pms.api.repositories.EmployeeRepository;
//import io.pms.api.repositories.GoalCategoryRepository;
//import io.pms.api.repositories.ResponsibilityRepository;
//import io.pms.api.repositories.RoleTagRepository;
//import io.pms.api.services.GoalCategoryService;
//import io.pms.api.services.RoleTagService;
//import io.pms.api.vo.BusinessPhilosphyVO;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = BusinessPhilosophyController.class, secure = false)
//@ComponentScan({"io.pms.api.services", "io.pms.api.repositories", "io.pms.api.vo"})
//public class BusinessPhilosophyTest {
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
//    CommonRepository commonRepository;
//
//    @MockBean
//    ResponsibilityRepository responsibilityRepository;
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
//    RestTemplateBuilder restTemplateBuilder;
//    
//    @SpyBean
//    GoalCategoryService goalCategoryService;
//
//    /*@InjectMocks
//    GoalCategoryService goalCategoryService = new GoalCategoryService();*/
//
//    public void setUp() {
//        //MockitoAnnotations.initMocks(this);
//    }
//
//    private List<String> philosophies() {
//        List<String> philosophies = new ArrayList<>();
//        philosophies.add("new competency");
//        return philosophies;
//    }
//
//    @Test
//    public void createBusinessPhilosophies() throws Exception {
//        BusinessPhilosphyVO businessPhilosphyVO = new BusinessPhilosphyVO();
//        businessPhilosphyVO.setPhilosophies(philosophies());
//
//        Mockito.when(commonRepository.findAll()).thenReturn(new ArrayList<>());
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/business-philosophy").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(businessPhilosphyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertEquals(philosophies().get(0), resultObject.getJSONArray("philosophies").get(0));
//    }
//
//    @Test
//    public void createBusinessPhilosophyAlreadyExists() throws Exception {
//        BusinessPhilosphyVO businessPhilosphyVO = new BusinessPhilosphyVO();
//        businessPhilosphyVO.setPhilosophies(philosophies());
//        List<Common> existingCommon = new ArrayList<>();
//        Common common = new Common();
//        common.setPhilosophies(philosophies());
//        existingCommon.add(common);
//
//        Mockito.when(commonRepository.findAll()).thenReturn(existingCommon);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/business-philosophy").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(businessPhilosphyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//        checkErrorJson(result, "businessPhilosophy", "Bad request, unable to parse field", "businessPhilosophy already exists");
//    }
//
//    @Test
//    public void editBusinessPhilosophy() throws Exception {
//        BusinessPhilosphyVO businessPhilosphyVO = new BusinessPhilosphyVO();
//        businessPhilosphyVO.setPhilosophies(philosophies());
//        List<Common> existingCommon = new ArrayList<>();
//        Common common = new Common();
//        common.setPhilosophies(philosophies());
//        existingCommon.add(common);
//
//        Mockito.when(commonRepository.findAll()).thenReturn(existingCommon);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/business-philosophy").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(businessPhilosphyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertEquals(philosophies().get(0), resultObject.getJSONArray("philosophies").get(0));
//    }
//
//    @Test
//    public void editBusinessPhilosophyNoRecord() throws Exception {
//        BusinessPhilosphyVO businessPhilosphyVO = new BusinessPhilosphyVO();
//        businessPhilosphyVO.setPhilosophies(philosophies());
//
//        Mockito.when(commonRepository.findAll()).thenReturn(new ArrayList<>());
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/business-philosophy").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(businessPhilosphyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();
//        checkErrorJson(result, "business philosophies", "Requested Resource Not Found", "business philosophies not found");
//    }
//
//    @Test
//    public void testGetAllBusinessPhilosophies() throws Exception {
//        List<Common> existingCommon = new ArrayList<>();
//        Common common = new Common();
//        common.setPhilosophies(philosophies());
//        existingCommon.add(common);
//
//        Mockito.when(commonRepository.findAll()).thenReturn(existingCommon);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/business-philosophy").contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertNotNull(resultObject.get("philosophies"));
//        assertEquals("1", result.getResponse().getHeader("X-Total-Count"));
//    }
//}
