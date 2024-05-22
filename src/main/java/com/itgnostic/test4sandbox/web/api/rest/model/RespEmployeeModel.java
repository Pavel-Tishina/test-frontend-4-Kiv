package com.itgnostic.test4sandbox.web.api.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespEmployeeModel {
    String id;
    String firstName;
    String lastName;
    String fullName;
    String position;
    String supervisorFullName;
    String supervisorId;
    String created;
}
