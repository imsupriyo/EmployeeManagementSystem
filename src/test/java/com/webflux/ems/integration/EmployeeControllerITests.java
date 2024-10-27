package com.webflux.ems.integration;

import com.webflux.ems.DTO.EmployeeDTO;
import com.webflux.ems.entity.Employee;
import com.webflux.ems.repository.EmployeeRepository;
import com.webflux.ems.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerITests {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private EmployeeRepository employeeRepository;

    // this value is used in other tests
    private String EMPLOYEE_ID;

    @BeforeEach
    public void setup() {
        // database should be empty before each test
        employeeRepository.deleteAll().subscribe();

        Employee employee = Employee.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyo@gmail.com")
                .build();
        Employee savedEmployee = employeeRepository.save(employee).block();
        assertNotNull(savedEmployee);

        EMPLOYEE_ID = savedEmployee.getId();
    }

    @DisplayName("test to add employee")
    @Test
    public void givenEmployeeObject_whenAddEmployee_thenReturnSavedEmployee() {
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyo@gmail.com")
                .build();

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeDTO), EmployeeDTO.class)
                .exchange();

        response.expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDTO.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDTO.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDTO.getEmail());
    }

    @DisplayName("test to get employee by Id")
    @Test
    public void givenEmployeeId_whenGetEmployee_thenReturnEmployee() {
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyo@gmail.com")
                .build();

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees/{id}", EMPLOYEE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(EMPLOYEE_ID)
                .jsonPath("$.firstName").isEqualTo(employeeDTO.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDTO.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDTO.getEmail());
    }


    @DisplayName("test to get all employees")
    @Test
    public void givenListOfEmployee_whenGetAllEmployee_thenReturnListOfEmployee() {
        Employee employee1 = Employee.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyo@gmail.com")
                .build();

        Employee employee2 = Employee.builder()
                .firstName("Pappu")
                .lastName("Pal")
                .email("pappu@gmail.com")
                .build();

        employeeRepository.deleteAll()
                .then(employeeRepository.save(employee1))
                .then(employeeRepository.save(employee2))
                .block();

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.size()").isEqualTo(2);
    }

    @DisplayName("test to update an employee")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        EmployeeDTO responseDTO = EmployeeDTO.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyopal@gmail.com")
                .build();

        WebTestClient.ResponseSpec response = webTestClient.put().uri("/api/employees/{id}", EMPLOYEE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(responseDTO), EmployeeDTO.class)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(responseDTO.getFirstName())
                .jsonPath("$.lastName").isEqualTo(responseDTO.getLastName())
                .jsonPath("$.email").isEqualTo(responseDTO.getEmail());
    }

    @DisplayName("test to delete an employee")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnNothing() {
        WebTestClient.ResponseSpec response = webTestClient.delete().uri("/api/employees/{id}", EMPLOYEE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);
    }
}

