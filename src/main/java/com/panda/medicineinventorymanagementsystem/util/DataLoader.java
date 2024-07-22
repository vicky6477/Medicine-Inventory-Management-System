package com.panda.medicineinventorymanagementsystem.util;

import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.entity.Role;
import com.panda.medicineinventorymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private UserRepository userRepository;

    private PasswordEncoder encoder;

    @Autowired
    public DataLoader(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        // Add initial data to the database
        if (userRepository.count() == 0) {
            userRepository.save(new User(null, "Léa Seydoux", "lea.seydoux@gmail.com", encoder.encode("123"), Role.ADMIN,now, now));
            userRepository.save(new User(null, "Angela Sarafyan", "angela.sarafyan@gmail.com", encoder.encode("123"), Role.USER,now, now));
            userRepository.save(new User(null, "Talulah Riley", "talulah.riley@gmail.com", encoder.encode("321"), Role.USER,now, now));
        }
    }
}