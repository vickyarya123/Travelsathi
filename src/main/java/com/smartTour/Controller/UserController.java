package com.smartTour.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartTour.model.Category;
import com.smartTour.model.UserDtls;
import com.smartTour.service.CategoryService;
import com.smartTour.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;


	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title",
		        "User Dashboard | TravelSathi");
		return "user/home";
	}

//    @ModelAttribute
//    public void getUserDetails(Principal p, Model m) {
//        if (p != null) {
//            String email = p.getName();
//            UserDtls userDtls = userService.getUserByEmail(email);
//            m.addAttribute("user", userDtls);
//        }
//        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
//        m.addAttribute("categorys", allActiveCategory);
//    }
	@ModelAttribute
	public void getUserDetails(Authentication authentication, Model m) {

		if (authentication != null) {

			String email = null;

			Object principal = authentication.getPrincipal();

			// NORMAL LOGIN
			if (principal instanceof UserDetails) {

				email = ((UserDetails) principal).getUsername();
			}

			// GOOGLE LOGIN
			else if (principal instanceof OAuth2User) {

				OAuth2User oauthUser = (OAuth2User) principal;

				email = oauthUser.getAttribute("email");
			}

			// FETCH USER FROM DB
			if (email != null) {

				UserDtls userDtls = userService.getUserByEmail(email);

				m.addAttribute("user", userDtls);
			}
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();

		m.addAttribute("categorys", allActiveCategory);
	}

	
	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}


	@GetMapping("/profile")
	public String profile(Model m) {
		  m.addAttribute("title",
			        "My Profile | TravelSathi");
		return "/user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/user/profile";
	}

	@GetMapping("/changepassword")
	public String showChangePasswordPage(Model m) {
		m.addAttribute("title",
		        "change password- TravelSathi");
		return "/user/changepassword";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated sucessfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}

		return "redirect:/user/changepassword";
	}

}
