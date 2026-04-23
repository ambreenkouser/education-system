package com.edumanage.studentservice.service;

import com.edumanage.studentservice.dto.StudentRequest;
import com.edumanage.studentservice.dto.StudentResponse;
import com.edumanage.studentservice.exception.ResourceNotFoundException;
import com.edumanage.studentservice.mapper.StudentMapper;
import com.edumanage.studentservice.model.Student;
import com.edumanage.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("Student record already exists for userId: " + request.getUserId());
        }
        String studentCode = generateStudentCode();
        Student student = Student.builder()
                .userId(request.getUserId())
                .studentCode(studentCode)
                .gradeLevel(request.getGradeLevel())
                .dateOfBirth(request.getDateOfBirth())
                .parentId(request.getParentId())
                .build();
        return studentMapper.toResponse(studentRepository.save(student));
    }

    public StudentResponse findById(UUID id) {
        return studentMapper.toResponse(getStudentOrThrow(id));
    }

    public StudentResponse findByUserId(UUID userId) {
        return studentMapper.toResponse(
                studentRepository.findByUserId(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found for userId: " + userId)));
    }

    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    public List<StudentResponse> findByGradeLevel(String gradeLevel) {
        return studentRepository.findByGradeLevel(gradeLevel).stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    private Student getStudentOrThrow(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private String generateStudentCode() {
        String code;
        do {
            code = "STU-" + String.format("%06d", (int)(Math.random() * 1_000_000));
        } while (studentRepository.existsByStudentCode(code));
        return code;
    }
}
