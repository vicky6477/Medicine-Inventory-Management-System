package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.config.JwtService;
import com.panda.medicineinventorymanagementsystem.dto.AuthRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.AuthResponseDTO;
import com.panda.medicineinventorymanagementsystem.dto.UserRequestDTO;

import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.exception.UserNameAlreadyExistsException;
import com.panda.medicineinventorymanagementsystem.exception.UserNotFoundException;
import com.panda.medicineinventorymanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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


    /**
     * Registers a new user in the system.
     *
     * @param user The user to register.
     * @return A response containing the JWT token.
     * @throws UserNameAlreadyExistsException If the email is already in use.
     */
    public AuthResponseDTO registerUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserNameAlreadyExistsException("Email already exists.");
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

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param authRequestDTO The login credentials.
     * @return A response containing the JWT token.
     * @throws UsernameNotFoundException If the user's email is not found.
     */
    public AuthResponseDTO loginUser(AuthRequestDTO authRequestDTO) {
        try {
            // This assumes your UserDetailsService implementation throws UsernameNotFoundException if the user doesn't exist
            User user = userRepository.findByEmail(authRequestDTO.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authRequestDTO.getEmail()));

            // Now attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDTO.getEmail(),
                            authRequestDTO.getPassword()
                    )
            );

            // Assuming authentication was successful, generate token
            String jwtToken = jwtService.generateToken(user);
            return AuthResponseDTO.builder().token(jwtToken).build();

        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found with email: " + authRequestDTO.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect password provided.");
        }
    }


    /**
     * Retrieves all users in the system.
     *
     * @return A list of all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The found user.
     * @throws UserNotFoundException If the user is not found.
     */
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }


    /**
     * Updates the information of a user.
     *
     * @param id The ID of the user to update.
     * @param userRequestDTO The new information to update.
     * @return The updated user.
     * @throws UserNotFoundException If the user is not found.
     */
    public User updateUser(Integer id, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        existingUser.setName(userRequestDTO.getName());
        existingUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        return userRepository.save(existingUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return true if the user was successfully deleted.
     * @throws UserNotFoundException If no user is found with the provided ID.
     */
    public Boolean deleteUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return true;
    }



    /**
     * Retrieves the currently authenticated user.
     *
     * @return The authenticated user.
     * @throws UsernameNotFoundException If the user's email is not found in the database.
     * @throws IllegalStateException If the user is not found in the security context.
     */
    public User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername(); // Here, username is actually the email.
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        } else {
            throw new UserNotFoundException("User not found in session.");
        }
    }
}
