package com.itgnostic.test4sandbox.service;

import com.google.common.collect.Sets;
import com.itgnostic.test4sandbox.db.dao.EmployeeDbService;
import com.itgnostic.test4sandbox.db.dao.impl.EmployeeDbServiceImpl;
import com.itgnostic.test4sandbox.db.entity.EmployeeEntity;
import com.itgnostic.test4sandbox.errors.RestApiErrors;
import com.itgnostic.test4sandbox.errors.ValueErrors;
import com.itgnostic.test4sandbox.utils.EmployeeUtils;
import com.itgnostic.test4sandbox.utils.HibernateUtils;
import lombok.NonNull;
import org.h2.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.itgnostic.test4sandbox.errors.DbErrors.*;

@Service
public class EmployeeService {
    EmployeeDbService employeeDbService;


    public EmployeeService() {
        employeeDbService = new EmployeeDbServiceImpl(HibernateUtils.getSessionFactory().openSession());
    }

    public OperationResult add(String firstName, String lastName, String position, Long supervisor) {
        OperationResult out = prapareOperationResult(firstName, lastName, supervisor);

        if (out.hasErrors())
            return out;

        EmployeeEntity e = new EmployeeEntity();
        e.setFirstName(firstName.trim());
        e.setLastName(lastName.trim());
        e.setSupervisor(supervisor);
        e.setPosition(position);

        Long newId = employeeDbService.add(e);
        if (newId == null)
            out.addError(DB_SAVE_NEW_ERROR);
        else
            out.addResult(employeeDbService.get(newId), this);

        if (newId != null && supervisor != null)
            addOrRemoveSubForSupervisor(supervisor, newId, false);

        return out;
    }

    public OperationResult get(Long id) {
        OperationResult out = new OperationResult();
        if (id == null) {
            out.addError(ValueErrors.ID_IS_NULL.getErrorText());
            return out;
        }
        else if (id < 1) {
            out.addError(ValueErrors.ID_IS_ZERO_OR_MINUS.getErrorText());
            return out;
        }

        EmployeeEntity e = employeeDbService.get(id);
        if (e == null)
            out.addError(EMPLOYEE_NOT_FOUND.getErrorText().formatted(id));
        else
            out.addResult(e, this);

        return out;
    }

    public OperationResult get(long page, long lim) {
        OperationResult out = new OperationResult();

        if (page < 0)
            out.addError(RestApiErrors.BAD_PARAM.getErrorText().formatted("page", page));
        if (lim < 1)
            out.addError(RestApiErrors.BAD_PARAM.getErrorText().formatted("limit", lim));

        if (out.hasErrors())
            return out;

        List<EmployeeEntity> eList = employeeDbService.get(page, lim);
        if (eList == null)
            out.addError(DB_ERROR);
        else if (eList.isEmpty())
            out.addError(EMPLOYEE_GET_LIMITS.getErrorText().formatted(page, lim));
        else
            out.addResult(eList, this);

        return out;
    }

    public OperationResult del(long id) {
        OperationResult out = new OperationResult();
        EmployeeEntity deleteEntity = employeeDbService.get(id);

        if (deleteEntity == null)
            out.addError(EMPLOYEE_NOT_FOUND.getErrorText().formatted(id));

        return out.hasErrors()
                ? out
                : del(deleteEntity);
    }

    public OperationResult del(EmployeeEntity e) {
        OperationResult out = new OperationResult();
        if (e.getId() == null) {
            out.addError(ID_IS_NULL);
            return out;
        }

        if (e.getSupervisor() != null)
            addOrRemoveSubForSupervisor(e.getSupervisor(), e.getId(), true);

        if (!e.getSubordinates().isEmpty())
            addOrRemoveSupervisorFor(e.getId(), e.getSubordinates(), true);

        out.addResult(employeeDbService.del(e));
        return out;
    }

    public OperationResult modify(Long id, String newFirstName, String newLastName, String newPosition, Long newSupervisor, Set<Long> newSubordinates) {
        OperationResult out = prapareOperationResult(newFirstName, newLastName, newSupervisor);
        if (id == null)
            out.addError(EMPLOYEES_ID_IS_NULL);

        EmployeeEntity editEntity = employeeDbService.get(id);

        if (editEntity == null)
            out.addError(EMPLOYEE_NOT_FOUND.getErrorText().formatted(id));

        if (out.hasErrors())
            return out;

        if (EmployeeUtils.someChanges(editEntity, newFirstName, newLastName, newPosition, newSupervisor, newSubordinates)) {
            Long oldSupervisorId = editEntity.getSupervisor();
            Set<Long> oldSubordinates = editEntity.getSubordinates();

            EmployeeEntity resultEntity = employeeDbService.modify(
                    EmployeeUtils.updateValues(
                            editEntity.clone(), newFirstName, newLastName, newPosition, newSupervisor, newSubordinates));

            if (resultEntity != null) {
                out.addResult(resultEntity, this);

                if (!Objects.equals(oldSupervisorId, resultEntity.getSupervisor())) {
                    if (oldSupervisorId != null)
                        addOrRemoveSubForSupervisor(oldSupervisorId, resultEntity.getId(), true);

                    if (resultEntity.getSupervisor() != null)
                        addOrRemoveSubForSupervisor(resultEntity.getSupervisor(), resultEntity.getId(), false);
                }

                if (Objects.equals(oldSubordinates, resultEntity.getSubordinates())) {
                    addOrRemoveSupervisorFor(resultEntity.getId(), Sets.difference(oldSubordinates, resultEntity.getSubordinates()), true);
                    addOrRemoveSupervisorFor(resultEntity.getId(), Sets.difference(resultEntity.getSubordinates(), oldSubordinates), false);
                }

            }

            out.addResult(
                    employeeDbService.modify(
                            EmployeeUtils.updateValues(
                                    editEntity.clone(), newFirstName, newLastName, newPosition, newSupervisor, newSubordinates)),
                   this
            );
        }
        else
            out.addError(NO_CHANGES);

        return out;
    }

    public OperationResult getList(Collection<Long> ids) {
        OperationResult out = new OperationResult();

        if (ids == null) {
            out.addError(ValueErrors.ID_LIST_IS_NULL_OR_EMPTY.getErrorText());
            return out;
        }

        Set<Long> resultIds = ids.parallelStream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (resultIds.isEmpty()) {
            out.addError(ValueErrors.ID_LIST_IS_NULL_OR_EMPTY.getErrorText());
            return out;
        }

        List<EmployeeEntity> findEntities = employeeDbService.getList(resultIds);

        if (findEntities == null || findEntities.isEmpty()) {
            out.addError(
                    EMPLOYEE_NOT_FOUND_LIST.getErrorText().formatted(
                            ids.stream()
                                .filter(Objects::nonNull)
                                .map(String::valueOf)
                                .collect(Collectors.joining(","))));
        }
        else
            out.addResult(findEntities, this);


        if (findEntities != null) {
            resultIds.removeAll(findEntities.parallelStream().filter(Objects::nonNull).map(EmployeeEntity::getId).filter(Objects::nonNull).collect(Collectors.toSet()));

            if (!resultIds.isEmpty())
                out.addError(EMPLOYEE_NOT_FOUND_LIST.getErrorText().formatted(
                        resultIds.stream().map(String::valueOf).collect(Collectors.joining(","))));
        }

        return out;
    }

    public Long getTotal() {
        return employeeDbService.getTotal();
    }

    public OperationResult getPossibleSupervisors(Long subId) {
        List<EmployeeEntity> supervisors = employeeDbService.getPossibleSupervisors(subId)
                .stream()
                .filter(e -> !Objects.equals(e.getId(), subId))
                .filter(e -> e.getSupervisor() == null || !Objects.equals(e.getSupervisor(), subId))
                .filter(e -> !e.getSubordinates().contains(subId)).collect(Collectors.toList());

        OperationResult out = new OperationResult();
        out.addResult(supervisors, this);

        return out;
    }

    // TODO : if employee remove - need delete all supervisor and subordinates


    private String canSetSupervisor(long employeeId, long supervisorId) {
        EmployeeEntity employeeEntity = employeeDbService.get(employeeId);

        if (employeeEntity == null)
            return EMPLOYEE_NOT_FOUND.getErrorText().formatted(employeeId);
        else if (employeeEntity.getSupervisor() != null)
            return EMPLOYEE_ALREADY_HAS_SUPERVISOR.getErrorText().formatted(employeeEntity);
        else {
            EmployeeEntity supervisorEntity = employeeDbService.get(supervisorId);

            if (supervisorEntity == null)
                return SUPERVISOR_NOT_FOUND.getErrorText().formatted(employeeId);
            else if (checkAllSubordinates(employeeEntity, supervisorId)) {
                return EMPLOYEE_CAN_NOT_BE_SUPERVISOR.getErrorText().formatted(
                        EmployeeUtils.getFullName(employeeEntity), EmployeeUtils.getFullName(supervisorEntity));
            }
        }

        return ""; // it's ok!
    }

    private boolean checkAllSubordinates(@NonNull EmployeeEntity employee, long id) {
        if (employee.getSubordinates() == null)
            return false;
        else if (employee.getSubordinates().contains(id))
            return true;
        else {
            for (Long subordinateId : employee.getSubordinates()) {
                if (subordinateId == null)
                    continue;
                EmployeeEntity subEnt = employeeDbService.get(subordinateId);
                if (subEnt == null)
                    continue;

                if (checkAllSubordinates(subEnt, id))
                    return true;
            }
        }

        return false;
    }

    private boolean employeeHasSubordinate(@NonNull EmployeeEntity employee, long id) {
        return employee.getSubordinates() == null || employee.getSubordinates().contains(id);
    }

    private OperationResult prapareOperationResult(String firstName, String lastName, Long supervisor) {
        OperationResult out = new OperationResult();

        if (StringUtils.isNullOrEmpty(firstName) || firstName.trim().isEmpty())
            out.addError(EMPLOYEE_NOT_SET_FIRST_NAME);

        if (StringUtils.isNullOrEmpty(lastName) || lastName.trim().isEmpty())
            out.addError(EMPLOYEE_NOT_SET_LAST_NAME);

        if (supervisor != null) {
            EmployeeEntity superVisorEntry = employeeDbService.get(supervisor.intValue());

            if (superVisorEntry == null)
                out.addError(SUPERVISOR_NOT_FOUND.getErrorText().formatted(supervisor));
        }

        return out;
    }

    private void addOrRemoveSubForSupervisor(long supervisorId, long subId, boolean remove) {
        EmployeeEntity superVisor = employeeDbService.get(supervisorId);
        if (superVisor != null) {
            Set<Long> subs = superVisor.getSubordinates();
            if (remove)
                subs.remove(subId);
            else
                subs.add(subId);

            superVisor.setSubordinates(subs);
            employeeDbService.modify(superVisor.clone());
        }
    }

    private void addOrRemoveSupervisorFor(long supervisorId, Set<Long> subIds, boolean remove) {
        for (Long subId : subIds) {
            EmployeeEntity subE = employeeDbService.get(subId);
            if (subE != null) {
                if (remove && Objects.equals(subE.getSupervisor(), supervisorId))
                    subE.setSupervisor(null);
                else if (!remove && subE.getSupervisor() == null)
                    subE.setSupervisor(supervisorId);

                employeeDbService.modify(subE.clone());
            }
        }
    }

}
