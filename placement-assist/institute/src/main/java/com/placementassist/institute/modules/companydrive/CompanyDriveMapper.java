package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.modules.companydrive.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyDriveMapper {

    CompanyResponse toCompanyResponse(Company company);

    @Mapping(target = "id", ignore = true)
    Company toCompanyEntity(CreateCompanyRequest request);

    @Mapping(target = "id", ignore = true)
    void updateCompanyFromDto(UpdateCompanyRequest request, @MappingTarget Company company);

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyLogoUrl", source = "company.logoUrl")
    @Mapping(target = "eligibleBranches", expression = "java(drive.getEligibleBranchesList())")
    @Mapping(target = "roles", expression = "java(drive.getRolesList())")
    DriveResponse toDriveResponse(Drive drive);

    @Mapping(target = "company", source = "company")
    @Mapping(target = "eligibleBranches", expression = "java(drive.getEligibleBranchesList())")
    @Mapping(target = "roles", expression = "java(drive.getRolesList())")
    @Mapping(target = "eligibleStudentsCount", ignore = true)
    @Mapping(target = "rounds", ignore = true)
    DriveDetailResponse toDriveDetailResponse(Drive drive);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "eligibleBranches", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Drive toDriveEntity(CreateDriveRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "eligibleBranches", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateDriveFromDto(UpdateDriveRequest request, @MappingTarget Drive drive);
}
