package com.example.demo.controllers;

import com.example.demo.Customer;
import com.example.demo.dao.CustomerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {
    @Autowired
    public CustomerDAO dao;


    @GetMapping("/users/{id}")
    public Customer get(@PathVariable("id") int id) {
        return dao.select(id);
    }


    @PostMapping(path = "/users")
    public String post(@RequestBody Customer newCustomer) {
        dao.insert(newCustomer);
        return "Success";
    }

    @DeleteMapping(path = "/users/{id}")
    public String delete(@PathVariable("id") int id) {
        dao.delete(id);
        return "Success";
    }

    @PutMapping(path = "users/{id}")
    public String put(@RequestBody Customer customer, @PathVariable("id") int id) {
        dao.update(customer, id);
        return "Success";
    }
}
