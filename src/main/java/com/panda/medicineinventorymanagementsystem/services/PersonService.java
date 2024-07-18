package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.config.JwtService;
import com.panda.medicineinventorymanagementsystem.dto.AuthRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.AuthResponseDTO;
import com.panda.medicineinventorymanagementsystem.entity.Person;
import com.panda.medicineinventorymanagementsystem.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person getPersonById(Integer id) {
        return personRepository.findById(id).orElse(null);
    }

    public AuthResponseDTO savePerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(person);
        String token = jwtService.generateToken(person);
        return AuthResponseDTO
                .builder()
                .token(token)
                .build();
    }

    public Boolean deletePerson(Integer id) {
        personRepository.deleteById(id);
        return true;
    }

    public AuthResponseDTO authenticate(AuthRequestDTO authRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getPassword()
                )
        );

        // find user
        Person person = personRepository.findByEmail(authRequestDTO.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(person);
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }
}
