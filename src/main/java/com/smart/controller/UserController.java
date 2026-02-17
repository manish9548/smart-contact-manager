package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // common data
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        String userName = principal.getName();

        User user = userRepository.findByEmail(userName);

        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model) {

        model.addAttribute("title", "User Dashboard");

        return "normal/user_dashboard";
    }

    // open add contact form
    @GetMapping("/add-contact")
    public String opeAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact");

        model.addAttribute("contact", new Contact());

        return "normal/add_contact_form";
    }

    // ✅ FIXED METHOD
    @PostMapping("/process-contact")
    @Transactional
    public String processContact
    (@ModelAttribute Contact contact,
    		@RequestParam("profileImage") MultipartFile file,
    		Principal principal) {
    	
    	

       try {
    	   String name = principal.getName();

           User user = this.userRepository.findByEmail(name);
           //processing anduploading file...
           if(file.isEmpty()) {
        	   System.out.println("File is empty");
        	   
           }else {
        	   contact.setImage(file.getOriginalFilename());
        	
        	File saveFile =   new ClassPathResource("static/img").getFile();
        	Path    path  = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
        	Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
        	System.out.println("img is uploaded");
           }

           contact.setUser(user);
           
           

           user.getContacts().add(contact);

           this.userRepository.save(user);

           System.out.println("DATA " + contact);

           System.out.println("Added to database");

           return "normal/add_contact_form";
       }catch(Exception e){
    	   //TODO :handle exception
    	   System.out.println("ERROR"+e.getMessage());
    	   e.printStackTrace();      
    	   }
       return "normal/add_contact_form"; 
    }

}
