package matste;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Slf4j
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

	@Autowired
	private CustomerFixture customerFixture;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@BeforeEach
	void setUp() {
		// Clean up all customers before each test to ensure isolation
		customerService.findAll().forEach(customer -> customerService.deleteById(customer.getId()));
	}

	@Test
	@Transactional
	void shouldCreateAndFindCustomer() {
		// Create a customer
		Customer customer = customerService.createCustomer("John Doe", null);

		assertNotNull(customer.getId());
		assertEquals("John Doe", customer.getName());

		// Find the customer by ID
		Customer foundCustomer = customerService.findById(customer.getId());

		assertNotNull(foundCustomer);
		assertEquals(customer.getId(), foundCustomer.getId());
		assertEquals("John Doe", foundCustomer.getName());
	}

	@Test
	@Transactional
	void shouldFindAllCustomers() {
		// Create multiple customers using fixture
		customerFixture.insertAliceAndBob();

		// Find all customers
		List<Customer> customers = customerService.findAll();

		assertNotNull(customers);
		assertEquals(2, customers.size());
	}

	@Test
	@Transactional
	void shouldDeleteCustomer() {
		// Create a customer
		Customer customer = customerService.createCustomer("Jane Doe", null);
		Long customerId = customer.getId();

		// Delete the customer
		customerService.deleteById(customerId);

		// Verify the customer is deleted
		Customer deletedCustomer = customerService.findById(customerId);
		assertEquals(null, deletedCustomer);
	}

	@Test
	void shouldTransferFunds() {
		log.info("GIVEN");
		Long fromCustomerId = transactionTemplate.execute(status -> {
			Customer fromCustomer = customerService.createCustomer("Alice", new BigDecimal("1000.00"));
			return fromCustomer.getId();
		});
		Long toCustomerId = transactionTemplate.execute(status -> {
			Customer toCustomer = customerService.createCustomer("Bob", new BigDecimal("500.00"));
			return toCustomer.getId();
		});

		log.info("WHEN");

		transactionTemplate.execute(status -> {
			customerService.transferFunds(fromCustomerId, toCustomerId, new BigDecimal("300.00"));
			return null;
		});

		log.info("THEN");
		Customer updatedFromCustomer = customerService.findById(fromCustomerId);
		Customer updatedToCustomer = customerService.findById(toCustomerId);

		assertEquals(new BigDecimal("700.00"), updatedFromCustomer.getBalance());
		assertEquals(new BigDecimal("800.00"), updatedToCustomer.getBalance());
	}

	@Test
	@Transactional
	void shouldThrowExceptionWhenInsufficientFunds() {
		// Create two customers with initial balances
		Customer fromCustomer = customerService.createCustomer("Alice", new BigDecimal("100.00"));
		Customer toCustomer = customerService.createCustomer("Bob", new BigDecimal("500.00"));

		// Attempt to transfer more funds than available
		Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> customerService.transferFunds(fromCustomer.getId(), toCustomer.getId(), new BigDecimal("200.00"))
		);

		assertEquals("Insufficient funds for transfer", exception.getMessage());

		// Verify the balances have not changed
		Customer unchangedFromCustomer = customerService.findById(fromCustomer.getId());
		Customer unchangedToCustomer = customerService.findById(toCustomer.getId());

		assertEquals(new BigDecimal("100.00"), unchangedFromCustomer.getBalance());
		assertEquals(new BigDecimal("500.00"), unchangedToCustomer.getBalance());
	}

	@Test
	void transferFundsForSameCustomer() {
		log.info("GIVEN");
		// Initialize customers in first transaction
		Long customerId = transactionTemplate.execute(status -> {
			Customer fromCustomer = customerService.createCustomer("Alice", new BigDecimal("1000.00"));
			return fromCustomer.getId();
		});

		log.info("WHEN");

		// Execute transfer in separate transaction
		transactionTemplate.execute(status -> {
			customerService.transferFunds(customerId, customerId, new BigDecimal("300.00"));
			return null;
		});

		log.info("THEN");
	}
}

