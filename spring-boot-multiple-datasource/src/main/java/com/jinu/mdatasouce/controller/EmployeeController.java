package com.jinu.mdatasouce.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jinu.mdatasouce.dto.request.AddEmployeeRequest;
import com.jinu.mdatasouce.employee.model.Employee;
import com.jinu.mdatasouce.employee.repository.EmployeeRepository;
import com.jinu.mdatasouce.user.model.User;
import com.jinu.mdatasouce.user.repository.UserRepository;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private UserRepository userRepository;
    @PostMapping("/addemployee")
    @ResponseBody
	public String addEmployee(@RequestBody AddEmployeeRequest request) {
		Employee employee = new Employee();
		employee.setFirstName(request.getFirstName());
		employee.setLastName(request.getLastName());
		employee.setEmail(request.getEmail());
		employeeRepository.save(employee);
		return "Employee created with id:"+employee.getUserId();
	}
    @GetMapping("/addEmployees")
	public String addEmployees() {
    	for(int i=1;i<20;i++) {
    		Employee employee = new Employee();
    		employee.setFirstName("Employee"+i);
    		employee.setLastName("");
    		employee.setEmail("employee"+i+"gmail.com");
    		employee = employeeRepository.save(employee);
    		System.out.println("Employee id:"+employee.getUserId());
    	}
    	return "employees added";
	}
    @GetMapping("/getEmployee")
    @ResponseBody
	public Employee getEmployee(@RequestParam ("employeeId")long employeeId) {
    	Optional<Employee> employee = employeeRepository.findById((long)employeeId);
    	if(employee.isPresent()){
    		return employee.get();
    	}
    	return null;
	}
}
