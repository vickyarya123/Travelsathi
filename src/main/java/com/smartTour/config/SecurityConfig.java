package com.smartTour.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	@Lazy
	private AuthFailureHandlerImpl authenticationFailureHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

		provider.setPasswordEncoder(passwordEncoder());

		return provider;
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.cors(cors -> cors.disable())

				.authorizeHttpRequests(req -> req

						.requestMatchers("/user/**").authenticated()

						.requestMatchers("/admin/**").hasRole("ADMIN")

						.requestMatchers("/**")
						.permitAll()

						.anyRequest().authenticated())

				// NORMAL LOGIN
				.formLogin(form -> form

						.loginPage("/signin").loginProcessingUrl("/login")

						.failureHandler(authenticationFailureHandler)

						.successHandler(authenticationSuccessHandler))

				// GOOGLE LOGIN
				.oauth2Login(oauth -> oauth

						.loginPage("/signin")

						.successHandler(authenticationSuccessHandler)

						.failureHandler(authenticationFailureHandler))

				.logout(logout -> logout.permitAll());

		return http.build();
	}

}
