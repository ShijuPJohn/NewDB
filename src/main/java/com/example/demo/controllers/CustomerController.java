package com.example.demo.controllers;

import com.example.demo.Customer;
import com.example.demo.LoginObject;
import com.example.demo.dao.CustomerDAO;
import org.apache.catalina.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    public CustomerDAO dao;


    private FacebookConnectionFactory factory = new FacebookConnectionFactory("233585174315729",
            "c2d3b1e92e9b052ea6119d4c79758bac");

    @GetMapping(value = "/useApplication")
    public String producer() {

        OAuth2Operations operations = factory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri("/forwardLogin");
        params.setScope("email,public_profile");

        String url = operations.buildAuthenticateUrl(params);
        System.out.println("The URL is" + url);
        return "redirect:" + url;

    }

    @RequestMapping(value = "/forwardLogin")
    public ModelAndView prodducer(@RequestParam("code") String authorizationCode) {
        OAuth2Operations operations = factory.getOAuthOperations();
        AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, "/forwardLogin",
                null);

        Connection<Facebook> connection = factory.createConnection(accessToken);
        Facebook facebook = connection.getApi();
        String[] fields = {"id", "email", "first_name", "last_name"};
        User userProfile = facebook.fetchObject("me", User.class, fields);
        ModelAndView model = new ModelAndView("details");
        model.addObject("user", userProfile);
        return model;

    }


    @GetMapping("/")
    public String indexGet(Model model) {
        model.addAttribute("loginObject", new LoginObject());
        return "index";
    }

    @PostMapping(path = "/")
    public String loginPost(LoginObject loginObject, Model model, HttpSession session) {
        boolean exists = dao.exists(loginObject.getUsername());
        if (!exists) {
            model.addAttribute("logError", "logError");
            model.addAttribute("loginObject", new LoginObject());
            return "index";
        } else {
            Customer currentCustomer = dao.selectByUsername(loginObject.getUsername());
            if (BCrypt.checkpw(loginObject.getPassword(), currentCustomer.getPassword())) {
                session.setAttribute("user", currentCustomer);
                return "redirect:/home";
            } else {
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
        if (loggedInCustomer.getIsAdmin()) {
            List<Customer> customer = dao.selectAll();
            model.addAttribute("listOfObjects", customer);
            model.addAttribute("isAdmin", "Set");
        }
        model.addAttribute("loggedInCustomer", loggedInCustomer);
        System.out.println(loggedInCustomer.getIsAdmin());
        model.addAttribute("isAdmin", loggedInCustomer.getIsAdmin());
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "logout";
    }

    @GetMapping("/editadmin/{id}")
    public String editAdmin(@PathVariable int id, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Customer loggedInCustomer = (Customer) session.getAttribute("user");
        if (loggedInCustomer != null) {
            Customer customerToEdit = dao.select(id);
            model.addAttribute("CustomerToBeEdited", customerToEdit);
        }
        return "edit_admin";
    }

    @PostMapping(path = "/users/update_admin/{id}")
    public String updateAdmin(@PathVariable("id") int id, @ModelAttribute Customer newCustomer) {
        System.out.println(newCustomer.getIsAdmin());
        System.out.println(newCustomer.getId());
        dao.update(newCustomer, id);
        return "success";
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
    public String deleteG(@PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            dao.delete(id);
        }
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
    public String post(@ModelAttribute Customer newCustomer, HttpSession session) {
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
