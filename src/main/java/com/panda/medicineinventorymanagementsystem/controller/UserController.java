package com.panda.medicineinventorymanagementsystem.controller;


import com.panda.medicineinventorymanagementsystem.dto.AuthRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.AuthResponseDTO;
import com.panda.medicineinventorymanagementsystem.dto.UserRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.UserResponseDTO;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.mapper.UserMapper;
import com.panda.medicineinventorymanagementsystem.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/signup")
    @Operation(summary = "Create a new user", description = "Adds a new user to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRequestDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error occurred. Detailed error messages are provided in the response.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Email Error", value = "{\"email\": \"Email should be valid\"}"),
                                    @ExampleObject(name = "Password Error", value = "{\"password\": \"Password must be at least 3 characters long\"}"),
                                    @ExampleObject(name = "Name Error", value = "{\"name\": \"Name is required\"}"),
                            }))
    })
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.registerUser(userMapper.toUser(requestDTO)));
    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(userService.loginUser(authRequestDTO));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(userMapper::toUserResponseDTO).toList());
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userMapper.toUserResponseDTO(userService.getUserById(id)));
    }

    //considering to change id to email to update.
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<User> updateUser(@Valid @PathVariable Integer id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        User updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID", description = "Delete a user by their unique identifier.")
    @ApiResponse(responseCode = "200", description = "User deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description ="User not found with this id")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello, no need to authenticate to visit");
    }
}
