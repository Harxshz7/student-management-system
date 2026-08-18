package com.sms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sms.model.Student;

public class StudentService {
    private final List<Student> students = new ArrayList<>();
    private int nextId = 1;

    public void addStudent(Student student) {
        validateStudent(student);
        student.setId(nextId++);
        students.add(student);
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public Optional<Student> findById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return Optional.of(student);
            }
        }
        return Optional.empty();
    }

    public boolean updateStudent(int id, Student updatedStudent) {
        validateStudent(updatedStudent);
        for (int i = 0; i < students.size(); i++) {
            Student current = students.get(i);
            if (current.getId() == id) {
                updatedStudent.setId(id);
                students.set(i, updatedStudent);
                return true;
            }
        }
        return false;
    }

    public boolean deleteStudent(int id) {
        return students.removeIf(student -> student.getId() == id);
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> matches = new ArrayList<>();
        String search = keyword.toLowerCase();
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(search)
                    || student.getCourse().toLowerCase().contains(search)) {
                matches.add(student);
            }
        }
        return matches;
    }

    private void validateStudent(Student student) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name must be non-empty");
        }
        if (student.getName().length() < 2 || student.getName().length() > 50) {
            throw new IllegalArgumentException("Name must be between 2 and 50 characters");
        }
        if (student.getAge() < 5 || student.getAge() > 100) {
            throw new IllegalArgumentException("Age must be between 5 and 100");
        }
        if (student.getCourse() == null || student.getCourse().trim().isEmpty()) {
            throw new IllegalArgumentException("Course must be non-empty");
        }
    }
}
