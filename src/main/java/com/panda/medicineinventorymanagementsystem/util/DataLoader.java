package com.panda.medicineinventorymanagementsystem.util;

import com.panda.medicineinventorymanagementsystem.entity.Person;
import com.panda.medicineinventorymanagementsystem.entity.Role;
import com.panda.medicineinventorymanagementsystem.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private PersonRepository personRepository;

    private PasswordEncoder encoder;

    @Autowired
    public DataLoader(PersonRepository personRepository, PasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        // Add initial data to the database
        if (personRepository.count() == 0) {
            personRepository.save(new Person(null, "Léa Seydoux", "lea.seydoux@gmail.com", encoder.encode("123"), Role.ADMIN,now, now));
            personRepository.save(new Person(null, "Angela Sarafyan", "angela.sarafyan@gmail.com", encoder.encode("123"), Role.USER,now, now));
            personRepository.save(new Person(null, "Talulah Riley", "talulah.riley@gmail.com", encoder.encode("321"), Role.USER,now, now));
        }
    }
}