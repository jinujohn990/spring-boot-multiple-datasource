package com.jinu.mdatasouce.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
@EnableJpaRepositories(entityManagerFactoryRef = "userEntityManagerFactory", transactionManagerRef = "userTransactionManager", basePackages = {
		"com.jinu.mdatasouce.user" })
public class UserDBConfig {

	@Autowired
	Environment env;


	@Bean(name = "userDataSource")
	@ConfigurationProperties(prefix = "spring.user.datasource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("spring.user.datasource.driver-class-name"));
		dataSource.setUrl(env.getProperty("spring.user.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.user.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.user.datasource.password"));
		return dataSource;
	}

	@Bean(name = "userEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
			EntityManagerFactoryBuilder secondaryEntityManagerFactoryBuilder,
			@Qualifier("userDataSource") DataSource secondaryDataSource) {

		Map<String, String> secondaryJpaProperties = new HashMap<>();
		secondaryJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		secondaryJpaProperties.put("hibernate.hbm2ddl.auto", "none");

		return secondaryEntityManagerFactoryBuilder.dataSource(secondaryDataSource)
				.packages("com.jinu.mdatasouce.user.model").persistenceUnit("secondaryDataSource")
				.properties(secondaryJpaProperties).build();
	}

	@Bean(name = "userTransactionManager")
	public PlatformTransactionManager secondaryTransactionManager(
			@Qualifier("userEntityManagerFactory") EntityManagerFactory secondaryEntityManagerFactory) {

		return new JpaTransactionManager(secondaryEntityManagerFactory);
	}

}
