/**
 * 
 */
package io.pms.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import org.springframework.test.context.junit4.SpringRunner;

import io.pms.api.common.Status;
import io.pms.api.exception.AlreadyDeletedEntityException;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.CommonTag;
import io.pms.api.model.RoleTag;
import io.pms.api.repositories.RoleTagRepository;
import io.pms.api.services.RoleTagService;
import io.pms.api.vo.RoleTagListVO;
import io.pms.api.vo.RoleTagVO;

/**
 * @author rugved.m
 *
 */
@RunWith(SpringRunner.class)
public class RoleTagServiceTest {

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private RoleTagRepository roleTagRepository;

	@InjectMocks
	private RoleTagService roleTagService;

	@MockBean
	private AuditingHandler auditingHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	private RoleTag roleTag() {
		List<CommonTag> mockRoleResponsibilities = new ArrayList<>();
		List<CommonTag> mockCommonCompetencies = new ArrayList<>();
		List<CommonTag> mockBusinessPhilosophies = new ArrayList<>();

		RoleTag tagVO = new RoleTag();
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

	private CommonTag commonTag() {
		CommonTag commonTag = new CommonTag();
		commonTag.setAttribute("Attribute - 1");
		commonTag.setSelfComments("Self comment");
		commonTag.setReviewerComments("Reviewer comment");
		commonTag.setRating(1);
		return commonTag;
	}

	@Test
	public void whenGetAllTagsIsTriggered_thenReturnListOfTags() {
		Mockito.when(roleTagRepository.findAll()).thenReturn(Arrays.asList(roleTag()));

		RoleTagListVO tagListVO = roleTagService.getAllExistingTags();

		assertNotNull(tagListVO);
		assertEquals("Mock Role Tag", tagListVO.getRoleTags().get(0).getTagName());
		assertEquals("john.doe@afourtech.com", tagListVO.getRoleTags().get(0).getModifiedBy());
		assertEquals(Status.ACTIVE, tagListVO.getRoleTags().get(0).getStatus());
	}

	@Test
	public void whenGetAllTagsIsTriggered_thenReturnOnlyListOfActiveTags() {
		RoleTag roleTag = roleTag();
		roleTag.setStatus(Status.INACTIVE);

		Mockito.when(roleTagRepository.findAll()).thenReturn(Arrays.asList(roleTag(), roleTag));

		RoleTagListVO tagListVO = roleTagService.getAllExistingTags();

		assertNotNull(tagListVO);
		assertEquals(1, tagListVO.getRoleTags().size());
	}

	@Test
	public void whenGetAllTagsIsTriggered_thenNotFoundException() {
		Mockito.when(roleTagRepository.findAll()).thenReturn(null);
		RoleTagListVO tagListVO = roleTagService.getAllExistingTags();

		assertNull(tagListVO.getRoleTags());
	}

	@Test
	public void whenGetRoleTagIsTriggered_thenRoleTagFound() {
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), any()))
				.thenReturn(Arrays.asList(roleTag()));

		RoleTagVO tagVO = roleTagService.getRoleTag("Mock Role Tag");

		assertNotNull(tagVO);
		assertEquals("Mock Role Tag", tagVO.getTagName());
		assertEquals(Status.ACTIVE, tagVO.getStatus());
	}

	@Test(expected = NotFoundException.class)
	public void whenGetRoleTagIsTriggered_thenRoleTagIsInactive() {
		RoleTag roleTag = roleTag();
		roleTag.setStatus(Status.INACTIVE);

		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), any()))
				.thenReturn(Collections.emptyList());

		roleTagService.getRoleTag("Mock Role Tag");
	}

	@Test(expected = PMSAppException.class)
	public void whenGetRoleTagIsTriggered_thenEmptyTagNameProvided() {
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), any()))
				.thenReturn(Arrays.asList(roleTag()));

		roleTagService.getRoleTag("");

	}

	@Test
	public void whenAddRoleTagIsTriggered_thenNewRoleTagIsCreated() {
		RoleTagVO roleTagVO = roleTagVO();
		roleTagVO.setRoleResponsibilities(Arrays.asList(commonTag()));
		roleTagVO.setBusinessPhilosophies(Arrays.asList(commonTag()));
		roleTagVO.setCommonCompetencies(Arrays.asList(commonTag()));
		
		Mockito.when(roleTagRepository.save(Mockito.any(RoleTag.class))).thenReturn(roleTag());

		RoleTagVO createdTag = roleTagService.addRoleTag(roleTagVO);

		assertNotNull(createdTag);
		assertEquals("Mock Role Tag", createdTag.getTagName());
		assertEquals("john.doe@afourtech.com", createdTag.getModifiedBy());
		assertEquals(Status.ACTIVE, createdTag.getStatus());

	}

	@Test(expected = ValidationException.class)
	public void whenAddRoleTagIsTriggered_thenMissingRequiredEntitiesWithException() {
		Mockito.when(roleTagRepository.save(Mockito.any(RoleTag.class))).thenReturn(roleTag());

		roleTagService.addRoleTag(roleTagVO());
	}

	@Test(expected = ValidationException.class)
	public void whenAddRoleTagIsTriggered_thenDescriptionIsNullWithException() {
		Mockito.when(roleTagRepository.save(roleTag())).thenReturn(roleTag());

		RoleTagVO tagVO = roleTagVO();
		tagVO.setTagName(null);

		RoleTagVO createdTag = roleTagService.addRoleTag(tagVO);

		assertNull(createdTag);
	}

	@Test(expected = ValidationException.class)
	public void whenAddRoleTagIsTriggered_thenSourceIsNullWithException() {
		RoleTagVO createdTag = roleTagService.addRoleTag(null);

		assertNull(createdTag);
	}

	@Test
	public void whenUpdateRoleTagIsTriggered_thenUpdateSpecifiedRoleTag() {
		RoleTag exisitingRoleTag = roleTag();
		exisitingRoleTag.setDescription("This tag is updated.");
		RoleTagVO tagVO = new RoleTagVO();
		tagVO.setDescription("This tag is updated.");

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(roleTag());
		Mockito.when(roleTagRepository.save(Mockito.any(RoleTag.class))).thenReturn(exisitingRoleTag);

		RoleTagVO updatedRoleTag = roleTagService.updateRoleTag(tagVO);
		assertNotNull(updatedRoleTag);
		assertEquals("This tag is updated.", updatedRoleTag.getDescription());
	}

	@Test
	public void whenUpdateRoleTagIsTriggeredWithStatusDisabled_thenUpdateRoleTag() {
		RoleTag exisitingRoleTag = roleTag();
		exisitingRoleTag.setStatus(Status.DISABLE);
		RoleTagVO tagVO = new RoleTagVO();
		tagVO.setDescription("This tag is updated.");

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(exisitingRoleTag);
		Mockito.when(roleTagRepository.save(Mockito.any(RoleTag.class))).thenReturn(roleTag());

		RoleTagVO updatedRoleTag = roleTagService.updateRoleTag(tagVO);
		assertNotNull(updatedRoleTag);
		assertEquals("This tag is updated.", updatedRoleTag.getDescription());
	}

	@Test(expected = NotFoundException.class)
	public void whenUpdateRoleTagIsTriggered_thenExisitingTagIsNotFound() {
		RoleTag exisitingRoleTag = roleTag();
		exisitingRoleTag.setDescription("This tag is updated.");
		RoleTagVO tagVO = new RoleTagVO();
		tagVO.setDescription("This tag is updated.");

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(null);

		roleTagService.updateRoleTag(tagVO);
	}

	@Test(expected = AlreadyDeletedEntityException.class)
	public void whenUpdateRoleTagIsTriggered_thenFailedToUpdateRoleTag() {
		RoleTag exisitingRoleTag = roleTag();
		exisitingRoleTag.setStatus(Status.INACTIVE);
		RoleTagVO tagVO = new RoleTagVO();
		tagVO.setDescription("This tag is updated.");

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(exisitingRoleTag);

		roleTagService.updateRoleTag(tagVO);
	}

	@Test(expected = NotFoundException.class)
	public void whenUpdateRoleTagIsTriggered_thenRequestUpdateRoleTagParameterAreNull() {
		RoleTagVO tagVO = null;

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(roleTag());

		roleTagService.updateRoleTag(tagVO);
	}

	@Test
	public void whenDeleteRoleTagIsTriggered_thenRoleTagIsDeleted() {
		RoleTag exisitingRoleTag = roleTag();
//		exisitingRoleTag.setStatus(Status.INACTIVE);

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(roleTag());
		Mockito.when(roleTagRepository.save(Mockito.any(RoleTag.class))).thenReturn(exisitingRoleTag);

		RoleTagVO deletedTagVO = roleTagService.deleteRoleTag(roleTag().getId(), false);
		assertNotNull(deletedTagVO);
		assertEquals(Status.INACTIVE, deletedTagVO.getStatus());
	}

	@Test
	public void whenDeleteRoleTagIsTriggered_thenRoleTagIsDisabled() {
		RoleTag exisitingRoleTag = roleTag();
		exisitingRoleTag.setStatus(Status.DISABLE);

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(roleTag());
		Mockito.when(roleTagRepository.save(Mockito.any(RoleTag.class))).thenReturn(exisitingRoleTag);

		RoleTagVO deletedTagVO = roleTagService.deleteRoleTag(roleTag().getId(), true);
		assertNotNull(deletedTagVO);
		assertEquals(Status.DISABLE, deletedTagVO.getStatus());
	}

	@Test(expected = AlreadyDeletedEntityException.class)
	public void whenDeleteRoleTagIsTriggered_thenAlreadyDeletedEntityException() {
		RoleTag exisitingRoleTag = roleTag();
		exisitingRoleTag.setStatus(Status.INACTIVE);

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(exisitingRoleTag);

		roleTagService.deleteRoleTag(roleTag().getId(), false);
	}

	@Test(expected = NotFoundException.class)
	public void whenDeleteRoleTagIsTriggered_thenNotFoundException() {
		RoleTag exisitingRoleTag = roleTag();
		exisitingRoleTag.setStatus(Status.INACTIVE);

		Mockito.when(roleTagRepository.findOne(Mockito.anyString())).thenReturn(null);

		roleTagService.deleteRoleTag(roleTag().getId(), false);
	}
}
