package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.config.JwtService;
import com.panda.medicineinventorymanagementsystem.dto.AuthRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.AuthResponseDTO;
import com.panda.medicineinventorymanagementsystem.dto.UserRequestDTO;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.exception.UserNameAlreadyExistsException;
import com.panda.medicineinventorymanagementsystem.exception.UserNotFoundException;
import com.panda.medicineinventorymanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterUser_UserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(UserNameAlreadyExistsException.class, () -> userService.registerUser(user));
    }

    @Test
    public void testRegisterUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken(user)).thenReturn("token");
        when(userRepository.save(user)).thenReturn(user);

        AuthResponseDTO result = userService.registerUser(user);

        assertEquals("token", result.getToken());
    }

    @Test
    public void testLoginUser_UserNotFound() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("test@example.com");
        authRequestDTO.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loginUser(authRequestDTO));
    }

    @Test
    public void testLoginUser_BadCredentials() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("test@example.com");
        authRequestDTO.setPassword("wrongpassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.loginUser(authRequestDTO));
    }

    @Test
    public void testLoginUse() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("test@example.com");
        authRequestDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(user)).thenReturn("token");

        AuthResponseDTO result = userService.loginUser(authRequestDTO);

        assertEquals("token", result.getToken());
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertEquals(1, result.getId());
    }

    @Test
    public void testUpdateUser_NotFound() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("New Name");
        userRequestDTO.setPassword("newPassword");

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1, userRequestDTO));
    }

    @Test
    public void testUpdateUser() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setName("Old Name");

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("New Name");
        userRequestDTO.setPassword("newPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1, userRequestDTO);

        assertEquals("New Name", updatedUser.getName());
    }

    @Test
    public void testDeleteUserById_NotFound() {
        when(userRepository.existsById(1)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1));
    }

    @Test
    public void testDeleteUserById() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        assertTrue(userService.deleteUserById(1));
    }

    @Test
    public void testGetCurrentAuthenticatedUser_NotFound() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("unknown@user.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("unknown@user.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentAuthenticatedUser());
    }



    @Test
    public void testGetCurrentAuthenticatedUser() {
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.getCurrentAuthenticatedUser();

        assertEquals("user@example.com", result.getEmail());
    }

}
