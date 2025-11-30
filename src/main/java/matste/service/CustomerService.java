package matste.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import matste.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

	@PersistenceContext
	private EntityManager entityManager;

	public Customer createCustomer(String name) {
		Customer customer = new Customer(name);
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

