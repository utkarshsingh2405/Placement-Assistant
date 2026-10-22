package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.common.enums.DriveStatus;
import com.placementassist.institute.modules.companydrive.dto.*;
import com.placementassist.institute.modules.driveround.DriveRound;
import com.placementassist.institute.modules.driveround.DriveRoundRepository;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentMapper;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.institute.modules.student.dto.StudentResponse;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.exception.ConflictException;
import com.placementassist.shared.exception.NotFoundException;
import com.placementassist.shared.util.DateUtil;
import com.placementassist.shared.util.SpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing company profiles and campus placement drive lifecycles.
 */
@Service
public class CompanyDriveService {

    private final CompanyRepository companyRepository;
    private final DriveRepository driveRepository;
    private final StudentRepository studentRepository;
    private final DriveRoundRepository driveRoundRepository;
    private final CompanyDriveMapper mapper;
    private final StudentMapper studentMapper;

    public CompanyDriveService(
            CompanyRepository companyRepository,
            DriveRepository driveRepository,
            StudentRepository studentRepository,
            DriveRoundRepository driveRoundRepository,
            CompanyDriveMapper mapper,
            StudentMapper studentMapper) {
        this.companyRepository = companyRepository;
        this.driveRepository = driveRepository;
        this.studentRepository = studentRepository;
        this.driveRoundRepository = driveRoundRepository;
        this.mapper = mapper;
        this.studentMapper = studentMapper;
    }

    // --- COMPANY OPERATIONS ---

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        if (companyRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ConflictException("Company", "name", request.getName().trim());
        }
        Company company = mapper.toCompanyEntity(request);
        company = companyRepository.save(company);
        return mapper.toCompanyResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(Long id, UpdateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company", id));
        mapper.updateCompanyFromDto(request, company);
        company = companyRepository.save(company);
        return mapper.toCompanyResponse(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company", id));
        return mapper.toCompanyResponse(company);
    }

    @Transactional(readOnly = true)
    public PageResponse<CompanyResponse> listCompanies(String search, Pageable pageable) {
        Specification<Company> spec = SpecificationBuilder.<Company>create()
                .withSearch(search, "name", "industry", "headquarters")
                .build();
        Page<Company> page = companyRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(mapper::toCompanyResponse));
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company", id));
        companyRepository.delete(company);
    }

    // --- DRIVE OPERATIONS ---

    @Transactional
    public DriveResponse createDrive(CreateDriveRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NotFoundException("Company", request.getCompanyId()));

        Drive drive = mapper.toDriveEntity(request);
        drive.setCompany(company);
        drive.setEligibleBranchesList(request.getEligibleBranches());
        drive.setRolesList(request.getRoles());

        drive = driveRepository.save(drive);
        return mapper.toDriveResponse(drive);
    }

    @Transactional
    public DriveResponse updateDrive(Long id, UpdateDriveRequest request) {
        Drive drive = driveRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drive", id));

        mapper.updateDriveFromDto(request, drive);
        if (request.getEligibleBranches() != null) {
            drive.setEligibleBranchesList(request.getEligibleBranches());
        }
        if (request.getRoles() != null) {
            drive.setRolesList(request.getRoles());
        }

        drive = driveRepository.save(drive);
        return mapper.toDriveResponse(drive);
    }

    @Transactional(readOnly = true)
    public DriveDetailResponse getDriveById(Long id) {
        Drive drive = driveRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drive", id));

        DriveDetailResponse response = mapper.toDriveDetailResponse(drive);

        // Fetch Rounds
        List<DriveRound> rounds = driveRoundRepository.findByDriveIdOrderByOrderNoAsc(id);
        List<DriveDetailResponse.RoundSummary> roundSummaries = rounds.stream()
                .map(r -> DriveDetailResponse.RoundSummary.builder()
                        .id(r.getId())
                        .roundType(r.getRoundType())
                        .orderNo(r.getOrderNo())
                        .roundDate(DateUtil.formatLocalDate(r.getRoundDate()))
                        .venue(r.getVenue())
                        .durationMins(r.getDurationMins())
                        .cutoff(r.getCutoff())
                        .instructions(r.getInstructions())
                        .build())
                .toList();
        response.setRounds(roundSummaries);

        // Calculate count of eligible students
        long eligibleCount = countEligibleStudents(drive);
        response.setEligibleStudentsCount(eligibleCount);

        return response;
    }

    @Transactional(readOnly = true)
    public List<DriveResponse> getUpcomingDrives() {
        List<Drive> drives = driveRepository.findByDriveDateGreaterThanEqualOrderByDriveDateAsc(LocalDate.now());
        return drives.stream().map(mapper::toDriveResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<DriveResponse> listDrives(String search, DriveStatus status, Pageable pageable) {
        Specification<Drive> spec = SpecificationBuilder.<Drive>create()
                .withSearch(search, "title", "roles", "location", "company.name")
                .withEqual("status", status)
                .build();
        Page<Drive> page = driveRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(mapper::toDriveResponse));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getEligibleStudentsForDrive(Long driveId) {
        Drive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new NotFoundException("Drive", driveId));

        List<Student> allStudents = studentRepository.findAll();
        List<String> eligibleBranches = drive.getEligibleBranchesList();

        List<Student> matchingStudents = allStudents.stream()
                .filter(s -> s.getCgpa() != null && s.getCgpa() >= drive.getEligibilityCgpa())
                .filter(s -> s.getBacklogs() != null && s.getBacklogs() <= drive.getMaxBacklogs())
                .filter(s -> eligibleBranches.isEmpty() || eligibleBranches.stream().anyMatch(b -> b.equalsIgnoreCase(s.getBranch())))
                .toList();

        return matchingStudents.stream().map(studentMapper::toResponse).toList();
    }

    @Transactional
    public void deleteDrive(Long id) {
        Drive drive = driveRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drive", id));
        driveRepository.delete(drive);
    }

    private long countEligibleStudents(Drive drive) {
        List<String> branches = drive.getEligibleBranchesList();
        return studentRepository.findAll().stream()
                .filter(s -> s.getCgpa() != null && s.getCgpa() >= drive.getEligibilityCgpa())
                .filter(s -> s.getBacklogs() != null && s.getBacklogs() <= drive.getMaxBacklogs())
                .filter(s -> branches.isEmpty() || branches.stream().anyMatch(b -> b.equalsIgnoreCase(s.getBranch())))
                .count();
    }
}
