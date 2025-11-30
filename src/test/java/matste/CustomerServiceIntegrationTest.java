package matste;

import matste.entity.Customer;
import matste.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
public class CustomerServiceIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		// Clean up all customers before each test to ensure isolation
		customerService.findAll().forEach(customer -> customerService.deleteById(customer.getId()));
	}

	@Test
	void shouldCreateAndFindCustomer() {
		// Create a customer
		Customer customer = customerService.createCustomer("John Doe");

		assertNotNull(customer.getId());
		assertEquals("John Doe", customer.getName());

		// Find the customer by ID
		Customer foundCustomer = customerService.findById(customer.getId());

		assertNotNull(foundCustomer);
		assertEquals(customer.getId(), foundCustomer.getId());
		assertEquals("John Doe", foundCustomer.getName());
	}

	@Test
	void shouldFindAllCustomers() {
		// Create multiple customers
		customerService.createCustomer("Alice");
		customerService.createCustomer("Bob");

		// Find all customers
		List<Customer> customers = customerService.findAll();

		assertNotNull(customers);
		assertEquals(2, customers.size());
	}

	@Test
	void shouldDeleteCustomer() {
		// Create a customer
		Customer customer = customerService.createCustomer("Jane Doe");
		Long customerId = customer.getId();

		// Delete the customer
		customerService.deleteById(customerId);

		// Verify the customer is deleted
		Customer deletedCustomer = customerService.findById(customerId);
		assertEquals(null, deletedCustomer);
	}
}

