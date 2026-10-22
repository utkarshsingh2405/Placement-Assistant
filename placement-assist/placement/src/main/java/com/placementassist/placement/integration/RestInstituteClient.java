package com.placementassist.placement.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.placementassist.placement.integration.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Primary
public class RestInstituteClient implements InstituteClient {

    private static final Logger log = LoggerFactory.getLogger(RestInstituteClient.class);

    private final RestClient restClient;
    private final StubInstituteClient stubFallback;
    private final String baseUrl;
    private final String apiKey;

    public RestInstituteClient(
            StubInstituteClient stubFallback,
            @Value("${pillar.institute.url:http://localhost:8081}") String baseUrl,
            @Value("${pillar.institute.api-key:inst-sec-key-7f9a12b4e8c345d6}") String apiKey) {
        this.stubFallback = stubFallback;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().build();
    }

    private String getEndpoint(String path) {
        String base = baseUrl;
        if (!base.endsWith("/institute") && !base.contains("/institute/")) {
            base = base + "/institute";
        }
        return base + path;
    }

    @Override
    public Optional<StudentDto> getStudentByRollNo(String rollNo) {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/students?rollNo={rollNo}"), rollNo)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                StudentDto dto = mapper.convertValue(res.get("data"), StudentDto.class);
                return Optional.ofNullable(dto);
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching rollNo {}: {}. Falling back to stub.", rollNo, ex.getMessage());
        }
        return stubFallback.getStudentByRollNo(rollNo);
    }

    @Override
    public Optional<StudentDto> getStudentById(Long id) {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/students/{id}"), id)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                StudentDto dto = mapper.convertValue(res.get("data"), StudentDto.class);
                return Optional.ofNullable(dto);
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching student id {}: {}. Falling back to stub.", id, ex.getMessage());
        }
        return stubFallback.getStudentById(id);
    }

    @Override
    public List<StudentDto> getEligibleStudentsForDrive(Long driveId) {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/drives/{id}/eligible-students"), driveId)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return mapper.convertValue(res.get("data"), new TypeReference<List<StudentDto>>() {});
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching eligible students for drive {}: {}. Falling back to stub.", driveId, ex.getMessage());
        }
        return stubFallback.getEligibleStudentsForDrive(driveId);
    }

    @Override
    public List<DriveMasterDto> getAllDrives() {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/drives"))
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return mapper.convertValue(res.get("data"), new TypeReference<List<DriveMasterDto>>() {});
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching drives: {}. Falling back to stub.", ex.getMessage());
        }
        return stubFallback.getAllDrives();
    }

    @Override
    public List<DriveMasterDto> getUpcomingDrives() {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/drives/upcoming"))
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return mapper.convertValue(res.get("data"), new TypeReference<List<DriveMasterDto>>() {});
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching upcoming drives: {}. Falling back to stub.", ex.getMessage());
        }
        return stubFallback.getUpcomingDrives();
    }

    @Override
    public Optional<DriveMasterDto> getDriveById(Long id) {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/drives/{id}"), id)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                DriveMasterDto dto = mapper.convertValue(res.get("data"), DriveMasterDto.class);
                return Optional.ofNullable(dto);
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching drive id {}: {}. Falling back to stub.", id, ex.getMessage());
        }
        return stubFallback.getDriveById(id);
    }

    @Override
    public List<DriveRoundDto> getDriveRounds(Long driveId) {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/drives/{id}/rounds"), driveId)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return mapper.convertValue(res.get("data"), new TypeReference<List<DriveRoundDto>>() {});
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching rounds for drive {}: {}. Falling back to stub.", driveId, ex.getMessage());
        }
        return stubFallback.getDriveRounds(driveId);
    }

    @Override
    public Optional<ResumeDto> getApprovedResume(Long studentId) {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/students/{id}/resume"), studentId)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                ResumeDto dto = mapper.convertValue(res.get("data"), ResumeDto.class);
                return Optional.ofNullable(dto);
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching resume for student {}: {}. Falling back to stub.", studentId, ex.getMessage());
        }
        return stubFallback.getApprovedResume(studentId);
    }

    @Override
    public List<OpportunityDto> getInternships() {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/internships"))
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return mapper.convertValue(res.get("data"), new TypeReference<List<OpportunityDto>>() {});
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching internships: {}. Falling back to stub.", ex.getMessage());
        }
        return stubFallback.getInternships();
    }

    @Override
    public List<OpportunityDto> getHackathons() {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/hackathons"))
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return mapper.convertValue(res.get("data"), new TypeReference<List<OpportunityDto>>() {});
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching hackathons: {}. Falling back to stub.", ex.getMessage());
        }
        return stubFallback.getHackathons();
    }

    @Override
    public List<OpportunityDto> getOffCampusJobs() {
        try {
            var res = restClient.get()
                    .uri(getEndpoint("/api/integration/offcampus-jobs"))
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (res != null && res.containsKey("data") && res.get("data") != null) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return mapper.convertValue(res.get("data"), new TypeReference<List<OpportunityDto>>() {});
            }
        } catch (Exception ex) {
            log.warn("Institute service error fetching offcampus jobs: {}. Falling back to stub.", ex.getMessage());
        }
        return stubFallback.getOffCampusJobs();
    }
}
