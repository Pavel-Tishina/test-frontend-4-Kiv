package com.itgnostic.test4sandbox.db.entity;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.*;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeEntityTest {
    private static final EmployeeEntity e1 = new EmployeeEntity();
    private static final EmployeeEntity e2 = new EmployeeEntity();

    // test draft-entities
    @Test
    public void t0_testConstructor() throws InterruptedException {
        // created in one time draft-entities
        assertTrue(e1.equals(e2));

        try {
            Thread.sleep(1); // Wait 0,001 sec
        } catch (InterruptedException e) {
            throw e; // Sorry, but I can't wait!!! Hurry up!!!
        }

        EmployeeEntity laterE = new EmployeeEntity();
        assertFalse(e1.equals(laterE));
    }

    @Test
    public void t1_equalsTest() {
        assertTrue(e1.equals(e2));

        e2.setPosition("");
        assertFalse(e1.equals(e2));

        Set<Integer> d1 = Set.of(6, 1, 3, 4);
        Collection<Integer> d2 = Arrays.asList(6, 1, 3, 4);


        System.out.println(Objects.deepEquals(d1, d2));
    }
}
