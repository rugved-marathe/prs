package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.pms.api.exception.AlreadyExistsException;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.GoalCategory;
import io.pms.api.repositories.GoalCategoryRepository;
import io.pms.api.vo.GoalCategoryListVO;
import io.pms.api.vo.GoalCategoryVO;

@Service
public class GoalCategoryService {

    @Autowired
    GoalCategoryRepository goalCategoryRepository;

    public GoalCategory createGoalCategory(GoalCategoryVO goalCategoryVO) {
        if (goalCategoryVO.validateCreateGoalCategory().isEmpty()) {
            if (goalCategoryRepository.findByGoalCategory(goalCategoryVO.getGoalCategory()) == null) {
                GoalCategory goalCategory = GoalCategory.builder()
                        .goalCategory(goalCategoryVO.getGoalCategory())
                        .build();
                goalCategoryRepository.save(goalCategory);
                return goalCategory;
            }
            throw new AlreadyExistsException(goalCategoryVO.validateDuplicateGoalCategory());
        }
        throw new ValidationException(goalCategoryVO.validateCreateGoalCategory());
    }

    @Transactional
    public GoalCategoryListVO getGoalCategories() {
        List<GoalCategory> goalCategories = goalCategoryRepository.findAll();
        GoalCategoryListVO goalCategoryListVO = new GoalCategoryListVO();
        List<GoalCategoryVO> goalCategoryList = goalCategories.stream().map(goalCategory -> GoalCategoryVO.builder()
                .goalCategoryId(goalCategory.getGoalCategoryId())
                .goalCategory(goalCategory.getGoalCategory())
                .dateModified(goalCategory.getModifiedDate().toString())
                .build()).collect(Collectors.toList());
        goalCategoryListVO.setGoalCategories(goalCategoryList);
        return goalCategoryListVO;
    }

    public GoalCategory editGoalCategory(String goalCategoryId, GoalCategoryVO goalCategoryVO) {
        GoalCategory goalCategoryToBeEdited = goalCategoryRepository.findOne(goalCategoryId);
        if (goalCategoryToBeEdited != null) {
            if (goalCategoryVO.validateCreateGoalCategory().isEmpty()) {
                goalCategoryToBeEdited.setGoalCategory(goalCategoryVO.getGoalCategory());
                goalCategoryRepository.save(goalCategoryToBeEdited);
                return goalCategoryToBeEdited;
            }
            throw new ValidationException(goalCategoryVO.validateCreateGoalCategory());
        }
        throw new NotFoundException(listOfErrors("goalCategory"));
    }

    public GoalCategory deleteGoalCategory(String goalCategoryId) {
        GoalCategory goalCategoryToBeDeleted = goalCategoryRepository.findOne(goalCategoryId);
        if (goalCategoryToBeDeleted != null) {
            goalCategoryRepository.delete(goalCategoryToBeDeleted);
            return null;
        }
        throw new NotFoundException(listOfErrors("goalCategory"));
    }
}
