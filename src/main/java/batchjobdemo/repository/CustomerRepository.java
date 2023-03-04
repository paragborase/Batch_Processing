package batchjobdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import batchjobdemo.Entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{
    
}
