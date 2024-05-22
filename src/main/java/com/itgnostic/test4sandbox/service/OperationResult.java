package com.itgnostic.test4sandbox.service;

import com.google.common.base.Strings;
import com.itgnostic.test4sandbox.db.entity.EmployeeEntity;
import com.itgnostic.test4sandbox.errors.DbErrors;
import com.itgnostic.test4sandbox.utils.EmployeeUtils;
import com.itgnostic.test4sandbox.web.api.rest.model.RespEmployeeModel;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itgnostic.test4sandbox.common.Const.DT;
import static com.itgnostic.test4sandbox.errors.DbErrors.BAD_RESULT;
import static com.itgnostic.test4sandbox.errors.DbErrors.DB_ERROR;

public class OperationResult {
    @Getter
    private List<RespEmployeeModel> resultList = new ArrayList<>();
    @Getter
    private final List<String> errorList = new ArrayList<>();

    OperationResult() {}

    OperationResult(Object result, EmployeeService employeeService) {
        if (result instanceof EmployeeEntity r)
            addResult(r, employeeService);
        else if (result instanceof List rl && !rl.isEmpty() && rl.iterator().next() instanceof EmployeeEntity)
            addResult((List<EmployeeEntity>) rl, employeeService);
        else if (result == null)
            addError(BAD_RESULT);
    }

    public void addResult(EmployeeEntity result, EmployeeService employeeService) {
        if (result != null)
            resultList.add(EmployeeUtils.toRespEmployeeModel(result, employeeService));
        else
            addError(BAD_RESULT);
    }

    public void addResult(Collection<EmployeeEntity> result, EmployeeService employeeService) {
        if (result != null)
            result.forEach(e -> addResult(e, employeeService));
        else
            addError();
    }

    public void addResult(Boolean result) {
        if (result == null || !result)
            addError();
    }

    public void addError() {
        addError(DB_ERROR);
    }
    public void addError(String error) {
        errorList.add(error);
    }

    public void addError(DbErrors error) {
        errorList.add(error.getErrorText());
    }

    public boolean hasErrors() {
        return !errorList.isEmpty();
    }

    public boolean isSuccess() {
        return !resultList.isEmpty() || errorList.isEmpty();
    }

    public String getErrorDetails() {
        return hasErrors()
                ? "Result has " + errorList.size() + " errors: " + String.join("\n\t", errorList)
                : "No errors";
    }

    public List<Map<String, Object>> getResultsAsList() {
        return resultList.stream()
                .filter(EmployeeUtils::allReqFieldsOk)
                .flatMap(e -> {
                    Map<String, Object> map = Map.of(
                            "id", e.getId(),
                            "firstName", e.getFirstName(),
                            "lastName", e.getLastName(),
                            "fullName", e.getFullName(),
                            "position", Strings.nullToEmpty(e.getPosition()),
                            "supervisor", e.getSupervisorId(),
                            "supervisorFullName", e.getSupervisorFullName(),
                            "created", e.getCreated()
                    );
                    return Stream.of(map);
                })
                .collect(Collectors.toList());
    }

}
