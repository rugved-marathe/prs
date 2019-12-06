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
import io.pms.api.vo.CommonCompetencyListVO;
import io.pms.api.vo.CommonCompetencyVO;

@Service
public class CommonCompetencyService {

    @Autowired
    private CommonRepository commonRepository;

    public Common createCommonCompetencies(CommonCompetencyVO commonCompetencyVO) {
        List<Common> existingCommon = commonRepository.findAll();
        Common common;
        if (existingCommon.isEmpty()) common = new Common();
        else if (CollectionUtils.isEmpty(existingCommon.get(0).getCompetencies())) common = existingCommon.get(0);
        else {
            common = existingCommon.get(0);
            if (CollectionUtils.intersection(commonCompetencyVO.getCompetencies(), common.getCompetencies()).isEmpty()) {
                common.getCompetencies().addAll(commonCompetencyVO.getCompetencies());
                commonRepository.save(common);
                return common;
            }
            throw new AlreadyExistsException(commonCompetencyVO.validateDuplicateCommonCompetency());
        }
        common.setCompetencies(commonCompetencyVO.getCompetencies());
        commonRepository.save(common);
        return common;
    }

    public Common editCommonCompetencies(CommonCompetencyVO commonCompetencyVO) {
        List<Common> existingCommon = commonRepository.findAll();
        if (!existingCommon.isEmpty()) {
            Common common = existingCommon.get(0);
            if (existingCommon.isEmpty() || common.getCompetencies().isEmpty())
                throw new NotFoundException(listOfErrors("commonCompetencies"));
            common.setCompetencies(commonCompetencyVO.getCompetencies());
            commonRepository.save(existingCommon.get(0));
            return common;
        }
        throw new NotFoundException(listOfErrors("common competencies"));
    }

    @Transactional
    public CommonCompetencyListVO getCommonCompetencies() {
        List<Common> competencies = commonRepository.findAll();
        CommonCompetencyListVO commonCompetencyListVO = new CommonCompetencyListVO();
        commonCompetencyListVO.setCompetencies(competencies.get(0).getCompetencies());
        return commonCompetencyListVO;
    }
}
