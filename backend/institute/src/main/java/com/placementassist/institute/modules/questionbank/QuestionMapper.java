package com.placementassist.institute.modules.questionbank;

import com.placementassist.institute.modules.questionbank.dto.CreateQuestionRequest;
import com.placementassist.institute.modules.questionbank.dto.QuestionResponse;
import com.placementassist.institute.modules.questionbank.dto.UpdateQuestionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuestionMapper {

    @Mapping(target = "driveId", source = "drive.id")
    @Mapping(target = "driveTitle", source = "drive.title")
    @Mapping(target = "companyName", source = "drive.company.name")
    @Mapping(target = "tags", expression = "java(question.getTagsList())")
    QuestionResponse toResponse(Question question);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "drive", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Question toEntity(CreateQuestionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "drive", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntityFromDto(UpdateQuestionRequest request, @MappingTarget Question question);
}
