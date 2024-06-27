package com.panda.medicineinventorymanagementsystem.services;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    //Constructor injection
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Retrieve a user by its ID
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("id not found"));
    }

    // Retrieve all users info
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update the details of an existing user
    public User updateUser(Integer id, User userDetails) {
        //Retrieve a user by its ID, if user does not exist, throw exception
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setPassword(userDetails.getPassword());
        return userRepository.save(user);
    }
    //delete a user by ID
    public Boolean deleteUserById(Integer id) {
        userRepository.deleteById(id);
        return !userRepository.existsById(id);
    }


}
