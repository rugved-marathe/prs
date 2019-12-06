package io.pms.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.pms.api.common.Status;
import io.pms.api.commontest.Util;
import io.pms.api.controllers.RoleTagController;
import io.pms.api.exception.AlreadyDeletedEntityException;
import io.pms.api.model.CommonTag;
import io.pms.api.services.RoleTagService;
import io.pms.api.vo.RoleTagListVO;
import io.pms.api.vo.RoleTagVO;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RoleTagController.class, secure = false)
public class RoleTagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private RoleTagService roleTagService;

	@MockBean
	private AuditingHandler auditingHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	private RoleTagVO roleTagVO() {
		List<CommonTag> mockRoleResponsibilities = new ArrayList<>();
		List<CommonTag> mockCommonCompetencies = new ArrayList<>();
		List<CommonTag> mockBusinessPhilosophies = new ArrayList<>();
		RoleTagVO tagVO = new RoleTagVO();
		tagVO.setId("5bfe685730642420c4e1da76");
		tagVO.setTagName("Mock Role Tag");
		tagVO.setDescription("");
		tagVO.setRoleResponsibilities(mockRoleResponsibilities);
		tagVO.setCommonCompetencies(mockCommonCompetencies);
		tagVO.setBusinessPhilosophies(mockBusinessPhilosophies);
		tagVO.setModifiedDate(DateTime.parse("2018-11-30T00:00:00"));
		tagVO.setModifiedBy("john.doe@afourtech.com");
		tagVO.setStatus(Status.ACTIVE);
		return tagVO;
	}

	@Test
	public void testGetAllTags() {
		RoleTagListVO tagListVO = new RoleTagListVO();
		tagListVO.setRoleTags(Arrays.asList(roleTagVO()));
		Mockito.when(roleTagService.getAllExistingTags()).thenReturn(tagListVO);
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/roleTag/all")
					.contentType(MediaType.APPLICATION_JSON);
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			assertNotNull(resultObject.getJSONArray("roleTags").get(0));

			JSONObject roleTagObject = (JSONObject) resultObject.getJSONArray("roleTags").get(0);
			assertNotNull(roleTagObject.get("id"));
			assertNotNull(roleTagObject.get("tagName"));
			assertNotNull(roleTagObject.get("modifiedDate"));
			assertNotNull(roleTagObject.get("modifiedBy"));

			assertEquals("Mock Role Tag", roleTagObject.get("tagName"));
			assertEquals("john.doe@afourtech.com", roleTagObject.get("modifiedBy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetAllTagsWhenNoTagsFound() {
		RoleTagListVO tagListVO = new RoleTagListVO();
		tagListVO.setRoleTags(Collections.emptyList());
		Mockito.when(roleTagService.getAllExistingTags()).thenReturn(tagListVO);
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/roleTag/all")
					.contentType(MediaType.APPLICATION_JSON);
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			assertNotNull(resultObject.getJSONArray("roleTags"));

			assertEquals(new ArrayList<>().size(), resultObject.getJSONArray("roleTags").length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetTagByName() {
		Mockito.when(roleTagService.getRoleTag(Mockito.anyString())).thenReturn(roleTagVO());
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/roleTag").param("tagName", "Mock Role Tag")
					.contentType(MediaType.APPLICATION_JSON);
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			
			assertNotNull(resultObject);
			assertEquals("Mock Role Tag", resultObject.get("tagName"));
			assertEquals(Status.ACTIVE.name(), resultObject.get("status"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddRoleTagWhenValidInput() {
		List<CommonTag> mockRoleResponsibilities = new ArrayList<>();
		List<CommonTag> mockCommonCompetencies = new ArrayList<>();
		List<CommonTag> mockBusinessPhilosophies = new ArrayList<>();
		RoleTagListVO roleTagListVO = new RoleTagListVO();
		List<RoleTagVO> roleTags = new ArrayList<>();
		RoleTagVO tagVO = new RoleTagVO();
		tagVO.setTagName("Mock Role Tag");
		tagVO.setRoleResponsibilities(mockRoleResponsibilities);
		tagVO.setCommonCompetencies(mockCommonCompetencies);
		tagVO.setBusinessPhilosophies(mockBusinessPhilosophies);

		roleTags.add(tagVO);
		roleTagListVO.setRoleTags(roleTags);
		try {
			ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String requestBody = mapper.writeValueAsString(roleTagListVO);

			Mockito.when(roleTagService.addRoleTag(Mockito.any())).thenReturn(roleTagVO());

			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/roleTag")
					.contentType(MediaType.APPLICATION_JSON).content(requestBody);
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();

			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			assertEquals("Mock Role Tag", resultObject.get("tagName"));
			assertEquals("john.doe@afourtech.com", resultObject.get("modifiedBy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateRoleTagWhenValidInput() {
		List<CommonTag> mockRoleResponsibilities = new ArrayList<>();
		List<CommonTag> mockCommonCompetencies = new ArrayList<>();
		List<CommonTag> mockBusinessPhilosophies = new ArrayList<>();
		RoleTagVO tagVO = new RoleTagVO();
		tagVO.setDescription("Test Role Tag Update");
		tagVO.setRoleResponsibilities(mockRoleResponsibilities);
		tagVO.setCommonCompetencies(mockCommonCompetencies);
		tagVO.setBusinessPhilosophies(mockBusinessPhilosophies);
		tagVO.setModifiedBy("john.doe@afourtech.com");
		tagVO.setStatus(Status.ACTIVE);

		try {
			Mockito.when(roleTagService.updateRoleTag(Mockito.any())).thenReturn(tagVO);

			RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/roleTag")
					.contentType(MediaType.APPLICATION_JSON).content(Util.asJsonString(tagVO))
					.param("documentId", "5bfe685730642420c4e1da76");
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			assertEquals("Test Role Tag Update", resultObject.get("description"));
			assertEquals("john.doe@afourtech.com", resultObject.get("modifiedBy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteRoleTagWhenStatusIsDisabled() {
		RoleTagVO tagVO = roleTagVO();
		tagVO.setStatus(Status.DISABLE);
		try {
			Mockito.when(roleTagService.deleteRoleTag(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(tagVO);
			RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/roleTag")
					.contentType(MediaType.APPLICATION_JSON).content(Util.asJsonString(tagVO))
					.param("tagId", "5bfe685730642420c4e1da76").param("disabled", "true");
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isAccepted()).andReturn();

			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			assertEquals("DISABLE", resultObject.get("status"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteRoleTagWhenStatusIsInactive() {
		RoleTagVO tagVO = roleTagVO();
		tagVO.setStatus(Status.INACTIVE);
		try {
			Mockito.when(roleTagService.deleteRoleTag(Mockito.anyString(), Mockito.anyBoolean())).thenThrow(AlreadyDeletedEntityException.class);
			RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/roleTag")
					.contentType(MediaType.APPLICATION_JSON).content(Util.asJsonString(tagVO))
					.param("tagId", "5bfe685730642420c4e1da76");
			mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
