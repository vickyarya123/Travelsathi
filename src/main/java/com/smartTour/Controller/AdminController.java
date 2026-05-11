package com.smartTour.Controller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.smartTour.model.Category;
import com.smartTour.model.Destination;
import com.smartTour.model.UserDtls;
import com.smartTour.service.CategoryService;
import com.smartTour.service.DestinationService;
import com.smartTour.service.UserService;
import com.smartTour.util.CommonUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private DestinationService destinationService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@ModelAttribute
//	public void getUserDetails(Principal p, Model m) {
//		if (p != null) {
//			String email = p.getName();
//			UserDtls userDtls = userService.getUserByEmail(email);
//			m.addAttribute("user", userDtls);
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

	@GetMapping("/")
	public String index(Model m) {
		m.addAttribute("title",
		        "India Tour Packages | Book Holiday Packages in India with TravelSathi");

		return "admin/index";
	}

	@GetMapping("/category")
	public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {
//		 m.addAttribute("categorys", categoryService.getAllCategory());
		Page<Category> page = categoryService.getAllCategorPagination(pageNo, pageSize);
		List<Category> categorys = page.getContent();
		m.addAttribute("categorys", categorys);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/category";
	}



	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Category already exists");
		} else {

			Category saveCategory = categoryService.saveCategory(category);

			if (!ObjectUtils.isEmpty(saveCategory)) {

//				if (!file.isEmpty()) {
//					String uploadDir = "C:/springbootproject/SmartTour/images/category/";
//
//					File folder = new File(uploadDir);
//					if (!folder.exists())
//						folder.mkdirs();
//
//					Path path = Paths.get(uploadDir + file.getOriginalFilename());
//					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//				}

				if (!file.isEmpty()) {

					String uploadDir = "C:/springbootproject/SmartTour/images/category/";

					File folder = new File(uploadDir);
					if (!folder.exists())
						folder.mkdirs();

					String originalName = file.getOriginalFilename();
					String fileName = System.currentTimeMillis() + "_" + originalName;

					Path path = Paths.get(uploadDir + fileName);

					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

					category.setImageName(imageName);
				}

				session.setAttribute("succMsg", "Category saved successfully");
			} else {
				session.setAttribute("errorMsg", "Internal server error");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("succMsg", "category delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		m.addAttribute("title",
		        "Category-India Tour with TravelSathi");

		return "admin/edit_category";
	}


	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());

		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		oldCategory.setName(category.getName());
		oldCategory.setIsActive(category.getIsActive());
		oldCategory.setImageName(imageName);

		Category updateCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

//			if (!file.isEmpty()) {
//				String uploadDir = "C:/springbootproject/SmartTour/images/category/";
//
//				File folder = new File(uploadDir);
//				if (!folder.exists())
//					folder.mkdirs();
//
//				Path path = Paths.get(uploadDir + file.getOriginalFilename());
//				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			}

			if (!file.isEmpty()) {

				String uploadDir = "C:/springbootproject/SmartTour/images/category/";

				File folder = new File(uploadDir);
				if (!folder.exists())
					folder.mkdirs();

				String originalName = file.getOriginalFilename();
				String fileName = System.currentTimeMillis() + "_" + originalName;

				Path path = Paths.get(uploadDir + fileName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				category.setImageName(imageName);
			}

			session.setAttribute("succMsg", "Category updated successfully");

		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@GetMapping("/loadAddDestination")
	public String loadAddPlace(Model m) {

		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		m.addAttribute("title",
		        "AddDestination - on TravelSathi");


		return "admin/add_places";
	}



	@PostMapping("/saveDestination")
	public String saveDestination(@ModelAttribute Destination destination, @RequestParam("file") MultipartFile image,
			HttpSession session) {

//	    Destination saved = destinationService.saveDestination(destination, image);
		Destination saveDestination = destinationService.saveDestination(destination);

		if (saveDestination != null) {
			session.setAttribute("succMsg", "Destination Saved Success");
		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}

		return "redirect:/admin/loadAddDestination";
	}



	@GetMapping("/destination")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		Page<Destination> page;

		if (pageNo < 0) {
			pageNo = 0;
		}

		if (ch != null && !ch.trim().isEmpty()) {
			page = destinationService.searchDestinationPagination(pageNo, pageSize, ch);
		} else {
			page = destinationService.getAllDestinationPagination(pageNo, pageSize);
		}

		// Only pagination data
		m.addAttribute("destination", page.getContent());

		// Pagination info
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		// Important (for search persistence)
		m.addAttribute("ch", ch);

		return "admin/destination";
	}

	@GetMapping("/deleteDestination/{id}")
	public String deleteDestination(@PathVariable int id, HttpSession session) {
		Boolean deleteDestination = destinationService.deleteDestination(id);
		if (deleteDestination) {
			session.setAttribute("succMsg", "destination delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}
		return "redirect:/admin/destination";
	}

	@GetMapping("/editDestination/{id}")
	public String loadEditDestination(@PathVariable int id, Model m) {
		m.addAttribute("destination", destinationService.getDestinationById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		m.addAttribute("title",
		        "EditDestination TravelSathi");

		return "admin/edit_destination";
	}

	@PostMapping("/updateDestination")
	public String updateDestination(@ModelAttribute Destination destination,
			@RequestParam(value = "file", required = false) MultipartFile image, HttpSession session, Model m) {
		Destination updateDestination = destinationService.updateDestination(destination, image);

		if (!ObjectUtils.isEmpty(updateDestination)) {

			session.setAttribute("succMsg", "Destination update success");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
//			}
		}
		return "redirect:/admin/editDestination/" + destination.getId();
//		return "redirect:/admin/destination";

	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<UserDtls> users = null;
		if (type == 1) {
			users = userService.getUsers("ROLE_USER");
		} else {
			users = userService.getUsers("ROLE_ADMIN");
		}
		m.addAttribute("userType", type);
		m.addAttribute("users", users);
		return "/admin/users";
	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,
			@RequestParam Integer type, HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/users?type=" + type;
	}

	@GetMapping("/profile")
	public String profile(Model m) {
		m.addAttribute("title",
		        "Yourprofile India Tour Packages with TravelSathi");

		return "/admin/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/admin/profile";
	}

	@GetMapping("/changepassword")
	public String showChangePasswordPage(Model m) {
		m.addAttribute("title",
		        "change password- TravelSathi");

		return "/admin/changepassword";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

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

		return "redirect:/admin/changepassword";
	}

	@GetMapping("/add-admin")
	public String loadAdminAdd(Model m) {
		m.addAttribute("title",
		        "AddAdmin- TravelSathi");
		return "/admin/add_admin";
	}

	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		// Check email exists
		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			session.setAttribute("errorMsg", "Email already exists");
			return "redirect:/register";
		}

		//  Upload folder (same as your config)
		String uploadDir = "C:/springbootproject/SmartTour/images/profile/";

		// create folder if not exists
		File folder = new File(uploadDir);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		//  Generate unique image name
		String imageName = "default.jpg";

		if (file != null && !file.isEmpty()) {

			String originalName = file.getOriginalFilename();
			imageName = System.currentTimeMillis() + "_" + originalName;

			Path path = Paths.get(uploadDir + imageName);

			// save image
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}

		//  set image to user
		user.setProfileImage(imageName);

		//  save user
		UserDtls saveAdmin = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveAdmin)) {
			session.setAttribute("succMsg", "Registered successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
		}

		return "redirect:/admin/add-admin";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

}
