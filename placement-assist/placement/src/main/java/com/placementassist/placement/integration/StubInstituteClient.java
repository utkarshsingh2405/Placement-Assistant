package com.placementassist.placement.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.placementassist.placement.integration.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("stubInstituteClient")
public class StubInstituteClient implements InstituteClient {

    private static final Logger log = LoggerFactory.getLogger(StubInstituteClient.class);
    private final ObjectMapper objectMapper;
    private final List<StudentDto> stubStudents = new ArrayList<>();
    private final List<DriveMasterDto> stubDrives = new ArrayList<>();
    private final List<OpportunityDto> stubOpportunities = new ArrayList<>();

    public StubInstituteClient() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        loadStubData();
    }

    private void loadStubData() {
        try {
            ClassPathResource studentRes = new ClassPathResource("stub-data/students.json");
            if (studentRes.exists()) {
                try (InputStream is = studentRes.getInputStream()) {
                    List<StudentDto> list = objectMapper.readValue(is, new TypeReference<>() {});
                    stubStudents.addAll(list);
                }
            }

            ClassPathResource drivesRes = new ClassPathResource("stub-data/drives.json");
            if (drivesRes.exists()) {
                try (InputStream is = drivesRes.getInputStream()) {
                    List<DriveMasterDto> list = objectMapper.readValue(is, new TypeReference<>() {});
                    stubDrives.addAll(list);
                }
            }

            ClassPathResource oppRes = new ClassPathResource("stub-data/opportunities.json");
            if (oppRes.exists()) {
                try (InputStream is = oppRes.getInputStream()) {
                    List<OpportunityDto> list = objectMapper.readValue(is, new TypeReference<>() {});
                    stubOpportunities.addAll(list);
                }
            }
            log.info("Loaded stub Institute data: {} students, {} drives, {} opportunities",
                    stubStudents.size(), stubDrives.size(), stubOpportunities.size());
        } catch (Exception ex) {
            log.warn("Could not load stub data files: {}", ex.getMessage());
        }
    }

    @Override
    public Optional<StudentDto> getStudentByRollNo(String rollNo) {
        return stubStudents.stream()
                .filter(s -> s.getRollNo().equalsIgnoreCase(rollNo))
                .findFirst();
    }

    @Override
    public Optional<StudentDto> getStudentById(Long id) {
        return stubStudents.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<StudentDto> getEligibleStudentsForDrive(Long driveId) {
        return new ArrayList<>(stubStudents);
    }

    @Override
    public List<DriveMasterDto> getAllDrives() {
        return new ArrayList<>(stubDrives);
    }

    @Override
    public List<DriveMasterDto> getUpcomingDrives() {
        return new ArrayList<>(stubDrives);
    }

    @Override
    public Optional<DriveMasterDto> getDriveById(Long id) {
        return stubDrives.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<DriveRoundDto> getDriveRounds(Long driveId) {
        return List.of(
                DriveRoundDto.builder().id(1L).driveId(driveId).orderNo(1).name("Online Assessment").roundType("ONLINE_TEST").build(),
                DriveRoundDto.builder().id(2L).driveId(driveId).orderNo(2).name("Technical Interview 1").roundType("TECHNICAL_INTERVIEW_1").build()
        );
    }

    @Override
    public Optional<ResumeDto> getApprovedResume(Long studentId) {
        return Optional.of(ResumeDto.builder()
                .studentId(studentId)
                .rollNo("21CS001")
                .fileUrl("/uploads/resumes/resume.pdf")
                .atsScore(88)
                .status("APPROVED")
                .version(1)
                .build());
    }

    @Override
    public List<OpportunityDto> getInternships() {
        return stubOpportunities.stream().filter(o -> "INTERNSHIP".equals(o.getType())).toList();
    }

    @Override
    public List<OpportunityDto> getHackathons() {
        return stubOpportunities.stream().filter(o -> "HACKATHON".equals(o.getType())).toList();
    }

    @Override
    public List<OpportunityDto> getOffCampusJobs() {
        return stubOpportunities.stream().filter(o -> "OFFCAMPUS_JOB".equals(o.getType())).toList();
    }
}
