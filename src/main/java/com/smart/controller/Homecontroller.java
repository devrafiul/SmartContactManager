package com.smart.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.messageHelper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class Homecontroller {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	
	//RequestMapping("/home")
	@GetMapping("/")
	public String home(Model model) {
		System.out.println("this is home page");
		
		model.addAttribute("title","Home - Smart contact Manager");
		
		return "home";
	}
	
	
	@GetMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title","About-Smart Contact Manager");
		
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","Register-Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	

	@GetMapping("/do_register")
    public String populateList(Model model) {
        List<String> options = new ArrayList<>();
        options.add("CSE");
        options.add("English");
        options.add("EEE");
        options.add("BBA");
        options.add("CIVIL");
        model.addAttribute("options", options);
       
        model.addAttribute("user", new User()); 
        return "signup";
	}
	
	@GetMapping("/do-register")
    public String PopulateList(Model model) {
        List<String> option = new ArrayList<>();
        option.add("Bogura");
        option.add("Rajshahi");
        option.add("Zoypurhat");
        option.add("Gaibandha");
        option.add("Nator");
        model.addAttribute("option", option);
       
        model.addAttribute("user", new User()); 
        return "signup";
	}
	
	@GetMapping("/do-registerr")
    public String PpopulateList(Model model) {
        List<String> optionss = new ArrayList<>();
        optionss.add("Bogura");
        optionss.add("Rajshahi");
        optionss.add("Zoypurhat");
        optionss.add("Gaibandha");
        optionss.add("Nator");
        model.addAttribute("optionss", optionss);
       
        model.addAttribute("user", new User()); 
        return "signup";
	}
	
	
	
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
				
		model.addAttribute("title","Login Page");
		return "login";
	}
	
	
	//handler for registering user
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,
			BindingResult result,
			@RequestParam(value="agreement",
			defaultValue="false") boolean agreement,
			Model model,
			HttpSession session) {
		
		try {
			
			if(!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agrred the terms and conditions");
			}
			
			
			if(result.hasErrors()) {
				
				System.out.println("Error" + result.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement"+agreement);
			System.out.println("User"+user);
			
			User save = this.userRepository.save(user);
			
			
			model.addAttribute("user",new User());
			
			session.setAttribute("message", new messageHelper("Successfully Registered !! ", "alert-success"));
			return "signup";
			
			
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new messageHelper("Something Went Wrong!!"+e.getMessage(),"alert-danger"));
			return "signup";
			
			
			
		}
		
		
		
	}
	
	
	
	
	
	
	

	/*@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/testty")
	@ResponseBody
	public String test() {
		
		User user=new User();
		user.setName("Rafiul Islam");
		user.setEmail("rafiulislam432060@gmail.com");
		
		Contact contact=new Contact();
		user.getContacts().add(contact);
		
		userRepository.save(user);
		
		return "Working";
	}*/
	
	

}
