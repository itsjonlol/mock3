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

	

	@PostMapping("/login2")
	public String loginRequest2(@RequestBody MultiValueMap<String,String> form,Model model,HttpSession session,RedirectAttributes 
	redirectAttributes) throws Exception {
		String username = form.getFirst("username");
		String password = form.getFirst("password");

		String captchaAnswer = form.getFirst("captchaAnswer");
		Boolean failedLoginAttempt = false;
		

		if (username.length() < 2 || password.length()<2) {
			String errorMessage = "Both username and password must be more than 2 characters";
			model.addAttribute("errorMessage",errorMessage);
			return "view0";
		}
		
		User user = (User) session.getAttribute("user");

		if (user ==null) {
			user = new User(username,0);
			session.setAttribute("user", user);
			
		}
		
		// if (authenticationService.isLocked(user.getName())) {
		// 	redirectAttributes.addFlashAttribute("disableduser", user);
		// 	return "redirect:/disabled";
		// }

		if (user.getNoOfAttempts()==2) {
			authenticationService.disableUser(user.getName());
			user.setNoOfAttempts(0);
			session.removeAttribute("user");
			
		}

		
		try {

			List<String> captcha = (List<String>)session.getAttribute("captcha");
			
			if (captchaAnswer != null) { // use the form null value
				//when captcha fails
				if(!authenticationService.isCaptchaCorrect(Integer.valueOf(captchaAnswer), captcha)) {
					if (authenticationService.isLocked(user.getName())) {
						redirectAttributes.addFlashAttribute("disableduser", user);
						return "redirect:/disabled";
					}
					
					user.setNoOfAttempts(user.getNoOfAttempts()+1);
					List<String> newCaptcha = authenticationService.showCaptcha();
					model.addAttribute("failedLoginAttempt",true);
					model.addAttribute("captcha",newCaptcha);
					session.setAttribute("captcha", newCaptcha);
					
					model.addAttribute("errorMessage","Captcha is wrong. Attempt counter: " + user.getNoOfAttempts());
					return "view0";
				} 
				//first attempt
				authenticationService.authenticate(username, password);
			}
			//first try. captcha is null. go through the authentication
			authenticationService.authenticate(username, password);

		} catch(AuthenticationException ex) {
			if (authenticationService.isLocked(user.getName())) {
				redirectAttributes.addFlashAttribute("disableduser", user);
				return "redirect:/disabled";
			}
			
			user.setNoOfAttempts(user.getNoOfAttempts()+1);
			model.addAttribute("errorMessage",ex.getMessage()+ " Attempt counter: " + user.getNoOfAttempts());
			
			failedLoginAttempt = true;
			model.addAttribute("failedLoginAttempt",failedLoginAttempt);
			List<String> captcha = authenticationService.showCaptcha();
			model.addAttribute("captcha",captcha);
			session.setAttribute("captcha", captcha);
			

			return "view0";

		}
		failedLoginAttempt = false;
		session.removeAttribute("captcha");
		user.setNoOfAttempts(0);
		session.setAttribute("authenticateduser", user);
		return "view1";
	}

	@GetMapping("/disabled")
	public String getDisabledEntry(@ModelAttribute("disableduser") User user,Model model) {
		model.addAttribute("disableduser",user);
		return "view2";
	}
	
	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "view0";
	}
	
	
}
