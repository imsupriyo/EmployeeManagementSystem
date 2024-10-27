package com.webflux.ems.controller;

import com.webflux.ems.DTO.EmployeeDTO;
import com.webflux.ems.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = EmployeeController.class)
public class EmployeeControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmployeeService employeeService;

    @DisplayName("test to add employee")
    @Test
    public void givenEmployeeObject_whenAddEmployee_thenReturnSavedEmployee() {
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyo@gmail.com")
                .build();

        BDDMockito.given(employeeService.addEmployee(any(EmployeeDTO.class)))
                .willReturn(Mono.just(employeeDTO));

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
                .id("a string value")
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyo@gmail.com")
                .build();

        BDDMockito.given(employeeService.getEmployee(anyString()))
                .willReturn(Mono.just(employeeDTO));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees/{id}", employeeDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(employeeDTO.getId())
                .jsonPath("$.firstName").isEqualTo(employeeDTO.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDTO.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDTO.getEmail());
    }

    @DisplayName("test to get all employees")
    @Test
    public void givenListOfEmployee_whenGetAllEmployee_thenReturnListOfEmployee() {
        EmployeeDTO employeeDTO1 = EmployeeDTO.builder()
                .id("some string value")
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyo@gmail.com")
                .build();

        EmployeeDTO employeeDTO2 = EmployeeDTO.builder()
                .id("some string value 2")
                .firstName("Pappu")
                .lastName("Pal")
                .email("pappu@gmail.com")
                .build();

        BDDMockito.given(employeeService.getAllEmployees())
                .willReturn(Flux.just(employeeDTO1, employeeDTO2));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[1].id").isEqualTo(employeeDTO2.getId())
                .jsonPath("$[1].firstName").isEqualTo(employeeDTO2.getFirstName())
                .jsonPath("$[1].lastName").isEqualTo(employeeDTO2.getLastName())
                .jsonPath("$[1].email").isEqualTo(employeeDTO2.getEmail());
    }

    @DisplayName("test to update an employee")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        EmployeeDTO responseDTO = EmployeeDTO.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyopal@gmail.com")
                .build();

        BDDMockito.given(employeeService.updateEmployee(anyString(), any(EmployeeDTO.class)))
                .willReturn(Mono.just(responseDTO));

        WebTestClient.ResponseSpec response = webTestClient.put().uri("/api/employees/{id}", "a valid id")
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
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .firstName("Supriyo")
                .lastName("Pal")
                .email("supriyopal@gmail.com")
                .build();

        BDDMockito.given(employeeService.deleteEmployee(anyString()))
                .willReturn(Mono.empty());

        WebTestClient.ResponseSpec response = webTestClient.delete().uri("/api/employees/{id}", "a valid id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);
    }
}
