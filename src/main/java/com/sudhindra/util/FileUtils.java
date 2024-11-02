package com.sudhindra.util;

import com.sudhindra.entity.Employee;
import com.sudhindra.entity.Headers;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {
    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());
    private static Employee ceo;
    private static Map<String, Employee> employeeMap = new HashMap<>();

    public static Employee getCeo() {
        return ceo;
    }

    public static Map<String, Employee> readFile(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader(Headers.class)
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);

            Set<Employee> reportingEmployees;
            for (CSVRecord record : records) {
                reportingEmployees = new HashSet<>();
                Employee employee = Employee.builder()
                        .id(record.get(Headers.ID))
                        .firstName(record.get(Headers.FIRST_NAME))
                        .lastName(record.get(Headers.LAST_NAME))
                        .salary(Integer.parseInt(record.get(Headers.SALARY)))
                        .managerId(record.get(Headers.MANAGER_ID))
                        .reportingEmployees(reportingEmployees)
                        .build();
                if (employee.getManagerId() == null || employee.getManagerId().isEmpty()) {
                    employee.setCEO(true);
                    ceo = employee;
                }
                if (employeeMap.containsKey(employee.getManagerId())) {
                    employeeMap.get(employee.getManagerId()).getReportingEmployees().add(employee);
                }
                employeeMap.put(record.get(Headers.ID), employee);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading file: " + e.getMessage());
        }
        return fillReportingEmployees(employeeMap);
    }

    public static Map<String, Employee> fillReportingEmployees(Map<String, Employee> employeeMap) {
        employeeMap.entrySet().stream()
                .filter(entry -> employeeMap.containsKey(entry.getValue().getManagerId()))
                .forEach(entry -> employeeMap.get(entry.getValue().getManagerId()).getReportingEmployees().add(entry.getValue()));
        return employeeMap;
    }
}
