package vttp2023.batch3.ssf.frontcontroller.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/test/api")
public class AuthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // POST method to handle authentication
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody String jsonPayload) {
        try {
            // Parse the incoming JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonPayload);

            // Extract username and password from the parsed JSON
            String username = jsonNode.get("username").asText() + "A";
            String password = jsonNode.get("password").asText();

            // Use Redis to check if the username exists and retrieve the stored password
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String storedPassword = ops.get(username);

            // If username does not exist, create it in Redis with the provided password
            if (storedPassword == null) {
                ops.set(username, password);  // Create new user with username and password
                return new ResponseEntity<>(
                    "{\"message\": \"User created and authenticated successfully\"}", HttpStatus.CREATED
                );
            }

            // If password doesn't match, return 401 Unauthorized
            if (!storedPassword.equals(password)) {
                return new ResponseEntity<>(
                    "{\"message\": \"Unauthorized: Invalid username or password\"}", HttpStatus.UNAUTHORIZED
                );
            }

            // If authentication is successful (username exists and password matches)
            return new ResponseEntity<>(
                "{\"message\": \"Authenticated successfully\"}", HttpStatus.CREATED
            );

        } catch (Exception e) {
            // You can handle exception here (for now we can ignore as requested)
            return new ResponseEntity<>(
                "{\"message\": \"An error occurred\"}", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}

