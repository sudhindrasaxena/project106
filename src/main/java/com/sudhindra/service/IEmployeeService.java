package com.sudhindra.service;

import com.sudhindra.entity.Employee;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {
    List<Employee> findManagersEarningLess(Map<String, Employee> employeeMap);

    List<Employee> findManagersEarningMore(Map<String, Employee> employeeMap);

    List<Employee> findEmployeesWithLongReportingLine(Map<String, Employee> employeeList);
}
