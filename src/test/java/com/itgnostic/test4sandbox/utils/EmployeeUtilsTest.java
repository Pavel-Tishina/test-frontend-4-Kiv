package com.itgnostic.test4sandbox.utils;

import com.itgnostic.test4sandbox.db.entity.EmployeeEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Set;

import static com.itgnostic.test4sandbox.common.Const.DT;
import static org.junit.Assert.*;

public class EmployeeUtilsTest {
    private static final EmployeeEntity EMPLOYEE = new EmployeeEntity();

    @Before
    public void init() {
        EMPLOYEE.setFirstName("Thomas");
        EMPLOYEE.setFirstName("Edison");
        EMPLOYEE.setPosition("Apple's salesman");

        EMPLOYEE.setSupervisor(10L);
        EMPLOYEE.setSubordinates(Set.of(1L, 2L, 3L));
    }

    @Test
    public void getCreatedDateTest() {
        EmployeeEntity e = new EmployeeEntity();
        Date now = new Date();

        // Actually may be not... but time is soooo clooose ^^
        assertEquals(now.getTime() == e.getCreated().getTime(),
                DT.format(now).equals(EmployeeUtils.getCreatedDate(e)));
    }

    @Test
    public void someChangesTest() {
        assertFalse(EmployeeUtils.someChanges(
                EMPLOYEE, EMPLOYEE.getFirstName(), EMPLOYEE.getLastName(), EMPLOYEE.getPosition(),
                EMPLOYEE.getSupervisor(), EMPLOYEE.getSubordinates()));

        assertTrue(EmployeeUtils.someChanges(
                EMPLOYEE, EMPLOYEE.getFirstName(), EMPLOYEE.getLastName(), EMPLOYEE.getPosition(),
                EMPLOYEE.getSupervisor(), null));
    }



}
