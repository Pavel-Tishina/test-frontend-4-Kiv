package com.itgnostic.test4sandbox.utils;

import com.itgnostic.test4sandbox.db.entity.EmployeeEntity;
import com.itgnostic.test4sandbox.service.EmployeeService;
import com.itgnostic.test4sandbox.service.OperationResult;
import com.itgnostic.test4sandbox.web.api.rest.model.RespEmployeeModel;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;
import java.util.Set;

import static com.itgnostic.test4sandbox.common.Const.DT;

public class EmployeeUtils {
    public static String getCreatedDate(EmployeeEntity e) {
        return DT.format(e.getCreated());
    }

    public static boolean someChanges(EmployeeEntity e,
                                      String newFirstName,
                                      String newLastName,
                                      String newPosition,
                                      Long newSupervisor,
                                      Set<Long> newSubs) {

        return !Objects.equals(e.getSupervisor(), newSupervisor)
                || !Objects.equals(e.getFirstName(), newFirstName)
                || !Objects.equals(e.getLastName(), newLastName)
                || !Objects.equals(e.getPosition(), newPosition)
                || !Objects.equals(e.getSubordinates(), newSubs);
    }

    public static EmployeeEntity updateValues(EmployeeEntity e,
                                      String newFirstName,
                                      String newLastName,
                                      String newPosition,
                                      Long newSupervisor,
                                      Set<Long> newSubs) {
        e.setFirstName(newFirstName);
        e.setLastName(newLastName);
        e.setPosition(newPosition);
        e.setSupervisor(newSupervisor);
        e.setSubordinates(newSubs);
        return e;
    }

    public static String getFullName(EmployeeEntity e) {
        return "%s %s (%d)".formatted(e.getFirstName(), e.getLastName(), e.getId());
    }

    public static String getFullName(RespEmployeeModel e) {
        return "%s %s (%s)".formatted(e.getFirstName(), e.getLastName(), e.getId());
    }

    public static String getSupervisorFullName(EmployeeEntity e, EmployeeService employeeService) {
        OperationResult supOperationResult = employeeService.get(e.getSupervisor());
        RespEmployeeModel supE = null;

        if (supOperationResult.isSuccess())
            supE = supOperationResult.getResultList().get(0);

        return supE != null
            ? getFullName(supE)
            : "";
    }

    public static boolean allReqFieldsOk(EmployeeEntity e) {
        return e != null && e.getId() != null && e.getId() > 0
                && Strings.isNotBlank(e.getFirstName()) && Strings.isNotBlank(e.getLastName())
                && e.getCreated() != null;
    }

    public static boolean allReqFieldsOk(RespEmployeeModel e) {
        return e != null && Strings.isNotBlank(e.getId())
                && Strings.isNotBlank(e.getFirstName()) && Strings.isNotBlank(e.getLastName())
                && e.getCreated() != null;
    }

    public static RespEmployeeModel toRespEmployeeModel(EmployeeEntity e, EmployeeService employeeService) {
        return allReqFieldsOk(e)
                ? RespEmployeeModel.builder()
                    .id(e.getId().toString())
                    .firstName(e.getFirstName())
                    .lastName(e.getLastName())
                    .fullName(getFullName(e))
                    .position(e.getPosition())
                    .supervisorId(e.getSupervisor() != null ? e.getSupervisor().toString() : "")
                    .supervisorFullName(getSupervisorFullName(e, employeeService))
                    .created(DT.format(e.getCreated()))
                    .build()
                : new RespEmployeeModel();
    }

    //TODO getting map for UI-elements context

}
