package com.sudhindra.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@Builder
@ToString
@AllArgsConstructor
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private Integer salary;
    private String managerId;
    private boolean isCEO;
    private Integer underPaidAmount;
    private Integer overPaidAmount;
    private Integer reportingLineLength;
    private Integer hierarchyLevel;
    private Integer averageLevelSalary;
    private Set<Employee> reportingEmployees;
}
