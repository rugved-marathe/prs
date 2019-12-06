package io.pms.api.controller;
//package io.pms.api.test;
//
//import io.pms.api.controllers.CommonCompetencyController;
//import io.pms.api.model.Common;
//import io.pms.api.repositories.CommonRepository;
//import io.pms.api.repositories.EmployeeRepository;
//import io.pms.api.repositories.GoalCategoryRepository;
//import io.pms.api.repositories.ResponsibilityRepository;
//import io.pms.api.repositories.RoleTagRepository;
//import io.pms.api.services.GoalCategoryService;
//import io.pms.api.services.RoleTagService;
//import io.pms.api.vo.CommonCompetencyVO;
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
//@WebMvcTest(value = CommonCompetencyController.class, secure = false)
//@ComponentScan({"io.pms.api.services", "io.pms.api.repositories", "io.pms.api.vo"})
//public class CommonCompetencyTest {
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
//    private List<String> competencies() {
//        List<String> competencies = new ArrayList<>();
//        competencies.add("new competency");
//        return competencies;
//    }
//
//    @Test
//    public void createCommonCompetency() throws Exception {
//        CommonCompetencyVO commonCompetencyVO = new CommonCompetencyVO();
//        commonCompetencyVO.setCompetencies(competencies());
//
//        Mockito.when(commonRepository.findAll()).thenReturn(new ArrayList<>());
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/common-competency").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(commonCompetencyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertEquals(competencies().get(0), resultObject.getJSONArray("competencies").get(0));
//    }
//
//    @Test
//    public void createCommonCompetencyAlreadyExists() throws Exception {
//        CommonCompetencyVO commonCompetencyVO = new CommonCompetencyVO();
//        commonCompetencyVO.setCompetencies(competencies());
//        List<Common> existingCommon = new ArrayList<>();
//        Common common = new Common();
//        common.setCompetencies(competencies());
//        existingCommon.add(common);
//
//        Mockito.when(commonRepository.findAll()).thenReturn(existingCommon);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/common-competency").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(commonCompetencyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
//        checkErrorJson(result, "commonCompetency", "Bad request, unable to parse field", "commonCompetency already exists");
//    }
//
//    @Test
//    public void editCommonCompetency() throws Exception {
//        CommonCompetencyVO commonCompetencyVO = new CommonCompetencyVO();
//        commonCompetencyVO.setCompetencies(competencies());
//        List<Common> existingCommon = new ArrayList<>();
//        Common common = new Common();
//        common.setCompetencies(competencies());
//        existingCommon.add(common);
//
//        Mockito.when(commonRepository.findAll()).thenReturn(existingCommon);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/common-competency").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(commonCompetencyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertEquals(competencies().get(0), resultObject.getJSONArray("competencies").get(0));
//    }
//
//    @Test
//    public void editCommonCompetencyNoRecord() throws Exception {
//        CommonCompetencyVO commonCompetencyVO = new CommonCompetencyVO();
//        commonCompetencyVO.setCompetencies(competencies());
//
//        Mockito.when(commonRepository.findAll()).thenReturn(new ArrayList<>());
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/common-competency").contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(commonCompetencyVO));
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();
//        checkErrorJson(result, "common competencies", "Requested Resource Not Found", "common competencies not found");
//    }
//
//    @Test
//    public void testGetAllCommonCompetencies() throws Exception {
//        List<Common> existingCommon = new ArrayList<>();
//        Common common = new Common();
//        common.setCompetencies(competencies());
//        existingCommon.add(common);
//
//        Mockito.when(commonRepository.findAll()).thenReturn(existingCommon);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/common-competency").contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
//        JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());
//        assertNotNull(resultObject.get("competencies"));
//        assertEquals("1", result.getResponse().getHeader("X-Total-Count"));
//    }
//}
