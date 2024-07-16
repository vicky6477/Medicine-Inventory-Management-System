package com.panda.medicineinventorymanagementsystem.controller;
import com.panda.medicineinventorymanagementsystem.dto.UserDTO;
import com.panda.medicineinventorymanagementsystem.services.UserService;
import com.panda.medicineinventorymanagementsystem.util.ControllerHelper;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public  UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Adds a new user to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error occurred. Detailed error messages are provided in the response.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Username Error", value = "{\"username\": \"Username must be between 3 and 10 characters\"}"),
                                    @ExampleObject(name = "Email Error", value = "{\"email\": \"Email should be valid\"}"),
                                    @ExampleObject(name = "Password Error", value = "{\"password\": \"Password must be at least 6 characters long\"}"),
                                    @ExampleObject(name = "Name Error", value = "{\"name\": \"Name is required\"}"),
                                    @ExampleObject(name = "Role Error", value = "{\"role\": \"Role is required\"}")
                            }))
    })
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerHelper.formatBindingErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their unique identifier.")
    @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "404", description = "User not found with this id ")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            UserDTO userDTO = userService.getUserById(id);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping
    @Operation(summary = "List all users", description = "Retrieve a list of all users in the system.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users); 
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID", description = "Update a user's information by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found with this id"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Username Error", value = "{\"username\": \"Username must be between 3 and 10 characters\"}"),
                                    @ExampleObject(name = "Email Error", value = "{\"email\": \"Email should be valid\"}"),
                                    @ExampleObject(name = "Password Error", value = "{\"password\": \"Password must be at least 6 characters long\"}"),
                                    @ExampleObject(name = "Name Error", value = "{\"name\": \"Name is required\"}"),
                                    @ExampleObject(name = "Role Error", value = "{\"role\": \"Role is required\"}")
                            }))
    })
    public ResponseEntity<?> updateUserById(@PathVariable Integer id, @Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerHelper.formatBindingErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            UserDTO updatedUser = userService.updateUserById(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID", description = "Delete a user by their unique identifier.")
    @ApiResponse(responseCode = "200", description = "User deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description ="User not found with this id")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
