package com.example.demo.controllers;

import com.example.demo.Customer;
import com.example.demo.LoginObject;
import com.example.demo.dao.CustomerDAO;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    public CustomerDAO dao;

    @GetMapping("/")
    public String indexGet(Model model) {
        model.addAttribute("loginObject", new LoginObject());
        return "index";
    }

    @PostMapping(path = "/")
    public String loginPost(LoginObject loginObject, Model model, HttpSession session) {
        boolean exists = dao.exists(loginObject.getUsername());
        System.out.println(exists);
        if (!exists) {
            System.out.println("incorrect username or password");
            model.addAttribute("logError", "logError");
            model.addAttribute("loginObject", new LoginObject());
            return "index";
        } else {
            Customer currentCustomer = dao.selectByUsername(loginObject.getUsername());
            System.out.println(currentCustomer.getPassword());
            System.out.println(loginObject.getPassword());
            if (BCrypt.checkpw(loginObject.getPassword(), currentCustomer.getPassword())) {
                session.setAttribute("user", currentCustomer);
                System.out.println("Login success");
                return "redirect:/home";
            } else {
                System.out.println("incorrect username or password");
                model.addAttribute("logError", "logError");
                model.addAttribute("loginObject", new LoginObject());
                return "index";
            }
        }

    }

    @GetMapping("/home")
    public String homeGet(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Customer loggedInCustomer = (Customer) session.getAttribute("user");
        System.out.println(loggedInCustomer.getFirstName());
        System.out.println(loggedInCustomer.isAdmin());
        if (loggedInCustomer.isAdmin()) {
            List<Customer> customer = dao.selectAll();
            model.addAttribute("listOfObjects",customer);
            model.addAttribute("isAdmin", "Set");
        }
        model.addAttribute("loggedInUser", loggedInCustomer);
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "logout";
    }

    @GetMapping("/edit")
    public String editWithId(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Customer loggedInCustomer = (Customer) session.getAttribute("user");
        model.addAttribute("customerObject", loggedInCustomer);
        return "edit";
    }

    @PostMapping(path = "/users/update/{id}")
    public String post(@PathVariable("id") int id, Customer newCustomer) {
        dao.update(newCustomer, id);
        return "success";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteG(@PathVariable int id) {
        dao.delete(id);
        return "success";
    }


    @GetMapping("/signup")
    public String signupGet(Model model) {
        model.addAttribute("newCustomer", new Customer());
        return "signup";
    }

    @GetMapping("/users/{id}")
    public String get(@PathVariable("id") int id, Model model) {
        Customer customer = dao.select(id);
        model.addAttribute("id", customer.getId());
        model.addAttribute("first_name", customer.getFirstName());
        model.addAttribute("last_name", customer.getLastName());
        return "getid";
    }


    @PostMapping(path = "/users")
    public String post(@ModelAttribute Customer newCustomer,HttpSession session) {
        String hashedPw = BCrypt.hashpw(newCustomer.getPassword(), BCrypt.gensalt());
        newCustomer.setPassword(hashedPw);
        dao.insert(newCustomer);
        session.setAttribute("user", newCustomer);
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
