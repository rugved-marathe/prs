package io.pms.api.vo;

import static io.pms.api.common.CommonUtils.createErrorList;
import static io.pms.api.common.Constants.SOURCE_EXISTS;
import static io.pms.api.common.ErrorType.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.pms.api.exception.Errors;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class BusinessPhilosphyVO {
    private List<String> philosophies;

    public List<Errors> validateDuplicateBusinessPhilosophy() {
        List<Errors> errorList = new ArrayList<>();
        String errorMessage = BAD_REQUEST.getErrorMessage();
        createErrorList("businessPhilosophy", errorMessage, SOURCE_EXISTS, errorList);
        return errorList;
    }
}
