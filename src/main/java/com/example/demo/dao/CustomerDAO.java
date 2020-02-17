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
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS customers(id BIGSERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
    }

    public void insert(Customer customer) {
        jdbcTemplate.update("INSERT INTO customers(first_name, last_name) VALUES (?,?)",
                customer.getFirstName(), customer.getLastName());
    }


    public Customer select(int id) {
        return jdbcTemplate.queryForObject("SELECT id, first_name, last_name FROM customers WHERE id = ?",
                new Object[]{id}, new CustomerRowMapper());
    }

    public void delete(int id) {
        String command = "DELETE FROM customers WHERE id =" + id;
        jdbcTemplate.execute(command);
    }

    public void update(Customer customer, int id) {
        jdbcTemplate.update("UPDATE customers SET first_name = ?, last_name = ? WHERE id =?",
                customer.getFirstName(), customer.getLastName(), id);
    }
public List<Customer> selectAll(){
        return jdbcTemplate.query("SELECT * FROM customers", new CustomerRowMapper());
}
}
