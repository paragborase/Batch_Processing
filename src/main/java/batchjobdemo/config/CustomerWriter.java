package batchjobdemo.config;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import batchjobdemo.Entity.Customer;
import batchjobdemo.repository.CustomerRepository;

@Component
public class CustomerWriter implements ItemWriter<Customer> {

    private CustomerRepository customerRepository;

    @Override
    public void write(List<? extends Customer> list) throws Exception {
        System.out.println("Thread Name: "+Thread.currentThread().getName());
        customerRepository.saveAll(list);
    }
    
}
