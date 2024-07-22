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
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
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
    @Operation(summary = "User login", description = "Authenticates user and returns a token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Incorrect password provided", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(userService.loginUser(authRequestDTO));
    }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users.")
    @ApiResponse(responseCode = "200", description = "List of all users", content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(userMapper::toUserResponseDTO).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found with ID", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "You need to log in to access this resource", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable @Min(1) Integer id) {
        return ResponseEntity.ok(userMapper.toUserResponseDTO(userService.getUserById(id)));
    }


    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    @Validated
    @Operation(summary = "Update user information", description = "Updates the user details for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error occurred. Detailed error messages are provided in the response.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Password Error", value = "{\"password\": \"Password must be at least 3 characters long\"}"),
                                    @ExampleObject(name = "Name Error", value = "{\"name\": \"Name is required\"}"),
                            }))
    })
    public ResponseEntity<User> updateUser(@PathVariable @Min(1) Integer id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        User updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Delete user by ID", description = "Delete a user by their unique identifier.")
    @ApiResponse(responseCode = "200", description = "User deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description ="User not found with this id")
    public Boolean deleteUser(@PathVariable @Min(1) Integer id) {
        return userService.deleteUserById(id);
    }

}
