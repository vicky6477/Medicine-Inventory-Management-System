package com.panda.medicineinventorymanagementsystem.controller;
import com.panda.medicineinventorymanagementsystem.entity.Role;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public  UserController(UserService userService) {
        this.userService = userService;
    }
    /*
    * ResponseEntity:
    *        fully control the HTTP response for RESTful services
    *        specify the status code, headers, and body directly from service methods,
    *        enhancing API flexibility and clarity in handling different HTTP responses efficiently
    * */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean>  deleteUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.deleteUserById(id));
    }

}
