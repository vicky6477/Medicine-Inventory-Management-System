package com.panda.medicineinventorymanagementsystem.mapper;

import com.panda.medicineinventorymanagementsystem.dto.InboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface InboundTransactionMapper {

    @Mapping(source = "medicineId", target = "medicine", qualifiedByName = "mapMedicineFromId")
    @Mapping(source =  "userId", target = "user", qualifiedByName = "mapUserFromId")
    InboundTransaction toEntity(InboundTransactionDTO dto, @Context Map<Integer, Medicine> medicineMap, @Context Map<Integer, User> userMap);

    @Mapping(source = "medicine.id", target = "medicineId")
    @Mapping(source =  "user.id", target = "userId")
    InboundTransactionDTO toDTO(InboundTransaction entity);

    @Named("mapMedicineFromId")
    default Medicine mapMedicineFromId(Integer id, @Context Map<Integer, Medicine> medicineMap) {
        return medicineMap.getOrDefault(id, null);
    }

    @Named("mapUserFromId")
    default User mapUserFromId(Integer id, @Context Map<Integer, User> userMap) {
        return userMap.getOrDefault(id, null);
    }

}