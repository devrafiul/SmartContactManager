package com.smart.controller;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.razorpay.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.messageHelper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder; 
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	@Autowired
	private MyOrderRepository myOrderRepository ;
	
	//method for adding common data to response
	
	@ModelAttribute // All handler works addCommonData
	public void addCommonData(Model model, Principal principal) {
		
        String name = principal.getName();
		
		//get the user using username(Email)
		System.out.println("Username " +name);
		
		User userByUserName = userRepository.getUserByUserName(name);
		System.out.println("User"+userByUserName);
		
		model.addAttribute("user",userByUserName);
		
	}
	
	// dashboard home handler
	
	@RequestMapping("/index")
	public String dashboard(Model model , Principal principal){
		
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		return "normal/add_contact_form";
	}
	
	
	//processing and contact form
	
	@PostMapping("/process-content")
	public String processContact(@ModelAttribute Contact contact , 
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,HttpSession session) {
		
		try {
		
		String name=principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		
		/*if(3>2) {
			throw new Exception();
		}*/
		
		//processing and uploading file...
		
		if(file.isEmpty()) {
			
			
			System.out.println(" File is empty");
			contact.setImage("normal/contact.png");
			contact.getImage();
			session.setAttribute("message", new messageHelper("Your contact is addes  !! add more ..." ,  "success" ));
		
		}else {
			//file the file to folder and update the name to contact
			
			contact.setImage(file.getOriginalFilename());
			
			File saveFile = new ClassPathResource("static/image").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("Image is uploaded");
		
		contact.setUser(user);
		
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		
		
		
		
		
		System.out.println("Data "+contact);
		
		System.out.println("Added to database");
		
		//message success
		session.setAttribute("message", new messageHelper("Your contact is addes  !! add more ..." ,  "success" ));
		
		
		}
		
		}catch(Exception e) {
			
			System.out.println("Error" +e.getMessage());
			
			e.printStackTrace();
			
			//error message
			session.setAttribute("Some went wrong !! Try again..","danger");
		}
		
		return "normal/add_contact_form";
	}
	
	
	/*show contact handler*/
	
	//per page=5   //n=5
	//current page= 0[page]
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts( @PathVariable ("page") Integer page, Model m, Principal principal) {
		
		m.addAttribute("title","Show User Contacts");
		
		//contact list
		/*String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		List<Contact> contacts = user.getContacts();*/
		
		
		String UserName = principal.getName();
		User user= this.userRepository.getUserByUserName(UserName);
		
		Pageable pageable = PageRequest.of(page,3);
		
		//A page is a sublist of a list of objects. 
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts",contacts);
		
		m.addAttribute("currentPage",page);
		
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	//showing particular contact details
	
	@RequestMapping("/{cid}/contact/")
	public String showContactDetail(@PathVariable("cid") Integer cid,Model model,Principal principal) {
		
		System.out.println("Cid"+cid);
		
		 Optional<Contact> findById = this.contactRepository.findById(cid);
		
		Contact contact = findById.get();
		
		
		//cheack
		String UserName = principal.getName();
		User user = this.userRepository.getUserByUserName(UserName);
		
		
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
		
		return "normal/contact_detail";
	}
	
	
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,Model model,HttpSession session,Principal principal) {
		
		System.out.println("CID "+cid);
		
		Optional<Contact> findById = this.contactRepository.findById(cid);
		
		Contact contact = findById.get();
		
		User user=this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		
		System.out.println("DELETED");
		session.setAttribute("message", new messageHelper("Contact deleted successfully..","success"));
		
			
		return "redirect:/user/show-contacts/0";
	}
	
	
	//open update form handler
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model model) {
		
		model.addAttribute("title","Update Contact");
		
		Optional<Contact> findById = this.contactRepository.findById(cid);
		Contact contact = findById.get();
		
		model.addAttribute("contact",contact);
		
		return "normal/update_form";
	}
	
	//update contact handler
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Model model,HttpSession session,Principal principal) {
		
		try {
			
			
			//old contact details
			
		Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
			
			
			//image
			if(!file.isEmpty()) {
			
				
				//file work
				//rewrite
				//delete old photo
				
				File deleteFile = new ClassPathResource("static/image").getFile();
				
				File file1=new File(deleteFile , oldContactDetail.getImage());
				file1.delete();
				
				//update photo
				File saveFile = new ClassPathResource("static/image").getFile();
				
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
			}else {
				
				contact.setImage(oldContactDetail.getImage());
			}
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message",new messageHelper("Your Contact is update..","success"));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("Contact Name"+contact.getName());
		System.out.println("Contact Id" +contact.getCid());
		
		
		
		return "redirect:/user/"+ contact.getCid() +"/contact/";
	}
	
	
	//your profile handler
	
	@GetMapping("/profile")
	public  String yourProfile(Model model) {
		
		model.addAttribute("title","Profile Page");
		
		return "normal/profile";
	}
	
	
	//open setting handler
	
	@GetMapping("/settings")
	public String openSettings() {
		
		
		return "normal/settings";
		
	}
	
	//change password handler
	//URL er vetor diye data asle @PathVariable 
	//form er vetor diye data asle @RequestParam
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,
			Principal principal,HttpSession session) {
		
		System.out.println("OLD PASSWORD "+oldPassword);
		System.out.println("NEW PASSWORD "+newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			//change the password
			
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			
			this.userRepository.save(currentUser);
			
			session.setAttribute("message", new messageHelper("Your password successfully change...", "success"));
		}else {
			
			//error
			
			session.setAttribute("message",new messageHelper("Please Enter correct old password !!" , "danger"));
			
			return"redirect:/user/settings";
		}
		
		
		
		return "redirect:/user/index";
	}
	
	
	
	
	//creating order for payment
	
	@PostMapping("/create_order")
	@ResponseBody
	//return kono html view nh kore amra korbo string("done") ay jonno ResponseBody use
	public String createOrder(@RequestBody Map<String,Object>data, Principal principal) throws Exception {
		
		System.out.println("Hey order function ex.");
		System.out.println(data);
		
		int amt =Integer.parseInt(data.get("amount").toString());
		
		
		//RazorpayClient razorpay = new RazorpayClient("[rzp_test_4YcgbOjFXiZ7wn]", "[YLgP4EbX2dzS3RrDU0jIigxc]");
		
		var client = new RazorpayClient("rzp_test_4YcgbOjFXiZ7wn", "YLgP4EbX2dzS3RrDU0jIigxc");	
		
		 JSONObject ob=new JSONObject();
		 ob.put("amount", amt*100);
		 ob.put("currency", "INR");
		 ob.put("receipt", "txn_235425");
		 
		 //creating new order
		 
		 Order order = client.orders.create(ob);
		 System.out.println(order);
		 
		 //save the order in database
		 
		 MyOrder myOrder = new MyOrder();
		 
		 myOrder.setAmount(order.get("amount")+"");
		 myOrder.setOrderId(order.get("id"));
		 myOrder.setPaymentId(null);
		 myOrder.setStatus("created");
		 myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
		 myOrder.setReceipt(order.get("receipt"));
		 
		 this.myOrderRepository.save(myOrder);
		 
		 
		 
			
		
		//if you want you can save this to your data.
		return order.toString();
	}
	
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object>data){
		
		MyOrder myorder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		myorder.setPaymentId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myorder);
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
	
	
	
	

}
	



