package hello;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by tr0k on 2014-07-11.
 */
public class Application {
    public static void main(String[] args) {
        // DS for test not intended for production
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUsername("sa");
        dataSource.setUrl("jdbc:h2:mem");
        dataSource.setPassword("");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        System.out.println("Creating tables");
        jdbcTemplate.execute("drop table customers if exists");
        StringBuilder query = new StringBuilder().append("create table customers")
                .append("(id serial, first_name varchar(255), last_name varchar(255))");
        jdbcTemplate.execute(query.toString());

        String[] names = "Jan Kowalski;John Snowden;Eric Clapton;John English".split(";");

        for (String fullname : names) {
            String[] name = fullname.split(" ");
            System.out.printf("Inserting customer record for %s %s\n", name[0], name[1]);
            jdbcTemplate.update(
                    "INSERT INTO customers(first_name,last_name) values(?,?)",
                    name[0], name[1]);
        }

        System.out.println("Querying for customer records where first_name = 'John':");
        List<Customer> result = jdbcTemplate.query(
                "select * from customers where first_name = ?", new Object[]{"John"},
                new RowMapper<Customer>() {
                    @Override
                    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Customer(rs.getLong("id"), rs.getString("first_name"),
                                rs.getString("last_name"));
                    }
                }
        );

        for (Customer customer : result){
            System.out.println(customer);
        }
    }
}