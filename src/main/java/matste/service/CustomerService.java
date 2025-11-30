package matste.service;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import matste.entity.Customer;

@Service
@Transactional
public class CustomerService {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void transferFunds(Long fromCustomerId, Long toCustomerId, BigDecimal amount) {
		Customer fromCustomer = requireNonNull(entityManager.find(Customer.class, fromCustomerId));
		Customer toCustomer = requireNonNull(entityManager.find(Customer.class, toCustomerId));

		if (fromCustomer.getBalance().compareTo(amount) < 0) {
			throw new IllegalArgumentException("Insufficient funds for transfer");
		}
		fromCustomer.setBalance(fromCustomer.getBalance().subtract(amount));
		toCustomer.setBalance(toCustomer.getBalance().add(amount));
	}

	public Customer createCustomer(String name, BigDecimal balance) {
		Customer customer = new Customer(name);
		customer.setBalance(balance);
		entityManager.persist(customer);
		return customer;
	}

	public Customer findById(Long id) {
		return entityManager.find(Customer.class, id);
	}

	public List<Customer> findAll() {
		return entityManager.createQuery("SELECT c FROM Customer c", Customer.class)
				.getResultList();
	}

	public void deleteById(Long id) {
		Customer customer = findById(id);
		if (customer != null) {
			entityManager.remove(customer);
		}
	}
}

