package com.sudhindra;

import com.sudhindra.entity.Employee;
import com.sudhindra.service.impl.EmployeeServiceImpl;
import com.sudhindra.util.FileUtils;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            LOGGER.log(Level.SEVERE, "No arguments provided, Please provide a local file path");
            return;
        } else if (args.length > 1) {
            LOGGER.log(Level.SEVERE, "Please provide only one argument, the local file path");
            return;
        } else {
            LOGGER.log(Level.INFO, "Reading file from path: " + args[0]);
        }

        Map<String, Employee> employeeList = FileUtils
                .readFile(args[0]);

        EmployeeServiceImpl employeeService = new EmployeeServiceImpl();
        employeeService.findManagersEarningLess(employeeList);
        employeeService.findManagersEarningMore(employeeList);
        employeeService.findEmployeesWithLongReportingLine(employeeList);
    }
}