package com.itgnostic.test4sandbox.db;

import com.itgnostic.test4sandbox.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbInit {
    @Autowired
    private EmployeeService employeeService;

    @PostConstruct
    public void init() {
        Long total = employeeService.getTotal();
        if (total == null || total == 0) {
            employeeService.add("John", "Doe", "Manager", null);
            employeeService.add("Jane", "Smith", "Assistant Manager", 1L);
            employeeService.add("Alice", "Johnson", "Team Leader", 1L);
        }
    }

}
