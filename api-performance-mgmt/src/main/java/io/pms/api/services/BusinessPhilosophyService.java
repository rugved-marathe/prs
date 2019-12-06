package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.pms.api.exception.AlreadyExistsException;
import io.pms.api.exception.NotFoundException;
import io.pms.api.model.Common;
import io.pms.api.repositories.CommonRepository;
import io.pms.api.vo.BusinessPhilosophyListVO;
import io.pms.api.vo.BusinessPhilosphyVO;

@Service
public class BusinessPhilosophyService {

    @Autowired
    CommonRepository commonRepository;

    public Common createBusinessPhilosophy(BusinessPhilosphyVO businessPhilosphyVO) {
        List<Common> existingCommon = commonRepository.findAll();
        Common common;
        if (existingCommon.isEmpty()) common = new Common();
        else if (CollectionUtils.isEmpty(existingCommon.get(0).getPhilosophies())) common = existingCommon.get(0);
        else {
            common = existingCommon.get(0);
            if (CollectionUtils.intersection(businessPhilosphyVO.getPhilosophies(), common.getPhilosophies()).isEmpty()) {
                common.getPhilosophies().addAll(businessPhilosphyVO.getPhilosophies());
                commonRepository.save(common);
                return common;
            }
            throw new AlreadyExistsException(businessPhilosphyVO.validateDuplicateBusinessPhilosophy());
        }
        common.setPhilosophies(businessPhilosphyVO.getPhilosophies());
        commonRepository.save(common);
        return common;
    }

    public Common editBusinessPhilosophy(BusinessPhilosphyVO businessPhilosphyVO) {
        List<Common> existingCommon = commonRepository.findAll();
        if (!existingCommon.isEmpty()) {
            Common common = existingCommon.get(0);
            if (existingCommon.isEmpty() || common.getPhilosophies().isEmpty())
                throw new NotFoundException(listOfErrors("businessPhilosophies"));
            common.setPhilosophies(businessPhilosphyVO.getPhilosophies());
            commonRepository.save(common);
            return common;
        }
        throw new NotFoundException(listOfErrors("business philosophies"));
    }

    @Transactional
    public BusinessPhilosophyListVO getBusinessPhilosophies() {
        List<Common> philosophies = commonRepository.findAll();
        BusinessPhilosophyListVO businessPhilosophyListVO = new BusinessPhilosophyListVO();
        businessPhilosophyListVO.setPhilosophies(philosophies.get(0).getPhilosophies());
        return businessPhilosophyListVO;
    }
}
