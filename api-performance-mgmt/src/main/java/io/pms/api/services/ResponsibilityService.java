package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.pms.api.exception.AlreadyExistsException;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.Responsibility;
import io.pms.api.repositories.ResponsibilityRepository;
import io.pms.api.vo.ResponsibilityListVO;
import io.pms.api.vo.ResponsibilityVO;

@Service
public class ResponsibilityService {

    @Autowired
    ResponsibilityRepository responsibilityRepository;

    public Responsibility createResponsibility(ResponsibilityVO responsibilityVO) {
        if (! (StringUtils.isEmpty(responsibilityVO.getRoleId()) || StringUtils.isEmpty(responsibilityVO.getResponsibilityDescription()))) {
            if (responsibilityRepository.findByRoleIdAndResponsibilityDescription(responsibilityVO.getRoleId(), responsibilityVO.getResponsibilityDescription()) == null) {
                Responsibility responsibility = Responsibility.builder()
                        .roleId(responsibilityVO.getRoleId())
                        .responsibilityDescription(responsibilityVO.getResponsibilityDescription())
                        .build();
                responsibilityRepository.save(responsibility);
                return responsibility;
            }
            throw new AlreadyExistsException(responsibilityVO.validateDuplicateResponsibility());
        }
        throw new ValidationException(responsibilityVO.validateCreateResponsibility());
    }

    @Transactional
    public ResponsibilityListVO getResponsibilities() {
        ResponsibilityListVO responsibilityListVO = new ResponsibilityListVO();
        List<Responsibility> responsibility = responsibilityRepository.findAll();
        List<ResponsibilityVO> responsibilities = responsibility.stream().map(r -> ResponsibilityVO.builder()
                .responsibilityId(r.getResponsibilityId())
                .roleId(r.getRoleId())
                .responsibilityDescription(r.getResponsibilityDescription())
                .dateModified(r.getModifiedDate().toString())
                .build()).collect(Collectors.toList());
        responsibilityListVO.setResponsibilities(responsibilities);
        return responsibilityListVO;
    }

    @Transactional
    public ResponsibilityListVO getRoleResponsibilities(String roleId) {
        ResponsibilityListVO responsibilityListVO = new ResponsibilityListVO();
        List<Responsibility> roleResponsibilities = responsibilityRepository.findByRoleId(roleId);
        List<ResponsibilityVO> responsibilities = roleResponsibilities.stream().map(r -> ResponsibilityVO.builder()
                .roleId(r.getRoleId())
                .responsibilityDescription(r.getResponsibilityDescription())
                .dateModified(r.getModifiedDate().toString())
                .build()).collect(Collectors.toList());
        responsibilityListVO.setResponsibilities(responsibilities);
        return responsibilityListVO;
    }

    public Responsibility editResponsibility(String responsibilityId, ResponsibilityVO responsibilityVO) {
        Responsibility responsibilityToBeEdited = responsibilityRepository.findOne(responsibilityId);
        if (responsibilityToBeEdited != null) {
            if (responsibilityVO.validateCreateResponsibility().isEmpty()) {
                responsibilityToBeEdited.setRoleId(responsibilityVO.getRoleId());
                responsibilityToBeEdited.setResponsibilityDescription(responsibilityVO.getResponsibilityDescription());
                responsibilityRepository.save(responsibilityToBeEdited);
                return responsibilityToBeEdited;
            }
            throw new ValidationException(responsibilityVO.validateCreateResponsibility());
        }
        throw new NotFoundException(listOfErrors("responsibility"));
    }

    public Responsibility deleteResponsibility(String responsibilityId) {
        Responsibility responsibilityToBeDeleted = responsibilityRepository.findOne(responsibilityId);
        if (responsibilityToBeDeleted != null) {
            responsibilityRepository.delete(responsibilityToBeDeleted);
            return null;
        }
        throw new NotFoundException(listOfErrors("responsibility"));
    }
}
