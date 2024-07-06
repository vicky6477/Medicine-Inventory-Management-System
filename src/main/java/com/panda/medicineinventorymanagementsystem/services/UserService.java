package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.dto.UserDTO;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    /**
     * Constructs a UserService with a UserRepository.
     * @param userRepository the repository for user data access
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Creates a new user in the repository.
     * @param userDTO the data transfer object containing user data
     * @return UserDTO the persisted user data
     */
    public UserDTO createUser(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    /**
     * Retrieves a user by their ID.
     * @param id the ID of the user to retrieve
     * @return UserDTO the user data transfer object
     * @throws RuntimeException if no user is found with the provided ID
     */
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    /**
     * Retrieves all users from the repository.
     * @return List<UserDTO> a list of user data transfer objects
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Updates the details of an existing user.
     * @param id the ID of the user to update
     * @param userDTO the new user data for the update
     * @return UserDTO the updated user data
     * @throws RuntimeException if no user is found with the provided ID
     */
    public UserDTO updateUserById(Integer id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        updateEntity(existingUser, userDTO);
        existingUser = userRepository.save(existingUser);
        return convertToDTO(existingUser);
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
     * Converts a User entity to a UserDTO.
     * @param user the User entity to convert
     * @return UserDTO the converted user data transfer object
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPassword(user.getPassword()); // Consider security implications
        dto.setName(user.getName());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    /**
     * Converts a UserDTO to a User entity.
     * @param dto the UserDTO to convert
     * @return User the User entity
     */
    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        return user;
    }

    /**
     * Updates an existing User entity with data from a UserDTO.
     * @param user the User entity to update
     * @param dto the UserDTO containing updated data
     */
    private void updateEntity(User user, UserDTO dto) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setUpdatedAt(LocalDateTime.now());
    }

}
