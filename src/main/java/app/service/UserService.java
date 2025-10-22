package app.service;

import app.dto.AuthResponseDTO;
import app.dto.UserDTO;
import app.entities.User;
import app.repository.UserRepository;
import app.security.JwtUtil;
import app.security.PasswordUtil;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO register(UserDTO userDTO) throws Exception {
        // Check if username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new Exception("Username already exists");
        }

        // Hash password
        String hashedPassword = PasswordUtil.hashPassword(userDTO.getPassword());

        // Create new user
        User user = new User(userDTO.getUsername(), hashedPassword);
        userRepository.save(user);

        // Return response
        UserDTO response = new UserDTO(user.getUsername());
        response.setMessage("User created");
        return response;
    }

    public AuthResponseDTO login(UserDTO userDTO) throws Exception {
        // Find user by username
        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user == null) {
            throw new Exception("Invalid username or password");
        }

        // Verify password
        if (!PasswordUtil.verifyPassword(userDTO.getPassword(), user.getPassword())) {
            throw new Exception("Invalid username or password");
        }

        // Generate JWT token
        String token = JwtUtil.generateToken(user.getUsername(), user.getRole());

        return new AuthResponseDTO(user.getUsername(), token);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}