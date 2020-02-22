package com.example.demo.dao;

import com.example.demo.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class CustomerDAO {


    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initDB() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS customers(id BIGSERIAL, first_name VARCHAR(255)," +
                "last_name VARCHAR(255), email VARCHAR(255) UNIQUE, user_name VARCHAR(255) UNIQUE," +
                " password VARCHAR(255), is_admin BOOLEAN, admin_requested BOOLEAN)");
    }

    public void insert(Customer customer) {
        jdbcTemplate.update("INSERT INTO customers(first_name, last_name, email, user_name, password, is_admin, admin_requested) VALUES (?,?,?,?,?,?,?)",
                customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getUsername(), customer.getPassword(),false, customer.isAdminRequested());
    }

    public boolean exists(String username) {
        return !jdbcTemplate.queryForList("SELECT * FROM customers WHERE user_name=?", username).isEmpty();
    }


    public Customer select(int id) {
        return jdbcTemplate.queryForObject("SELECT id, first_name, last_name, email, user_name, password, is_admin, admin_requested FROM customers WHERE id = ?",
                new Object[]{id}, new CustomerRowMapper());
    }

    public Customer selectByUsername(String username) {
        return jdbcTemplate.queryForObject("SELECT id, first_name, last_name, email, user_name, password, is_admin, admin_requested FROM customers WHERE user_name = ?",
                new Object[]{username}, new CustomerRowMapper());
    }

    public void delete(int id) {
        String command = "DELETE FROM customers WHERE id =" + id;
        jdbcTemplate.execute(command);
    }

    public void update(Customer customer, int id) {
        jdbcTemplate.update("UPDATE customers SET first_name = ?, last_name = ? WHERE id =?",
                customer.getFirstName(), customer.getLastName(), id);
    }

    public List<Customer> selectAll() {
        return jdbcTemplate.query("SELECT * FROM customers ORDER BY id", new CustomerRowMapper());
    }
}
