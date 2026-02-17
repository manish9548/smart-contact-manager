package com.smart.healper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

    public void removeMessageFromSession() {
        try {
            System.out.println("Removing message from session...");
            
            // Session nikalne ka tarika
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest().getSession();
            
            // Message ko remove karna
            session.removeAttribute("message");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}