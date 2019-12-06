package io.pms.api.exception;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private List<Errors> errorList = new LinkedList<>();

    public BaseException(List<Errors> errorList) {
        this.errorList = errorList;
    }
}
