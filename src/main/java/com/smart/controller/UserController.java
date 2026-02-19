package com.smart.controller;

import java.util.List;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import org.springframework.data.domain.Pageable; // ✅ CORRECT


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.healper.Message;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

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
    public String processContact(
            @Valid @ModelAttribute("contact") Contact contact,
            org.springframework.validation.BindingResult result,
            @RequestParam("profileImage") MultipartFile file,
            Principal principal,
            HttpSession session,
            Model model) {

        try {

            // 🔴 VALIDATION CHECK
            if (result.hasErrors()) {
                System.out.println("Validation Errors");
                model.addAttribute("contact", contact);
                return "normal/add_contact_form";
            }

            String name = principal.getName();
            User user = this.userRepository.findByEmail(name);

            // File upload
            if (file.isEmpty()) {
                System.out.println("File is empty");
            } else {
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()
                        + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path,
                        StandardCopyOption.REPLACE_EXISTING);

                System.out.println("img is uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            this.userRepository.save(user);

            session.setAttribute("message",
                    new Message("Your contact is added!! Add more..", "success"));

            return "redirect:/user/add-contact";

        } catch (Exception e) {

            e.printStackTrace();

            session.setAttribute("message",
                    new Message("Something went wrong !! Try again...", "danger"));
        }

        return "normal/add_contact_form";
        
    }
    
    //show contact handler 
    
    //per page =5[n]
 // ✅ Default handler (no page number required)
    @GetMapping("/show-contacts")
    public String showContactsFirstPage(Model model, Principal principal) {

        return showContacts(0, model, principal);

    }

    
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page  ,Model m,Principal principal) {
    	m.addAttribute("title", "Show user Contact");
    	String userName = principal.getName();
    	User user =	this.userRepository.findByEmail(userName);
    	Pageable pageable = PageRequest.of(page ,5);
    	
    	
    	Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
    	m.addAttribute("contacts",contacts );
    	m.addAttribute("currentPage", page);
    	m.addAttribute("totalPages", contacts.getTotalPages());
    	
    	return "normal/show_contacts";
    }


}
