package matste.controller;

import matste.entity.Customer;
import matste.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping
	public List<Customer> getAllCustomers() {
		return customerService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
		Customer customer = customerService.findById(id);
		if (customer != null) {
			return ResponseEntity.ok(customer);
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public Customer createCustomer(@RequestBody CreateCustomerRequest request) {
		return customerService.createCustomer(request.name(), request.balance());
	}

	public record CreateCustomerRequest(String name, BigDecimal balance) {
	}
}

