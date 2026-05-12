package com.smartTour.Controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.smartTour.model.Category;
import com.smartTour.model.Destination;
import com.smartTour.model.UserDtls;
//import com.smartTour.repository.UserRepository;
import com.smartTour.service.CategoryService;
import com.smartTour.service.DestinationService;
import com.smartTour.service.GeminiService;
import com.smartTour.service.UserService;
import com.smartTour.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private DestinationService destinationService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private GeminiService geminiService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
//	@Autowired
//	private UserRepository userRepository;

//	@ModelAttribute
//	public void getUserDetails(Principal p, Model m) {
//		if (p != null) {
//			String email = p.getName();
//			UserDtls userDtls = userService.getUserByEmail(email);
//			m.addAttribute("user", userDtls);
////			Integer countCart = cartService.getCountCart(userDtls.getId());
////			m.addAttribute("countCart", countCart);
//		}
//		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
//		m.addAttribute("categorys", allActiveCategory);
//	}
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

	@GetMapping("/ai-chat")
	@ResponseBody
	public String chat(@RequestParam String msg) {
		return geminiService.getResponse(msg);
	}

	@GetMapping("/about")
	public String aboutPage(Model m) {
		m.addAttribute("title", "About TravelSathi – Your Smart Travel Companion");
		return "about"; // this will open about.html
	}

	@GetMapping("/otp")
	public String otpPage(Model m) {
		m.addAttribute("title", "SentOtp-TravelSathi");
		return "otp"; // this will open otp.html
	}

	@GetMapping("/contact")
	public String contact(Model m) {
		m.addAttribute("title", "Get in Touch with TravelSathi");
		return "contact"; // this will open contact.html
	}

	@GetMapping("/ask_ai")
	public String AskAi(Model m) {
		m.addAttribute("title", "Ask Travel AI | Smart Trip Planning with TravelSathi");
		return "ask_ai"; // this will open contact.html
	}

	@GetMapping("/")
	public String index(Model m) {
//		System.out.println(System.getenv("MAIL_USERNAME"));
//		System.out.println(System.getenv("MAIL_PASSWORD"));
//		System.out.println(System.getenv("GOOGLE_CLIENT_ID"));
//		System.out.println(System.getenv("GOOGLE_CLIENT_SECRET"));
		List<Destination> destinations = destinationService.getAllDestination();
		m.addAttribute("title", "India Tour Packages | Book Holiday Packages in India with TravelSathi");

		// Shuffle list (random order)
		Collections.shuffle(destinations);

		Map<String, Destination> stateMap = new LinkedHashMap<>();

		for (Destination d : destinations) {

			if (!stateMap.containsKey(d.getState())) {
				stateMap.put(d.getState(), d);
			}
		}

		// only 10 states
		List<Map.Entry<String, Destination>> stateList = stateMap.entrySet().stream().limit(17).toList();

		m.addAttribute("stateList", stateList);
		m.addAttribute("destinations", destinations);

		return "index";
	}

	@GetMapping("/signin")
	public String login(Model m) {
		m.addAttribute("title", "Login -India Tour with TravelSathi");
		return "login";
	}

	@GetMapping("/register")
	public String register(Model m) {
		m.addAttribute("title", "Register -India Tour with TravelSathi");
		return "register";
	}

	@GetMapping("/destination")
	public String destination(Model m, @RequestParam(value = "category", required = false) List<String> category,
			@RequestParam(value = "state", required = false) List<String> state,
			@RequestParam(value = "duration", required = false) List<Integer> duration,
			@RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		// 🔹 Load Filters
		m.addAttribute("categories", categoryService.getAllActiveCategory());
		m.addAttribute("states", destinationService.getAllStates());
		m.addAttribute("durations", destinationService.getAllDurations());

		if (category != null && category.isEmpty())
			category = null;
		if (state != null && state.isEmpty())
			state = null;
		if (duration != null && duration.isEmpty())
			duration = null;

		// 🔹 Get Data
		Page<Destination> page = destinationService.filterDestination(pageNo, pageSize, category, state, duration, ch);

		m.addAttribute("destinations", page.getContent());
		m.addAttribute("totalElements", page.getTotalElements());
		// 🔹 Pagination
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		// 🔹 Keep selected values
		m.addAttribute("selectedCategory", category);
		m.addAttribute("selectedState", state);
		m.addAttribute("selectedDuration", duration);

		m.addAttribute("ch", ch);

		String title = "Explore Tour Packages & Destinations | TravelSathi";
		if (category != null && !category.isEmpty()) {
			title = category.get(0) + " Tour Packages & Destinations | TravelSathi";
		}
		if (state != null && !state.isEmpty()) {
			title = state.get(0) + " Tourism & Holiday Packages | TravelSathi";
		}
		if (!ch.isEmpty()) {
			title = ch + " Travel Guide & Tour Packages | TravelSathi";
		}
		m.addAttribute("title", title);

		return "trending";
	}

	@GetMapping("/view_detail/{id}")
	public String view_detail(@PathVariable int id, Model m) {
		Destination destinationById = destinationService.getDestinationById(id);
		m.addAttribute("destination", destinationById);

		String title = destinationById.getCity() + " Tour Packages, Hotels & Travel Guide | TravelSathi";

		m.addAttribute("title", title);

		return "view_detail";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		// Check email exists
		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			session.setAttribute("errorMsg", "Email already exists");
			return "redirect:/register";
		}

		// Upload folder (same as your config)
		String uploadDir = "C:/springbootproject/SmartTour/images/profile/";

		// create folder if not exists
		File folder = new File(uploadDir);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		// Generate unique image name
		String imageName = "C:/springbootproject/SmartTour/images/default.jpg";

		if (file != null && !file.isEmpty()) {

			String originalName = file.getOriginalFilename();
			imageName = System.currentTimeMillis() + "_" + originalName;

			Path path = Paths.get(uploadDir + imageName);

			// save image
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}

		// set image to user
		user.setProfileImage(imageName);

		// save user
		UserDtls saveUser = userService.saveUser(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			session.setAttribute("succMsg", "Registered successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
		}

		return "redirect:/register";
	}

	@GetMapping("/forgot-password")
	public String showForgotPassword(Model m) {
		m.addAttribute("title", "ForgotPassword -India Tour with TravelSathi");
		return "forgot_password.html";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		UserDtls userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Invalid email");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMail(url, email);

			if (sendMail) {
				session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
			} else {
				session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
			}
		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		UserDtls userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			m.addAttribute("errorMsg", "Your link is invalid or expired !!");
			return "message";
		}
		m.addAttribute("token", token);
		m.addAttribute("title", "resetPassword -India Tour with TravelSathi");
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model m) {

		UserDtls userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			m.addAttribute("errorMsg", "Your link is invalid or expired !!");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			// session.setAttribute("succMsg", "Password change successfully");
			m.addAttribute("errorMsg", "Password change successfully");

			return "message";
		}

	}

	@PostMapping("/contact")
	public String sendContactMail(@RequestParam String name, @RequestParam String email, @RequestParam String subject,
			@RequestParam("messageText") String messageText, HttpSession session, Model m) {

		try {
			commonUtil.sendContactMail(name, email, subject, messageText);
			session.setAttribute("succMsg", "Message sent successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("errorMsg", "Something went wrong!");
		}

		return "redirect:/contact";
	}

	@GetMapping("/search")
	public String searchProduct(@RequestParam String ch, Model m) {

		List<Destination> searchDestination = destinationService.searchDestination(ch);

		m.addAttribute("destinations", searchDestination);
		m.addAttribute("categories", categoryService.getAllActiveCategory());
		m.addAttribute("states", destinationService.getAllStates());
		m.addAttribute("durations", destinationService.getAllDurations());
		m.addAttribute("ch", ch);

		return "trending";
	}
}
