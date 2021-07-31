package com.jinu.mdatasouce.dto.request;

import lombok.Data;

@Data
public class AddEmployeeRequest {
	private String firstName;
	private String lastName;
	private String email;
}
