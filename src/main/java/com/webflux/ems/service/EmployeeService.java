package com.webflux.ems.service;

import com.webflux.ems.DTO.EmployeeDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    Mono<EmployeeDTO> addEmployee(EmployeeDTO employeeDTO);

    Mono<EmployeeDTO> getEmployee(String employeeId);

    Flux<EmployeeDTO> getAllEmployees();

    Mono<EmployeeDTO> updateEmployee(String employeeId, EmployeeDTO employeeDTO);

    Mono<Void> deleteEmployee(String employeeId);
}
