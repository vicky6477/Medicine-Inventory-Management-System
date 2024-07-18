package com.panda.medicineinventorymanagementsystem.mapper;

import com.panda.medicineinventorymanagementsystem.dto.PersonRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.PersonResponseDTO;
import com.panda.medicineinventorymanagementsystem.entity.Person;
import com.panda.medicineinventorymanagementsystem.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface PersonMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(target = "role", expression = "java(getRole(dto.getRole()))")
    Person toPerson(PersonRequestDTO dto);

    default Role getRole(String role) {
        return Role.valueOf(role);
    }

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "role", expression = "java(person.getRole().toString())")
    PersonResponseDTO toPersonResponseDTO(Person person);
}

