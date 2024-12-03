package vttp2023.batch3.ssf.frontcontroller.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vttp2023.batch3.ssf.frontcontroller.models.User;

@Controller
@RequestMapping("/protected")
public class ProtectedController {

    @GetMapping("")
    public String showProtectedPage(HttpSession session, RedirectAttributes redirectAttributes,Model model) {
        // Check if the user is authenticated (you can check if the user object exists in session)
       	User user = (User) session.getAttribute("authenticateduser");


	

        if (user == null ) {
            // If not authenticated or too many failed attempts, redirect to login page
            model.addAttribute("errorMessage", "You need to log in to access this resource ");
            // return "redirect:/";  // if using redirectAttributes to pass data when re-directing
			return "view0"; // if using model for returning the view
        } else if (user.getNoOfAttempts()>=3) {
			model.addAttribute("errorMessage", "You are currently blocked");
		}

        // User is authenticated, continue with the protected resource logic
		
        return "view1";  // Redirect or return a view for the protected resource
    }
}

