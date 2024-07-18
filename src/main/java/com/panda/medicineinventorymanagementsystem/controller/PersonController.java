package com.panda.medicineinventorymanagementsystem.controller;


import com.panda.medicineinventorymanagementsystem.dto.AuthRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.AuthResponseDTO;
import com.panda.medicineinventorymanagementsystem.dto.PersonRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.PersonResponseDTO;
import com.panda.medicineinventorymanagementsystem.entity.Person;
import com.panda.medicineinventorymanagementsystem.mapper.PersonMapper;
import com.panda.medicineinventorymanagementsystem.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class PersonController {
    private final PersonService personService;
    private final PersonMapper personMapper;

    @Autowired
    public PersonController(PersonService personService, PersonMapper personMapper) {
        this.personService = personService;
        this.personMapper = personMapper;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(personService.authenticate(authRequestDTO));
    }

    @GetMapping("/persons")
    public ResponseEntity<List<PersonResponseDTO>> getAllPersons() {
        return ResponseEntity.ok(personService.getAllPersons().stream().map(personMapper::toPersonResponseDTO).toList());
    }

    @GetMapping("/persons/{id}")
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable Integer id) {
        return ResponseEntity.ok(personMapper.toPersonResponseDTO(personService.getPersonById(id)));
    }

    @PostMapping("/persons")
    public ResponseEntity<AuthResponseDTO> createPerson(@RequestBody PersonRequestDTO requestDTO) {
        return ResponseEntity.ok(personService.savePerson(personMapper.toPerson(requestDTO)));
    }

    @DeleteMapping("/persons/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Boolean> deletePerson(@PathVariable Integer id) {
        return ResponseEntity.ok(personService.deletePerson(id));
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello, no need to authenticate to visit");
    }
}
