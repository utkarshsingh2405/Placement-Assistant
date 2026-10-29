package com.placementassist.institute.modules.student;

import com.placementassist.institute.modules.auth.User;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.student.dto.CreateStudentRequest;
import com.placementassist.institute.modules.student.dto.StudentResponse;
import com.placementassist.institute.modules.student.dto.UpdateStudentRequest;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.exception.ApiException;
import com.placementassist.shared.exception.ConflictException;
import com.placementassist.shared.exception.NotFoundException;
import com.placementassist.shared.security.Role;
import com.placementassist.shared.security.SecurityUtils;
import com.placementassist.shared.util.CsvUtil;
import com.placementassist.shared.util.SpecificationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service managing student profile lifecycle, eligibility calculation, and search queries.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;
    private final double minCgpa;
    private final int maxBacklogs;

    public StudentService(
            StudentRepository studentRepository,
            UserRepository userRepository,
            StudentMapper studentMapper,
            PasswordEncoder passwordEncoder,
            @Value("${institute.eligibility.min-cgpa:6.0}") double minCgpa,
            @Value("${institute.eligibility.max-backlogs:0}") int maxBacklogs) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.studentMapper = studentMapper;
        this.passwordEncoder = passwordEncoder;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
    }

    /**
     * Creates a student record along with user account in one atomic transaction.
     *
     * @param request Student creation request
     * @return Created student profile response
     */
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        String rollNo = request.getRollNo().toUpperCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User", "email", email);
        }
        if (studentRepository.existsByRollNo(rollNo)) {
            throw new ConflictException("Student", "rollNo", rollNo);
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .build();
        user = userRepository.save(user);

        Student student = studentMapper.toEntity(request);
        student.setUser(user);
        student.setEmail(email);
        student.setRollNo(rollNo);
        student.setPlacementEligible(computePlacementEligibility(request.getCgpa(), request.getBacklogs()));

        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    /**
     * Updates an existing student record. Students may only update their own record; TPO/ADMIN may update any.
     *
     * @param id Student ID
     * @param request Update request payload
     * @return Updated student response
     */
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student", id));

        validateOwnershipOrStaff(student);

        studentMapper.updateEntityFromDto(request, student);

        // Recompute placement eligibility if academic metrics changed
        student.setPlacementEligible(computePlacementEligibility(student.getCgpa(), student.getBacklogs()));

        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    /**
     * Retrieves a student by database ID.
     *
     * @param id Student ID
     * @return StudentResponse
     */
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student", id));
        validateOwnershipOrStaff(student);
        return studentMapper.toResponse(student);
    }

    /**
     * Retrieves student profile for the currently logged-in student.
     *
     * @return Current student response
     */
    @Transactional(readOnly = true)
    public StudentResponse getCurrentStudentProfile() {
        String email = SecurityUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "No active session"));

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Student profile not found for email: " + email));
        return studentMapper.toResponse(student);
    }

    /**
     * Updates student profile for the currently logged-in student.
     *
     * @param request Update request
     * @return Updated student response
     */
    @Transactional
    public StudentResponse updateCurrentStudentProfile(UpdateStudentRequest request) {
        String email = SecurityUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "No active session"));

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Student profile not found for email: " + email));

        studentMapper.updateEntityFromDto(request, student);
        student.setPlacementEligible(computePlacementEligibility(student.getCgpa(), student.getBacklogs()));
        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    /**
     * Searches and filters students with pagination.
     * Non-staff students can only view their own record.
     *
     * @param search Free text keyword across name, rollNo, email, and branch
     * @param branch Branch filter
     * @param year Passing year filter
     * @param minCgpa Minimum CGPA filter
     * @param eligible Eligibility boolean filter
     * @param pageable Pagination and sorting specifications
     * @return PageResponse of student records
     */
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> listStudents(
            String search,
            String branch,
            Integer year,
            Double minCgpa,
            Boolean eligible,
            Pageable pageable) {

        // Students can only see their own profile, not other students
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTpo() && !SecurityUtils.isDirector()) {
            String currentEmail = SecurityUtils.getCurrentUserEmail().orElse("");
            Student self = studentRepository.findByEmail(currentEmail).orElse(null);
            if (self == null) {
                return PageResponse.from(org.springframework.data.domain.Page.empty(pageable));
            }
            return PageResponse.from(new org.springframework.data.domain.PageImpl<>(
                    List.of(studentMapper.toResponse(self)), pageable, 1
            ));
        }

        Specification<Student> spec = SpecificationBuilder.<Student>create()
                .withSearch(search, "name", "rollNo", "email", "branch")
                .withEqualIgnoreCase("branch", branch)
                .withEqual("year", year)
                .withGreaterThanOrEqualTo("cgpa", minCgpa)
                .withEqual("placementEligible", eligible)
                .build();

        Page<Student> page = studentRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(studentMapper::toResponse));
    }

    /**
     * Exports matching students list as a CSV byte array.
     *
     * @param search Free text keyword
     * @param branch Branch filter
     * @param year Year filter
     * @param eligible Eligibility filter
     * @return CSV file byte array
     */
    @Transactional(readOnly = true)
    public byte[] exportStudentsCsv(String search, String branch, Integer year, Boolean eligible) {
        Specification<Student> spec = SpecificationBuilder.<Student>create()
                .withSearch(search, "name", "rollNo", "email", "branch")
                .withEqualIgnoreCase("branch", branch)
                .withEqual("year", year)
                .withEqual("placementEligible", eligible)
                .build();

        List<Student> students = studentRepository.findAll(spec);
        String[] headers = {
                "ID", "Roll Number", "Name", "Email", "Phone", "Branch", "Year", "CGPA", "Backlogs", "Eligible", "Skills"
        };

        List<String[]> rows = new ArrayList<>();
        for (Student s : students) {
            rows.add(new String[]{
                    String.valueOf(s.getId()),
                    s.getRollNo(),
                    s.getName(),
                    s.getEmail(),
                    s.getPhone() != null ? s.getPhone() : "",
                    s.getBranch(),
                    String.valueOf(s.getYear()),
                    String.format("%.2f", s.getCgpa()),
                    String.valueOf(s.getBacklogs()),
                    Boolean.TRUE.equals(s.getPlacementEligible()) ? "YES" : "NO",
                    String.join(", ", s.getSkills())
            });
        }

        return CsvUtil.writeCsv(headers, rows);
    }

    /**
     * Deletes a student and their associated user account. Only ADMIN can perform this action.
     *
     * @param id Student ID
     */
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student", id));

        User user = student.getUser();
        studentRepository.delete(student);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    /**
     * Validates whether the current principal is the student owner or a staff member (TPO/ADMIN).
     */
    private void validateOwnershipOrStaff(Student student) {
        if (SecurityUtils.isAdmin() || SecurityUtils.isTpo() || SecurityUtils.isDirector()) {
            return;
        }
        String currentEmail = SecurityUtils.getCurrentUserEmail().orElse("");
        if (!student.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "You can only view or modify your own student profile");
        }
    }

    private boolean computePlacementEligibility(Double cgpa, Integer backlogs) {
        if (cgpa == null || backlogs == null) return false;
        return cgpa >= minCgpa && backlogs <= maxBacklogs;
    }
}
