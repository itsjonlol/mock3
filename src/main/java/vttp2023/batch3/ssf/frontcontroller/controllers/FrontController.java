package vttp2023.batch3.ssf.frontcontroller.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vttp2023.batch3.ssf.exception.AuthenticationException;
import vttp2023.batch3.ssf.frontcontroller.models.User;
import vttp2023.batch3.ssf.frontcontroller.services.AuthenticationService;


@Controller
public class FrontController {
	@Autowired
	AuthenticationService authenticationService;

	@GetMapping("/")
	public String showLandingPage() {
		return "view0";
	}

	// TODO: Task 2, Task 3, Task 4, Task 6

	@PostMapping("/login")
	public String loginRequest(@RequestBody MultiValueMap<String,String> form,Model model,HttpSession session,
	RedirectAttributes redirectAttributes) throws Exception {
		String username = form.getFirst("username");
		String password = form.getFirst("password");
		String captchaAnswer = form.getFirst("captchaAnswer");
		
		
		
		if (username.length() < 2 || password.length()<2) {
			String errorMessage = "Both username and password must be more than 2 characters";
			model.addAttribute("errorMessage",errorMessage);
			
			return "view0";
		}
		
		
		String sessionKey = "user_" + username;
		User user = (User) session.getAttribute(sessionKey);
		if (user ==null) {

			user = new User(username,0);
		}
		
		
		// user.setName(username);
		System.out.println("Incorrect login attempt for " + user.getName() + user.getNoOfAttempts());
		if (user.getNoOfAttempts()==3) {
			authenticationService.disableUser(user.getName());
			user.setNoOfAttempts(0);
		}
		if (authenticationService.isLocked(user.getName())) {
			redirectAttributes.addFlashAttribute("disableduser", user);
			return "redirect:/disabled";
		}


		

		

		try {
			// Get the captcha from the session
			// List<String> storedCaptcha = (List<String>) session.getAttribute("captcha");
			
			if (captchaAnswer!=null) {
				Object captchaObj = session.getAttribute("captcha");
				List<String> storedCaptcha = (List<String>) captchaObj; //cannot use modelattribute cause not binded
				
				// if (captchaObj == null) {
				// 	System.out.println("Captcha is null in session");
				// } else {
				// 	System.out.println("Captcha object type: " + captchaObj.getClass().getName());
				// 	System.out.println("Captcha value: " + captchaObj);
				// }
				// System.out.println(authenticationService.isCaptchaCorrect(Integer.valueOf(captchaAnswer), storedCaptcha));
				//first check to see if pass captcha. use a session to store the captcha
				if (!authenticationService.isCaptchaCorrect(Integer.valueOf(captchaAnswer), storedCaptcha)) {
					user.setNoOfAttempts(user.getNoOfAttempts()+1);
					session.setAttribute(sessionKey, user);
					Boolean failedLoginAttempt = true;
					model.addAttribute("failedLoginAttempt",failedLoginAttempt);
					List<String> captcha = authenticationService.showCaptcha();
					model.addAttribute("captcha",captcha);
					
					
					return "view0";

				}
				//if passed captcha, go authenticate
				authenticationService.authenticate(username, password);
				

			}
			//first attempt with no captcha
			authenticationService.authenticate(username, password);

			
		} catch (AuthenticationException ex) {
			// If authentication fails, add the error message to the model
			//whenever you fail, add number of attempts to the user
			
			user.setNoOfAttempts(user.getNoOfAttempts()+1);
			session.setAttribute(sessionKey, user);
			model.addAttribute("errorMessage", ex.getMessage());

			Boolean failedLoginAttempt = true;
			model.addAttribute("failedLoginAttempt",failedLoginAttempt);
			List<String> captcha = authenticationService.showCaptcha();
			model.addAttribute("captcha",captcha);
			session.setAttribute("captcha", captcha);
	
			

			return "view0"; // Return to the login page with the error message
		}
		
		
		user.setNoOfAttempts(0); // reset counter
		
		return "view1";
		
		
	}

	@GetMapping("/disabled")
	public String getDisabledEntry(@ModelAttribute("disableduser") User user,Model model) {
		model.addAttribute("user",user);
		return "view2";
	}
	
	
}
