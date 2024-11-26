package com.example.demo.utils.mapper;

import com.example.demo.dto.SensorRecordDTO;
import com.example.demo.model.agriculture.SensorRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SensorRecordMapper {
    SensorRecordDTO toDto(SensorRecord sensorRecord);
    SensorRecord toEntity(SensorRecordDTO sensorRecordDTO);
    List<SensorRecordDTO> toDtoList(List<SensorRecord> sensorRecords);
    List<SensorRecord> toEntityList(List<SensorRecordDTO> sensorRecordDTOs);
}
