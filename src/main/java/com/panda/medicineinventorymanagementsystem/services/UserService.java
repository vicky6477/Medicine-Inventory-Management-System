package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.config.JwtService;
import com.panda.medicineinventorymanagementsystem.dto.AuthRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.AuthResponseDTO;
import com.panda.medicineinventorymanagementsystem.dto.UserRequestDTO;

import com.panda.medicineinventorymanagementsystem.entity.Role;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.exception.UserNotFoundException;
import com.panda.medicineinventorymanagementsystem.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public AuthResponseDTO registerUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists.");
        }

        // Encode the password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthResponseDTO
                .builder()
                .token(token)
                .build();
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public AuthResponseDTO loginUser(AuthRequestDTO authRequestDTO) {
       authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getPassword()
                )
        );

        // find user
        User user = userRepository.findByEmail(authRequestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + authRequestDTO.getEmail()));

        String jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    public User updateUser(Integer id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if (userRequestDTO.getName() != null && !userRequestDTO.getName().isEmpty()) {
            user.setName(userRequestDTO.getName());
        }
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     * @param id the ID of the user to delete
     * @return Boolean true if the deletion was successful
     * @throws RuntimeException if no user is found with the provided ID
     */
    public Boolean deleteUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return true;
    }

    /**
     * Fetches the currently authenticated user from the security context.
     * Assumes the 'username' in the UserDetails is the user's email.
     *
     * @return The authenticated User entity.
     * @throws UsernameNotFoundException if the user is not found in the repository.
     * @throws IllegalStateException if the security context does not hold a valid UserDetails.
     */
    public User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername(); // Here, username is actually the email.
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        } else {
            throw new IllegalStateException("User not found in session.");
        }
    }
}
