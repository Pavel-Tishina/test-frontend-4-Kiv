package com.itgnostic.test4sandbox.errors;

public enum DbErrors {
    ID_IS_NULL("ID can't be null"),
    EMPLOYEES_ID_IS_NULL("Employee's ID can't be null"),
    EMPLOYEE_NOT_SET_FIRST_NAME("First Name can't be null or empty"),
    EMPLOYEE_NOT_SET_LAST_NAME("Last Name can't be null or empty"),
    EMPLOYEE_NOT_FOUND("Not found Employee by id '%d'"),
    EMPLOYEE_GET_LIMITS("Not found Employee by page '%d' and limit '%d'"),
    EMPLOYEE_NOT_FOUND_LIST("Not found any Employees by ids '%s'"),
    EMPLOYEE_ALREADY_EXIST("Employee with id '%d' already exist"),
    EMPLOYEE_ALREADY_HAS_SUPERVISOR("Employee '%s' already has a Supervisor"),
    EMPLOYEE_CAN_NOT_BE_SUPERVISOR("Employee '%s' can not be a Supervisor for '%s'"),
    SUPERVISOR_NOT_FOUND("Not found Supervisor by id '%d'"),
    SUPERVISOR_SUBORDINATES_NOT_FOUND("Not found Subordinate by id '%d' for Supervisor '%s'"),
    BAD_RESULT("Some result of operation is bad"),
    DB_ERROR("Error with database connection"),
    DB_SAVE_NEW_ERROR("Error with save new record"),
    NO_CHANGES("No changes");

    private String error;

    DbErrors(String error) {
        this.error = error;
    }

    public String getErrorText() {
        return error;
    }

}
