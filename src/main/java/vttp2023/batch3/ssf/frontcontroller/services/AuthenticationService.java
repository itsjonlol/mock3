package vttp2023.batch3.ssf.frontcontroller.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2023.batch3.ssf.constant.Url;
import vttp2023.batch3.ssf.exception.AuthenticationException;
import vttp2023.batch3.ssf.frontcontroller.respositories.AuthenticationRepository;

@Service
public class AuthenticationService {

	@Autowired
	AuthenticationRepository authenticationRepository;

	// TODO: Task 2
	// DO NOT CHANGE THE METHOD'S SIGNATURE

	RestTemplate restTemplate = new RestTemplate();

	// Write the authentication method in here
	public void authenticate(String username, String password) throws Exception {
		JsonObject payloadJson = Json.createObjectBuilder()
									 .add("username",username)
									 .add("password",password)
									 .build();

		String payloadJsonString = payloadJson.toString();
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		
        RequestEntity<String> requestEntity = RequestEntity.post(Url.authUrl + "/api/authenticate")
                                                           .headers(headers)
                                                           .body(payloadJsonString);
        
		try {
		// Make the request using RestTemplate
				ResponseEntity<String> responseResult = restTemplate.exchange(requestEntity, String.class);

				// If successful, check response status and process (e.g., HTTP 201 Created)
				if (responseResult.getStatusCode() == HttpStatus.CREATED) {
					// Handle successful authentication (authenticated)
					System.out.println(responseResult.getHeaders());
					System.out.println(responseResult.getStatusCode());
					System.out.println("Authenticated successfully");
				}
			} catch (HttpClientErrorException.BadRequest ex) {
				// Handle bad payload (400 Bad Request)
				System.out.println("Bad request: The payload is invalid.");
				throw new IllegalArgumentException("The payload is invalid. Please check the provided data.");
			} catch (HttpClientErrorException.Unauthorized ex) {
				// Handle unauthorized (invalid username/password)
				System.out.println("Unauthorized: Invalid username or password.");
				throw new AuthenticationException("Invalid username or password");
			} catch (HttpClientErrorException.NotFound ex) {
				throw new AuthenticationException("Page not found");
			}
		}

    
	
	public List<String> showCaptcha() {
		List<String> operators = new ArrayList<>(Arrays.asList("+","-"));
		List<Integer> integerList = new ArrayList<>();
		List<String> displayCaptcha = new ArrayList<>();
		for (int i = 0; i<50;i++) {
			integerList.add(i);
		}
		Random random = new Random();
		String randomNumber1 = String.valueOf(integerList.get(random.nextInt(integerList.size())));
		String randomNumber2 = String.valueOf(integerList.get(random.nextInt(integerList.size())));
		String randomOperator = operators.get(random.nextInt(operators.size()));
		displayCaptcha.add(randomNumber1.trim());
		displayCaptcha.add(randomOperator.trim());
		displayCaptcha.add(randomNumber2.trim());
		
		// String displayCaptcha = String.format("%s%s%s",randomNumber1,randomOperator,randomNumber2);
		return displayCaptcha;
		
	}
	
	public Boolean isCaptchaCorrect(Integer answer,List<String> captcha) {
		Integer firstNumber = Integer.valueOf(captcha.get(0));
		Integer secondNumber =Integer.valueOf(captcha.get(2));
		String operator = captcha.get(1).trim();
		Integer correctAnswer = 0;
		if (operator.equals("+")) {
			correctAnswer = firstNumber + secondNumber;
		} else if (operator.equals("-")) {
			correctAnswer = firstNumber - secondNumber;
		} else if (operator.equals("*")) {
			correctAnswer = firstNumber * secondNumber;
		} else if (operator.equals("/")) {
			correctAnswer = firstNumber / secondNumber;
		}
		return answer == correctAnswer;

	}
	// TODO: Task 3
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to disable a user account for 30 mins
	public void disableUser(String username) {
		authenticationRepository.setKeyWithTTL(username, "to delete", 30);

	}

	// TODO: Task 5
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to check if a given user's login has been disabled
	public boolean isLocked(String username) {
		return authenticationRepository.checkExists(username);
	}
	
	public void addUser(String username) {
		authenticationRepository.createValue(username, "authenticated");
	}
}
