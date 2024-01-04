package com.smart.config;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class MyConfig {
	
	
	
	//it is use admin, normal and public
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	
	
	
	@Bean
	public UserDetailsService userDetailsService() {
		
		/*UserDetails normalUser=User.withUsername("rafi")
				.password(passwordEncoder().encode("Rafiul"))
				.roles("normal")
				.build();
		UserDetails adminUser=User.withUsername("user")
				.password(passwordEncoder().encode("password"))
				.roles("ADMIN")
				.build();
		
		return new InMemoryUserDetailsManager(normalUser,adminUser);*/
		
		return new UserDetailsServiceImpl();
		
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		
	httpSecurity.csrf().disable().authorizeHttpRequests()
	.requestMatchers("/admin/**")
	.hasRole("ADMIN")
	.requestMatchers("/user/**")
	.hasRole("USER")
	.requestMatchers("/**")
	.permitAll()
	.anyRequest()
	.authenticated()
	.and().formLogin()
	.loginPage("/signin") //the custom login page
	.loginProcessingUrl("/dologin") //the url to submit the username and password
	.defaultSuccessUrl("/user/index"); //the landing page after a successful login
	
	
	return httpSecurity.build();
	
	
	}
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
