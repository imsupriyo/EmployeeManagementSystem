package com.webflux.ems.service.impl;

import com.webflux.ems.DTO.EmployeeDTO;
import com.webflux.ems.entity.Employee;
import com.webflux.ems.repository.EmployeeRepository;
import com.webflux.ems.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    public Mono<EmployeeDTO> addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = mapDTOtoEntity(employeeDTO);
        Mono<Employee> savedEmployee = employeeRepository.save(employee);
        return savedEmployee.map(this::mapEntityToDTO);
    }

    @Override
    public Mono<EmployeeDTO> getEmployee(String employeeId) {
        Mono<Employee> employee = employeeRepository.findById(employeeId);
        return employee.map(this::mapEntityToDTO);
    }

    @Override
    public Flux<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().map(this::mapEntityToDTO).switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<EmployeeDTO> updateEmployee(String employeeId, EmployeeDTO employeeDTO) {
        Mono<Employee> employee = employeeRepository.findById(employeeId);
        Mono<Employee> updatedEmployee = employee.flatMap(emp ->
        {
            emp.setFirstName(employeeDTO.getFirstName());
            emp.setLastName(employeeDTO.getLastName());
            emp.setEmail(employeeDTO.getEmail());
            return employeeRepository.save(emp);
        });
        return updatedEmployee.map(this::mapEntityToDTO);
    }

    @Override
    public Mono<Void> deleteEmployee(String employeeId) {
        return employeeRepository.deleteById(employeeId);
    }

    /** helper functions */
    private Employee mapDTOtoEntity(EmployeeDTO employeeDTO) {
        return modelMapper.map(employeeDTO, Employee.class);
    }

    private EmployeeDTO mapEntityToDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }
}