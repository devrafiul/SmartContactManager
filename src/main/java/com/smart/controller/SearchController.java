package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController    //when we use ResponseEntity then we use restcontroller  because it returns ResponseEntity but it does not return view

public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//search handler
	//Principal -> current user return korbe
	//ResponseEntity create api(application 
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search( @PathVariable("query") String query,Principal principal){
		
		System.out.println(query);
		
		User user= this.userRepository.getUserByUserName(principal.getName());
		
		List<Contact> contacts= this.contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}

}
