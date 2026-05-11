package com.smartTour.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.smartTour.model.UserDtls;
import com.smartTour.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler {

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//            Authentication authentication) throws IOException, ServletException {
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);
//        if (roles.contains("ROLE_ADMIN")) {
//            response.sendRedirect("/admin/");
//        } else {
//            response.sendRedirect("/");
//        }
//    }

	@Autowired
	private UserRepository userRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		Object principal = authentication.getPrincipal();

		// GOOGLE LOGIN
		if (principal instanceof OAuth2User) {

			OAuth2User oauthUser = (OAuth2User) principal;

			String email = oauthUser.getAttribute("email");
			String name = oauthUser.getAttribute("name");

			// FIND USER
			UserDtls user = userRepository.findByEmail(email);

			// SAVE NEW USER
			if (user == null) {

				user = new UserDtls();

				user.setName(name);
				user.setEmail(email);
				user.setIsEnable(true);
				user.setRole("ROLE_USER");

				user = userRepository.save(user);
			}

			// IMPORTANT
			HttpSession session = request.getSession();

			session.setAttribute("user", user);

			response.sendRedirect("/");
			return;
		}

		// NORMAL LOGIN
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		Set<String> roles = AuthorityUtils.authorityListToSet(authorities);

		if (roles.contains("ROLE_ADMIN")) {

			response.sendRedirect("/admin/");

		} else {

			response.sendRedirect("/");
		}
	}
}
