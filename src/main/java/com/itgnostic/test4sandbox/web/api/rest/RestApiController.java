package com.itgnostic.test4sandbox.web.api.rest;

import com.itgnostic.test4sandbox.errors.RestApiErrors;
import com.itgnostic.test4sandbox.errors.ValueErrors;
import com.itgnostic.test4sandbox.web.api.rest.model.ReqEmployeeModel;
import com.itgnostic.test4sandbox.service.EmployeeService;
import com.itgnostic.test4sandbox.service.OperationResult;
import com.itgnostic.test4sandbox.utils.JsonUtils;
import com.itgnostic.test4sandbox.utils.RestApiUtils;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.itgnostic.test4sandbox.errors.RestApiErrors.*;

@CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/api")
public class RestApiController {
    private List<String> errors = new ArrayList<>();

    @Autowired
    private EmployeeService employeeService;

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee", method = RequestMethod.GET)
    public ResponseEntity<String> getEmployee(@RequestParam(value = "id") String id) {
        Long _id = null;
        errors = new ArrayList<>();

        if (Strings.isBlank(id))
            errors.add(NO_PARAM_VALUE.getErrorText().formatted("id"));
        else if (!id.matches("\\d+"))
            errors.add(BAD_PARAM.getErrorText().formatted("id", id));
        else
            _id = RestApiUtils.parseLong(id);

        if (!errors.isEmpty())
            return badResponse(HttpStatus.PRECONDITION_FAILED);

        OperationResult result = employeeService.get(_id);

        if (result.hasErrors())
            errors.add(result.getErrorDetails());

        return result.isSuccess()
                ? okResponse(result)
                : badResponse(HttpStatus.NOT_FOUND, result);
    }

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee", method = RequestMethod.PUT)
    public ResponseEntity<String> putEmployee(@RequestBody ReqEmployeeModel updEmployee) {
        Set<Long> subs = RestApiUtils.split2SetLong(updEmployee.getSubordinates());
        Long employeeId = RestApiUtils.parseLong(updEmployee.getId());
        Long supervisorId = RestApiUtils.parseLong(updEmployee.getSupervisor());

        errors = new ArrayList<>();

        if (Strings.isBlank(updEmployee.getId()))
            errors.add(NO_PARAM_VALUE.getErrorText().formatted("id"));
        else if (!updEmployee.getId().matches("\\d+"))
            errors.add(BAD_PARAM.getErrorText().formatted("id", updEmployee.getId()));
        else if (Objects.equals(employeeId, supervisorId))
            errors.add(ValueErrors.SUPERVISOR_ID_SAME_WITH_EMPLOYEE_ID.getErrorText());

        if (subs.contains(employeeId))
            errors.add(ValueErrors.EMPLOYEE_ID_IN_SUBS.getErrorText());

        if (subs.contains(supervisorId))
            errors.add(ValueErrors.SUPERVISOR_ID_IN_SUBS.getErrorText());

        if (Strings.isBlank(updEmployee.getFirstName()))
            errors.add(NO_PARAM_VALUE.getErrorText().formatted("firstName"));
        if (Strings.isBlank(updEmployee.getLastName()))
            errors.add(NO_PARAM_VALUE.getErrorText().formatted("lastName"));

        if (Strings.isNotBlank(updEmployee.getSupervisor()) && !updEmployee.getSupervisor().matches("\\d+"))
            errors.add(BAD_PARAM.getErrorText().formatted("supervisor", updEmployee.getSupervisor()));

        if (!errors.isEmpty())
            return badResponse(HttpStatus.PRECONDITION_FAILED);

        OperationResult result = employeeService.modify(
                RestApiUtils.parseLong(updEmployee.getId()),
                updEmployee.getFirstName(),
                updEmployee.getLastName(),
                updEmployee.getPosition(),
                RestApiUtils.parseLong(updEmployee.getSupervisor()),
                RestApiUtils.split2SetLong(updEmployee.getSubordinates()));

        if (result.hasErrors())
            errors.add(result.getErrorDetails());

        return result.isSuccess()
                ? okResponse(result)
                : badResponse(HttpStatus.NOT_FOUND, result);
    }

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee", method = RequestMethod.POST)
    public ResponseEntity<String> postEmployee(@RequestBody ReqEmployeeModel newEmployee) {
        errors = new ArrayList<>();

        if (Strings.isBlank(newEmployee.getFirstName()))
            errors.add(NO_PARAM_VALUE.getErrorText().formatted("firstName"));
        if (Strings.isBlank(newEmployee.getLastName()))
            errors.add(NO_PARAM_VALUE.getErrorText().formatted("lastName"));

        if (!errors.isEmpty())
            return badResponse(HttpStatus.PRECONDITION_FAILED);

        Long supervisor = Objects.isNull(newEmployee.getSupervisor()) || !newEmployee.getSupervisor().matches("\\d+")
                ? null : Long.parseLong(newEmployee.getSupervisor());

        OperationResult result = employeeService.add(
                newEmployee.getFirstName(), newEmployee.getLastName(),
                newEmployee.getPosition(),  supervisor);

        if (result.hasErrors())
            errors.add(result.getErrorDetails());

        return errors.isEmpty() && !result.getResultList().isEmpty()
                ? ResponseEntity.ok(new JSONObject().put("id", result.getResultList().get(0).getId()).toString())
                : badResponse(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee", method = RequestMethod.DELETE)
    public ResponseEntity<String> delEmployee(@RequestParam(value = "id") String id) {
        errors = new ArrayList<>();


        if (id == null)
            errors.add(NO_PARAM.getErrorText().formatted("id"));
        else if (!id.matches("\\d+"))
            errors.add(BAD_PARAM.getErrorText().formatted("id", id));

        if (!errors.isEmpty())
            return badResponse(HttpStatus.PRECONDITION_FAILED);

        long[] _id = RestApiUtils.getLongParamsAsArray(id, true);

        OperationResult result = employeeService.del(_id[0]);
        if (result.hasErrors())
            errors.add(result.getErrorDetails());

        return result.isSuccess()
                ? ResponseEntity.ok(new JSONObject().put("result", "User with id '%d' was deleted".formatted(_id[0])).toString())
                : badResponse(HttpStatus.NOT_FOUND, result);
    }

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee/list", method = RequestMethod.GET)
    public ResponseEntity<String> getEmployeesList(@RequestParam(value = "ids") String ids) {
        errors = new ArrayList<>();
        Set<Long> _ids = RestApiUtils.split2SetLong(ids);

        if (ids == null)
            errors.add(NO_PARAM.getErrorText().formatted("ids"));
        else if (!ids.matches("(\\d+,?)+"))
            errors.add(BAD_PARAM.getErrorText().formatted("ids", ids));
        else if (_ids.isEmpty())
            errors.add(NO_PARAM_VALUE.getErrorText().formatted("ids"));

        if (!errors.isEmpty())
            return badResponse(HttpStatus.PRECONDITION_FAILED);

        OperationResult result = employeeService.getList(_ids);

        if (result.hasErrors())
            errors.add(result.getErrorDetails());

        return result.isSuccess()
                ? okResponse(result)
                : badResponse(HttpStatus.NOT_FOUND, result);
    }

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee/page", method = RequestMethod.GET)
    public ResponseEntity<String> getEmployeesPage(@RequestParam(value = "p") String p, @RequestParam(value = "lim") String lim) {
        errors = new ArrayList<>();
        Long page = null;
        Long limit = null;

        if (Strings.isBlank(p))
            errors.add(NO_PARAM.getErrorText().formatted("p"));
        else if (!p.matches("\\d+"))
            errors.add(RestApiErrors.BAD_PARAM.getErrorText().formatted("p", p));
        else
            page = Long.parseLong(p);

        if (Strings.isBlank(lim))
            errors.add(NO_PARAM.getErrorText().formatted("lim"));
        else if (!lim.matches("\\d+"))
            errors.add(RestApiErrors.BAD_PARAM.getErrorText().formatted("lim", lim));
        else
            limit = Long.parseLong(lim);

        if (page == null || lim == null)
            return badResponse(HttpStatus.PRECONDITION_FAILED);

        OperationResult result = employeeService.get(page, limit);
        if (result.hasErrors())
            errors.add(result.getErrorDetails());

        return result.isSuccess()
                ? okResponse(result)
                : badResponse(HttpStatus.NOT_FOUND, result);
    }

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee/total", method = RequestMethod.GET)
    public ResponseEntity<String> getTotal() {
        Long result = employeeService.getTotal();
        return result != null && result >= 0
                ? ResponseEntity.ok().body(new JSONObject().put("total", result).toString())
                : badResponse(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "https://localhost:3000", maxAge = 3600)
    @RequestMapping(value = "/employee/supervisors", method = RequestMethod.GET)
    public ResponseEntity<String> getPossibleSupervisors(@RequestParam(value = "id") String id) {
        Long _id = RestApiUtils.parseLong(id);

        errors = new ArrayList<>();
        OperationResult result = employeeService.getPossibleSupervisors(_id);

        return result.isSuccess() && !result.getResultList().isEmpty()
                ? okResponse(result)
                : badResponse(HttpStatus.NOT_FOUND);
    }

    // TODO
    // need?
    @RequestMapping(value = "/employee/subordinates", method = RequestMethod.GET)
    public ResponseEntity<String> getEmployeeSubordinates(@RequestParam(value = "id") String id) {
        return null;
    }

    private ResponseEntity<String> badResponse(HttpStatus status) {
        return ResponseEntity.status(status).body(new JSONObject().put("errors", new JSONArray(errors)).toString());
    }

    private ResponseEntity<String> badResponse(HttpStatus status, OperationResult result) {
        return ResponseEntity.status(status).body(JsonUtils.operationResultToJson(result, errors).toString());
    }

    private ResponseEntity<String> okResponse(OperationResult result) {
        return ResponseEntity.ok().body(JsonUtils.operationResultToJson(result, errors).toString());
    }

}
