package com.panda.medicineinventorymanagementsystem.mapper;
import com.panda.medicineinventorymanagementsystem.dto.UserRequestDTO;
import com.panda.medicineinventorymanagementsystem.dto.UserResponseDTO;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(target = "role", expression = "java(getRole(dto.getRole()))")
    User toUser(UserRequestDTO dto);

    default Role getRole(String role) {
        return Role.valueOf(role);
    }

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserResponseDTO toUserResponseDTO(User user);
}

