package com.jinu.mdatasouce.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({ "classpath:application.properties" })
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "employeeEntityManagerFactory", 
transactionManagerRef = "employeeTransactionManager", 
basePackages = {"com.jinu.mdatasouce.employee.repository" })
public class EmployeeDBConfig {

	@Autowired
	Environment env;

	@Primary
	@Bean(name = "employeeDataSource")
	@ConfigurationProperties(prefix = "spring.employee.datasource")
	public DataSource employeeDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("spring.employee.datasource.driver-class-name"));
		dataSource.setUrl(env.getProperty("spring.employee.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.employee.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.employee.datasource.password"));
		return dataSource;

	}

	@Primary
	@Bean(name = "employeeEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
			EntityManagerFactoryBuilder secondaryEntityManagerFactoryBuilder,
			@Qualifier("employeeDataSource") DataSource secondaryDataSource) {

		Map<String, String> secondaryJpaProperties = new HashMap<>();
		secondaryJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		secondaryJpaProperties.put("hibernate.hbm2ddl.auto", "none");

		return secondaryEntityManagerFactoryBuilder.dataSource(secondaryDataSource)
				.packages("com.jinu.mdatasouce.employee.model").persistenceUnit("employeeDataSource")
				.properties(secondaryJpaProperties).build();
	}

	@Bean(name = "employeeTransactionManager")
	public PlatformTransactionManager secondaryTransactionManager(
			@Qualifier("employeeEntityManagerFactory") EntityManagerFactory secondaryEntityManagerFactory) {
		return new JpaTransactionManager(secondaryEntityManagerFactory);
	}

}
