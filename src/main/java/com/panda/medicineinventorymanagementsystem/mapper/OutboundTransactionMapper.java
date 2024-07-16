package com.panda.medicineinventorymanagementsystem.mapper;

import com.panda.medicineinventorymanagementsystem.dto.OutboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface OutboundTransactionMapper {

    @Mapping(source = "medicineId", target = "medicine", qualifiedByName = "mapMedicineFromId")
    OutboundTransaction toEntity(OutboundTransactionDTO dto, @Context Map<Integer, Medicine> medicineMap);

    @Mapping(source = "medicine.id", target = "medicineId")
    OutboundTransactionDTO toDTO(OutboundTransaction entity);

    @Named("mapMedicineFromId")
    default Medicine mapMedicineFromId(Integer id, @Context Map<Integer, Medicine> medicineMap) {
        return medicineMap.getOrDefault(id, null);
    }
}
