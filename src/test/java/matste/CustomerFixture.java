package matste;

import java.math.BigDecimal;

import matste.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerFixture {

	@Autowired
	private CustomerService customerService;

	public void insertAliceAndBob() {
		customerService.createCustomer("Alice", BigDecimal.valueOf(10));
		customerService.createCustomer("Bob", BigDecimal.valueOf(20));
	}
}

