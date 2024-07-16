package com.panda.medicineinventorymanagementsystem.mapper;

import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MedicineMapper {
    Medicine toEntity(MedicineDTO dto);

    MedicineDTO toDTO(Medicine entity);

    @Mapping(target = "name", source = "name", ignore = true)
    @Mapping(target = "description", expression = "java(dto.getDescription() != null ? dto.getDescription() : entity.getDescription())")
    @Mapping(target = "quantity", source = "quantity", ignore = true)
    @Mapping(target = "id", source = "id", ignore = true)
    void updateEntityFromDTO(MedicineDTO dto, @MappingTarget Medicine entity);
}

