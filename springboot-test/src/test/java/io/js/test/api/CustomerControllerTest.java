package io.js.test.api;

import io.js.test.domain.Customer;
import io.js.test.domain.CustomerService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @MockBean
    CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllCustomers() throws Exception {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1L, "jagdish@gmail.com", "Jagdish"));
        customers.add(new Customer(2L, "gul@gmail.com", "Gul"));

        given(customerService.getAllCustomers()).willReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].email", CoreMatchers.equalTo("jagdish@gmail.com")))
                .andExpect(jsonPath("$[0].name", CoreMatchers.equalTo("Jagdish")))
                .andExpect(jsonPath("$[1].email", CoreMatchers.equalTo("gul@gmail.com")))
                .andExpect(jsonPath("$[1].name", CoreMatchers.equalTo("Gul")));
    }
}