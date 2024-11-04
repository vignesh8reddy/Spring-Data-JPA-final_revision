package com.vignesh.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vignesh.Dao.ICustomerRepository;
import com.vignesh.Entity.Customer;

@Service
public class ICustomerManagementServiceImpl implements ICustomerManagementService {

	@Autowired
	private ICustomerRepository customerRepository;
	
	@Override
	public String registerCustomer(Customer customer) {
		// TODO Auto-generated method stub
		System.out.println("Repo's InMemory Proxy class name::"+customerRepository.getClass());
		System.out.println("Repo's InMemory Proxy class implemented interfaces::"+Arrays.toString(customerRepository.getClass().getInterfaces()));
		System.out.println("Repo's InMemory Proxy class implemented Methods::"+Arrays.toString(customerRepository.getClass().getMethods()));
		Customer registeredcustomer = customerRepository.save(customer);
		return registeredcustomer+" is registered.";
	}
	
	/*
	 	@Override
		public String registerCustomerGroup(Iterable<Customer> list) {
		if(list!=null) {
			
			Iterable<Customer> it=custRepo.saveAll(list);
			// get the size of the collection
			int size=((Collection) it).size();
			//get id values of the saved customers
			List<Integer> custIds=(List<Integer>) ((Collection)it).stream().map(cust->((Customer) cust).getCno()).collect(Collectors.toList());
		
			return  size+" no.of customers are registered having the id values::"+custIds.toString();
			
		}
		else {
			throw new IllegalArgumentException(" invalid inputs");
		}
		
	}
	 */
	
	@Override
	public String registerCustomerGroup(Iterable<Customer> customerList) {
		Iterable<Customer> registeredCustomerList = customerRepository.saveAll(customerList);
		return ((Collection)registeredCustomerList).size() + " no. of customers are registered: " + registeredCustomerList.toString();
	}
	
	@Override
	public long countCustomers() {
		return customerRepository.count();
	}
	
	@Override
	public boolean isThereAnyCustomerWithId(Integer cid) {
		return customerRepository.existsById(cid);
	}
	
	@Override
	public String getCustomerById(Integer cid) {
		Optional optionalObj = customerRepository.findById(cid);
		if(optionalObj.isPresent()) return ((Customer)optionalObj.get()).toString();
		return "No customer with id: "+cid;
	}
	
	@Override
	public Customer findCustomerById(Integer cid) {
		return customerRepository.findById(cid).orElseThrow(() -> new IllegalArgumentException("Customer Not Found"));
	}
	
	
	@Override
	public String getAllCustomersByIds(Iterable<Integer> cidList) {
		
		Iterable<Customer> customerList = customerRepository.findAllById(cidList);
		return ((Collection)customerList).toString();
	}
	
	
	@Override
	public String getAllCustomers() {
		Iterable<Customer> customerList = customerRepository.findAll();
		return customerList.toString();
	}
	
	@Override
	public String updateCustomerBillAmt(Integer cid, Float discountPercent) {
		try {
			Customer customer = findCustomerById(cid);//select query is executed
			Double currentBillAmt = customer.getBillamt();
			customer.setBillamt(currentBillAmt - 0.01 * discountPercent * currentBillAmt);
			customer = customerRepository.save(customer);//once again select query is executed
			
			/*
			  spring data jpa checks if the entity already exists in the database.
			  This behavior happens if the Customer entity is not managed by the persistence context 
			  (i.e., it is detached). When you retrieve the entity using findById and modify it outside of a 
			  transactional context, it may become detached, causing JPA to perform a check with another 
			  select query before updating it.
			  
			  To avoid the second select query, ensure that this method runs in a transactional context. 
			  Adding @Transactional to this method will keep the entity managed, so save will not issue a 
			  second select query
			 */
			
			return "Customer bill amount is updated to " + customer.getBillamt() + "from " + currentBillAmt;			
		}
		catch(Exception e) {
			throw e;
		}

	}
	
	@Override
	public String registerOrUpdateCustomer(Customer customer) {
		Optional<Customer> optionalObj = customerRepository.findById(customer.getCid());
		if(optionalObj.isPresent()) {
			//customer already present so just update it
			customer = customerRepository.save(customer);
			return "customer with id " + customer.getCid() + " is updated";
		}		
		//customer is not already present, register
		customer = customerRepository.save(customer);
		return "customer with id " + customer.getCid() + " is registered";
	
	}
	
	@Override
	public String removeCustomerById(Integer id) {
		Optional<Customer> optionalObj = customerRepository.findById(id);
		if(optionalObj.isPresent()) {
			customerRepository.deleteById(id);
			return "Customer with id: " + id + " removed.";
		}
		else
			return "No Customer with id: " + id;
	}
	
	@Override
	public String removeCustomer(Customer customer) {
		Integer id = customer.getCid();
		Optional<Customer> optionalObj = customerRepository.findById(id);
		if(optionalObj.isPresent()) {
			customerRepository.delete(customer);
			/*
			 The above line is deleting the record even though the entire record
			 is not matching with the one in db.
			 It is just deleting if the id is same
			 
			 Then how it is different from deleteById()?
			 */
			return "Customer with id: " + id + " removed.";
		}
		else
			return "No Customer with id: " + id;
	}
	
	@Override
	public String removeAllCustomersByIds(Iterable<Integer> idList) {
		customerRepository.deleteAllById(idList);
		return "Customers with ids: " + idList.toString() + "are removed";
	}
	
	@Override
	public String removeAllCustomers() {
		customerRepository.deleteAll();
		return "All Customers are removed";
	}

}
