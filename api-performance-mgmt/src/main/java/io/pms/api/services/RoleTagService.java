package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.pms.api.common.CommonUtils;
import io.pms.api.common.Status;
import io.pms.api.exception.AlreadyDeletedEntityException;
import io.pms.api.exception.Errors;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.RoleTag;
import io.pms.api.repositories.RoleTagRepository;
import io.pms.api.vo.RoleTagListVO;
import io.pms.api.vo.RoleTagVO;

@Service
public class RoleTagService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleTagService.class);

	@Autowired
	private RoleTagRepository roleRepository;

	public RoleTagListVO getAllExistingTags() {
		RoleTagListVO roleTagListVO = new RoleTagListVO();
		try {
			List<RoleTag> tags = roleRepository.findAll();
			if (null != tags) {
				List<RoleTagVO> tagList = tags.stream().map(tag -> {
					RoleTagVO tagVO = new RoleTagVO();
					CommonUtils.copyNonNullProperties(tag, tagVO);
					return tagVO;
				}).filter(tag -> !Status.INACTIVE.equals(tag.getStatus())).collect(Collectors.toList());
				roleTagListVO.setRoleTags(tagList);	
			} else {
				LOGGER.error("tags object of list is null");
				throw new NotFoundException(listOfErrors("Error fetching tag list from the db"));
			}
		} catch (Exception e) {
			LOGGER.error("Error while fetching values {}", e.getMessage() + e.getCause());
		}
		return roleTagListVO;
	}

	public RoleTagVO getRoleTag(String tagName) {
		RoleTagVO roleTagVO = null;
		if (!tagName.isEmpty()) {
			List<RoleTag> roleTags = roleRepository.findByTagNameAndStatus(tagName, Status.ACTIVE);
			if (null != roleTags && !roleTags.isEmpty()) {
				roleTagVO = new RoleTagVO();
				CommonUtils.copyNonNullProperties(roleTags.get(0), roleTagVO);
			} else {
				LOGGER.error("no tag found by {}", tagName);
				throw new NotFoundException(listOfErrors("Unable to find tag by name : " + tagName));
			}
		} else {
			LOGGER.error("roleTag : empty tag name");//
			throw new PMSAppException(listOfErrors("Invalid roletag"));
		}
		return roleTagVO;
	}

	public RoleTagVO addRoleTag(RoleTagVO roleTagVO) {
		RoleTagVO tagVO = new RoleTagVO();
		if (null == roleTagVO) {
			LOGGER.error("invalid input");
			throw new ValidationException(listOfErrors("Role Tag empty"));
		}
		List<Errors> validationErrorList = roleTagVO.validateRoleTag();
		if (validationErrorList.isEmpty()) {
			RoleTag roleTag = new RoleTag();
			CommonUtils.copyNonNullProperties(roleTagVO, roleTag);
			roleTag.setStatus(Status.ACTIVE);
			RoleTag createdTag = roleRepository.save(roleTag);
			BeanUtils.copyProperties(createdTag, tagVO);
		} else {
			LOGGER.error(validationErrorList.get(0).getMessage());
			throw new ValidationException(validationErrorList);
		}
		return tagVO;
	}

	public RoleTagVO updateRoleTag(RoleTagVO roleTagVO) {
		RoleTag tag = null;
		RoleTagVO tagVO = new RoleTagVO();
		if (null == roleTagVO) {
			LOGGER.error("Empty attribute values in tag object.");
			throw new NotFoundException(listOfErrors("no Parameters found."));
		}
		tag = roleRepository.findOne(roleTagVO.getId());
		LOGGER.debug("Checking if tag exist or not.");
		if (null != tag) {
			Status tagStatus = tag.getStatus();
			if (Status.ACTIVE.equals(tagStatus)) {
				LOGGER.debug("Status -> {}. Processing the update request.", tagStatus);
				CommonUtils.copyNonNullProperties(roleTagVO, tag);
				tag.setStatus(Status.ACTIVE);
				roleRepository.save(tag);
				CommonUtils.copyNonNullProperties(tag, tagVO);
				return tagVO;
			} else if (Status.DISABLE.equals(tagStatus)) {
				LOGGER.debug("Status -> {}. Processing the update request.", tagStatus);
				CommonUtils.copyNonNullProperties(roleTagVO, tag);
				roleRepository.save(tag);
				CommonUtils.copyNonNullProperties(tag, tagVO);
				return tagVO;
			} else {
				LOGGER.error("Error updating tag entity : Reason (Check status of the tag).");
				throw new AlreadyDeletedEntityException(listOfErrors("Cannot update role tag."));
			}
		} else {
			LOGGER.error("Unable to find tag. Provide valid tag id.");
			throw new NotFoundException(listOfErrors("This tag is not present."));
		}
	}

	public RoleTagVO deleteRoleTag(String tagId, boolean status) {
		RoleTag tagToBeDeleted = roleRepository.findOne(tagId);
		RoleTagVO tagVO = new RoleTagVO();
		if (null != tagToBeDeleted) {
			Status currentTagStatus = tagToBeDeleted.getStatus();
			if (Status.ACTIVE.equals(currentTagStatus) || Status.DISABLE.equals(currentTagStatus)) {
				if (status) {
					tagToBeDeleted.setStatus(Status.DISABLE);
					roleRepository.save(tagToBeDeleted);
					LOGGER.debug("Disable processed : {}", tagToBeDeleted.getStatus());
				} else {
					tagToBeDeleted.setStatus(Status.INACTIVE);
					roleRepository.save(tagToBeDeleted);
					LOGGER.debug("Delete processed : {}", tagToBeDeleted.getStatus());
				}
			} else {
				LOGGER.error("Tag is already deleted.");
				throw new AlreadyDeletedEntityException(listOfErrors("Tag is already deleted."));
			}
		} else {
			LOGGER.error("RoleTag not found for given tagId {}", tagId);
			throw new NotFoundException(listOfErrors("RoleTag not found."));
		}
		BeanUtils.copyProperties(tagToBeDeleted, tagVO);
		return tagVO;
	}

}
