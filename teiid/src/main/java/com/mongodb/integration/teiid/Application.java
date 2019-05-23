package com.mongodb.integration.teiid;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;


@SpringBootApplication
public class Application implements CommandLineRunner {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args).close();;
	}


	@Override
	public void run(String... args) throws Exception {
		
		System.out.println("RDBMS Query Engine Info:");
		System.out.println("    " + jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName());
		System.out.println("    " + jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductVersion());
		System.out.println("    " + jdbcTemplate.getDataSource().getConnection().getMetaData().getDriverName());
		
		System.out.println("RDBMS Tables:");
		DatabaseMetaData md = jdbcTemplate.getDataSource().getConnection().getMetaData();
		ResultSet rs = md.getTables(null, null, "%", new String[] {"TABLE"});
		while (rs.next()) {
		  System.out.println("    " + rs.getString(3));
		}
		
		rs.close();
		
//		List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT *  FROM cities limit 3");
		
		jdbcTemplate.query("SELECT *  FROM cities limit 3", new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				System.out.println(rs);
			}});

//		System.out.println(list);
		
	}


	

}
