package com.itgnostic.test4sandbox.db.dao;

import com.itgnostic.test4sandbox.db.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public interface EmployeeDbService {
    Long add(EmployeeEntity e);
    EmployeeEntity get(long id);
    List<EmployeeEntity> get(long page, long limit);
    List<EmployeeEntity> getPossibleSupervisors(Long subId);

    List<EmployeeEntity> getList(long[] ids);
    List<EmployeeEntity> getList(Collection<Long> ids);

    EmployeeEntity modify(EmployeeEntity e);

    Boolean del(EmployeeEntity e);
    Boolean del(int id);

    Long getLastIndex();
    Long getTotal();

}

