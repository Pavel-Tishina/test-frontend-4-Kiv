package com.itgnostic.test4sandbox.web.api.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqEmployeeModel {
    String id;
    String firstName;
    String lastName;
    String position;
    String supervisor;
    String subordinates;
}
