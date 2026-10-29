package com.placementassist.institute.modules.driveround;

import com.placementassist.institute.modules.driveround.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DriveRoundMapper {

    @Mapping(target = "driveId", source = "drive.id")
    @Mapping(target = "driveTitle", source = "drive.title")
    DriveRoundResponse toRoundResponse(DriveRound round);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "drive", ignore = true)
    DriveRound toRoundEntity(CreateRoundRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "drive", ignore = true)
    void updateRoundFromDto(UpdateRoundRequest request, @MappingTarget DriveRound round);

    @Mapping(target = "roundId", source = "round.id")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentRollNo", source = "student.rollNo")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "studentBranch", source = "student.branch")
    RoundResultResponse toResultResponse(RoundResult result);
}
