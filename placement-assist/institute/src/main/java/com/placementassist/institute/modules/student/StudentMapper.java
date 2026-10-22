package com.placementassist.institute.modules.student;

import com.placementassist.institute.modules.student.dto.CreateStudentRequest;
import com.placementassist.institute.modules.student.dto.StudentResponse;
import com.placementassist.institute.modules.student.dto.UpdateStudentRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "userId", source = "user.id")
    StudentResponse toResponse(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "placementEligible", ignore = true)
    Student toEntity(CreateStudentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "rollNo", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "placementEligible", ignore = true)
    void updateEntityFromDto(UpdateStudentRequest dto, @MappingTarget Student student);
}
