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
public class ResponsibilityVO {
    private String responsibilityId;

    private String responsibilityDescription;

    private String roleId;

    private String dateModified;

    public List<Errors> validateCreateResponsibility() {
        List<Errors> errorList = new ArrayList<>();
        String errorMessage = BAD_REQUEST.getErrorMessage();
        if (StringUtils.isEmpty(this.getResponsibilityDescription()))
            createErrorList("responsibilityDescription", errorMessage, SOURCE_REQUIRED, errorList);
        if (StringUtils.isEmpty(this.getRoleId()))
            createErrorList("roleId", errorMessage, SOURCE_REQUIRED, errorList);
        return errorList;
    }

    public List<Errors> validateDuplicateResponsibility() {
        List<Errors> errorList = new ArrayList<>();
        String errorMessage = BAD_REQUEST.getErrorMessage();
        createErrorList("responsibility", errorMessage, SOURCE_EXISTS, errorList);
        return errorList;
    }

    @Builder
    public ResponsibilityVO(String responsibilityId, String responsibilityDescription, String roleId, String dateModified) {
        this.responsibilityId = responsibilityId;
        this.responsibilityDescription = responsibilityDescription;
        this.roleId = roleId;
        this.dateModified = dateModified;
    }
}
