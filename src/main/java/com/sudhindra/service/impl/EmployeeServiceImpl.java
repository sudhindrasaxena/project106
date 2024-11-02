package com.sudhindra.service.impl;

import com.sudhindra.entity.Employee;
import com.sudhindra.service.IEmployeeService;
import com.sudhindra.util.FileUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeServiceImpl implements IEmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeServiceImpl.class.getName());
    private Employee ceo = null;
    private Map<Integer, Integer> levelWiseAverageSalary = new HashMap<>();

    @Override
    public List<Employee> findManagersEarningLess(Map<String, Employee> employeeMap) {
        ceo = FileUtils.getCeo();
        this.analyzeHierarchy(employeeMap);
        List<Employee> underPaidManagers = findUnderpaidManagers(employeeMap);
        return underPaidManagers;
    }

    @Override
    public List<Employee> findManagersEarningMore(Map<String, Employee> employeeMap) {
        if (levelWiseAverageSalary.size() == 0)
            this.analyzeHierarchy(employeeMap);
        List<Employee> overPaidManagers = this.findOverpaidManagers(employeeMap);
        return overPaidManagers;
    }

    @Override
    public List<Employee> findEmployeesWithLongReportingLine(Map<String, Employee> employeeMap) {
        return this.checkReportingLines(2, employeeMap);
    }

    private List<Employee> findOverpaidManagers(Map<String, Employee> employeeMap) {
        List<Employee> result = new ArrayList<>();
        Integer averageSalary;
        for (Map.Entry<String, Employee> entry : employeeMap.entrySet()) {
            Employee emp = entry.getValue();
            averageSalary = levelWiseAverageSalary.get(emp.getHierarchyLevel());
            if (emp.getHierarchyLevel() != 0 && emp.getSalary() > averageSalary) {
                emp.setOverPaidAmount(emp.getSalary() - averageSalary);
                result.add(emp);
                System.out.printf("%s(%s) is overpaid by ₹%d%n", emp.getFirstName() + " " + emp.getLastName(), emp.getId(), emp.getOverPaidAmount());
            }
        }
        return result;
    }

    // Helper method to find underpaid managers
    private List<Employee> findUnderpaidManagers(Map<String, Employee> employeeMap) {
        List<Employee> result = new ArrayList<>();
        Integer averageSalary;
        for (Map.Entry<String, Employee> entry : employeeMap.entrySet()) {
            Employee emp = entry.getValue();
            averageSalary = levelWiseAverageSalary.get(emp.getHierarchyLevel());
            if (emp.getSalary() < averageSalary && emp.getHierarchyLevel() != 0) {
                emp.setUnderPaidAmount(averageSalary - emp.getSalary());
                result.add(emp);
                System.out.printf("%s(%s) is underpaid by ₹%d%n", emp.getFirstName() + " " + emp.getLastName(), emp.getId(), emp.getUnderPaidAmount());
            }
        }
        return result;
    }

    // Helper method to find employees with long reporting lines
    public List<Employee> checkReportingLines(int threshold, Map<String, Employee> employeeMap) {
        List<Employee> result = new ArrayList<>();
        for (Employee employee : employeeMap.values()) {
            int lineLength = getReportingLineLength(employee, employeeMap); // Calculate reporting line length
            if (lineLength > threshold) {
                employee.setReportingLineLength(lineLength);
                result.add(employee);
                System.out.printf("%s has a reporting line that is too long by %d levels%n",
                        employee.getFirstName() + " " + employee.getLastName(),
                        lineLength);
            }
        }
        return result;
    }

    // Helper method to compute the reporting line length (depth) for each employee
    private int getReportingLineLength(Employee employee, Map<String, Employee> employeeMap) {
        int length = 0;
        String managerId = employee.getManagerId();

        // Traverse up the hierarchy until we reach the CEO (who has no manager)
        while (managerId != null && employeeMap.containsKey(managerId)) {
            length++;
            managerId = employeeMap.get(managerId).getManagerId();
        }

        return length;
    }

    // Analyze the hierarchy to find average salaries at each level
    public void analyzeHierarchy(Map<String, Employee> employeeMap) {
        if (ceo == null) {
            LOGGER.log(Level.SEVERE, "CEO not found in the dataset.");
            return;
        }

        // Map to store level data (salaries at each level)
        Map<Integer, Set<Integer>> levelSalaries = new HashMap<>();
        int maxDepth = 0;

        // Level-order traversal (BFS) to calculate depth and salary averages
        Queue<Employee> queue = new LinkedList<>();
        queue.add(ceo);
        int level = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            Set<Integer> salaries = new HashSet<>();

            for (int i = 0; i < levelSize; i++) {
                Employee current = queue.poll();
                current.setHierarchyLevel(level);
                salaries.add(current.getSalary());

                // Add reports to the queue for next level
                queue.addAll(current.getReportingEmployees());
            }

            // Store salaries at this level
            levelSalaries.put(level, salaries);

            // Update max depth
            maxDepth = Math.max(maxDepth, level);
            level++;
        }

        // Output results
        for (Map.Entry<Integer, Set<Integer>> entry : levelSalaries.entrySet()) {
            double avgSalary = entry.getValue().stream().mapToDouble(Integer::intValue).average().orElse(0);
            levelWiseAverageSalary.put(entry.getKey(), (int) avgSalary);
        }
    }
}
