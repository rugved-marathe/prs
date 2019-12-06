package io.pms.api.vo;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.SOURCE_EXISTS;
import static io.pms.api.common.Constants.SOURCE_REQUIRED;
import static io.pms.api.common.ErrorType.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.pms.api.exception.Errors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class GoalCategoryVO {

    private String goalCategoryId;

    private String goalCategory;

    private String dateModified;

    public List<Errors> validateCreateGoalCategory() {
        List<Errors> errorList = new ArrayList<>();
        String errorMessage = BAD_REQUEST.getErrorMessage();
        if (StringUtils.isEmpty(this.getGoalCategory()))
            createErrorList("goalCategory", errorMessage, SOURCE_REQUIRED, errorList);
        return errorList;
    }

    public List<Errors> validateDuplicateGoalCategory() {
        List<Errors> errorList = new ArrayList<>();
        String errorMessage = BAD_REQUEST.getErrorMessage();
        createErrorList("goalCategory", errorMessage, SOURCE_EXISTS, errorList);
        return errorList;
    }

    @Builder
    public GoalCategoryVO(String goalCategoryId, String goalCategory, String dateModified) {
        this.goalCategoryId = goalCategoryId;
        this.goalCategory = goalCategory;
        this.dateModified = dateModified;
    }
}
