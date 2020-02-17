package com.example.demo.controllers;

import com.example.demo.Customer;
import com.example.demo.dao.CustomerDAO;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    public CustomerDAO dao;

    @GetMapping("/")
    public String indexGet( Model model) {
        List<Customer> customer = dao.selectAll();
        model.addAttribute("listOfObjects",customer);
        return "index";
    }

    @GetMapping("/users/edit/{id}")
    public String editWithId(@PathVariable int id, Model model) {
        Customer customer = dao.select(id);
        model.addAttribute("customerObject",customer);
        return "edit";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteG(@PathVariable int id) {
        dao.delete(id);
        return "success";
    }


    @GetMapping("/signup")
    public String signupGet( Model model) {
        model.addAttribute("newCustomer", new Customer());
        return "signup";
    }

    @GetMapping("/users/{id}")
    public String get(@PathVariable("id") int id, Model model) {
        Customer customer = dao.select(id);
        model.addAttribute("id",customer.getId());
        model.addAttribute("first_name",customer.getFirstName());
        model.addAttribute("last_name",customer.getLastName());
        return "getid";
    }


    @GetMapping("/users/print")
    public String get(@RequestParam(defaultValue = "shiju",required = false)  String name, Model model) {
        model.addAttribute("user",name);
        return "hello";
    }


    @PostMapping(path = "/users")
    public String post(@ModelAttribute Customer newCustomer) {
        dao.insert(newCustomer);
        return "success";
    }

    @PostMapping(path = "/users/update/{id}")
    public String post(@PathVariable("id") int id, Customer newCustomer) {
        dao.update(newCustomer, id);
        return "success";
    }

    @DeleteMapping(path = "/users/{id}")
    public String delete(@PathVariable("id") int id) {
        dao.delete(id);
        return "success";
    }

    @PutMapping(path = "users/{id}")
    public String put(@RequestBody Customer customer, @PathVariable("id") int id) {
        dao.update(customer, id);
        return "Success";
    }
}
