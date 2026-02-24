package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.healper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // ================= COMMON USER DATA =================
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        if(principal != null){
            String userName = principal.getName();
            User user = userRepository.findByEmail(userName);
            model.addAttribute("user", user);
        }
    }

    // ================= DASHBOARD =================
    @GetMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // ================= ADD CONTACT =================
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    @Transactional
    public String processContact(
            @Valid @ModelAttribute("contact") Contact contact,
            BindingResult result,
            @RequestParam("profileImage") MultipartFile file,
            Principal principal,
            HttpSession session,
            Model model) {

        try {

            if (result.hasErrors()) {
                model.addAttribute("contact", contact);
                return "normal/add_contact_form";
            }

            User user = userRepository.findByEmail(principal.getName());

            // File upload
            if (file.isEmpty()) {
                contact.setImage("contact.jpg");
            } else {
                contact.setImage(file.getOriginalFilename());

                File saveDir = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveDir.getAbsolutePath()
                        + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            userRepository.save(user);

            session.setAttribute("message",
                    new Message("Contact added successfully!", "success"));

            return "redirect:/user/add-contact";

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message",
                    new Message("Something went wrong!", "danger"));
        }

        return "normal/add_contact_form";
    }

    // ================= SHOW CONTACTS (PAGINATION) =================
    @GetMapping("/show-contacts")
    public String showContactsFirstPage(Model model, Principal principal) {
        return showContacts(0, model, principal);
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,
                               Model model,
                               Principal principal) {

        User user = userRepository.findByEmail(principal.getName());

        Pageable pageable = PageRequest.of(page, 5);

        Page<Contact> contacts =
                contactRepository.findContactsByUser(user.getId(), pageable);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        model.addAttribute("title", "Your Contacts");

        return "normal/show_contacts";
    }

    // ================= CONTACT DETAIL =================
    @GetMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId,
                                    Model model,
                                    Principal principal) {

        Optional<Contact> optionalContact =
                contactRepository.findById(cId);

        if (optionalContact.isPresent()) {

            Contact contact = optionalContact.get();
            User user = userRepository.findByEmail(principal.getName());

            if(user.getId() == contact.getUser().getId()) {
                model.addAttribute("contact", contact);
                return "normal/contact_detail";
            }
        }

        return "redirect:/user/show-contacts";
    }

    // ================= DELETE CONTACT =================
    @GetMapping("/delete-contact/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,
                                HttpSession session,
                                Principal principal) {

        Optional<Contact> optionalContact =
                contactRepository.findById(cId);

        if (optionalContact.isPresent()) {

            Contact contact = optionalContact.get();
            User user = userRepository.findByEmail(principal.getName());

            if(user.getId() == contact.getUser().getId()) {
            	contact.setUser(null);
                contactRepository.delete(contact);

                session.setAttribute("message",
                        new Message("Contact deleted successfully!", "success"));
            }
        }

        return "redirect:/user/show-contacts";
    }

    // ================= UPDATE CONTACT =================
    @GetMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId,
                             Model model,
                             Principal principal) {

        Optional<Contact> optionalContact =
                contactRepository.findById(cId);

        if (optionalContact.isPresent()) {

            Contact contact = optionalContact.get();
            User user = userRepository.findByEmail(principal.getName());

            if(user.getId() == contact.getUser().getId()) {
                model.addAttribute("contact", contact);
                return "normal/update_contact";
            }
        }

        return "redirect:/user/show-contacts";
    }

    @PostMapping("/process-update")
    public String processUpdate(@ModelAttribute Contact contact,
                                HttpSession session,
                                Principal principal) {

        User user = userRepository.findByEmail(principal.getName());

        Optional<Contact> oldContactOptional =
                contactRepository.findById(contact.getcId());

        if (oldContactOptional.isPresent()) {

            Contact oldContact = oldContactOptional.get();

            if (user.getId() == oldContact.getUser().getId()) {

                contact.setUser(user);
                if(contact.getImage() == null || contact.getImage().isEmpty()) {
                    contact.setImage(oldContact.getImage());
                }
                contactRepository.save(contact);

                session.setAttribute("message",
                        new Message("Contact updated successfully!", "success"));

                return "redirect:/user/contact/" + contact.getcId();
            }
        }

        return "redirect:/user/show-contacts";
    }
    @GetMapping("/profile")
    public String yourprofile(Model model) {
    	model.addAttribute("title", "profile page");
    	
        return "normal/profile";
    }
    
    //open setting handler 
    @GetMapping("/settings")
    public String openSettings() {
    	return "normal/settings";
    }
    
   
    
}













