package matste.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import matste.service.CustomerService;

@Configuration
public class MyApplicationConfig {
	@Bean
	CustomerService customerService() {
		return new CustomerService();
	}
}
/*

class ABCService {
	private final CustomerService customerService;

	ABCService(CustomerService customerService) {
		this.customerService = customerService;
	}
}

@Configuration
class AnotherConfig {

	@Bean
	ABCService abcService() {
		return new ABCService();
	}
}
*/
