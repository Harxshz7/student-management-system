package com.sms.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.sms.dao.StudentDAO;
import com.sms.model.Student;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public void addStudent(Student student) {
        validateStudent(student);
        studentDAO.save(student);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public Optional<Student> findById(int id) {
        return studentDAO.findById(id);
    }

    public boolean updateStudent(int id, Student updatedStudent) {
        validateStudent(updatedStudent);
        Optional<Student> existing = studentDAO.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        updatedStudent.setId(id);
        return studentDAO.update(updatedStudent);
    }

    public boolean deleteStudent(int id) {
        return studentDAO.delete(id);
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> allStudents = studentDAO.findAll();
        List<Student> matches = new ArrayList<>();
        String search = keyword.toLowerCase();
        for (Student student : allStudents) {
            if (student.getName().toLowerCase().contains(search)
                    || student.getCourse().toLowerCase().contains(search)) {
                matches.add(student);
            }
        }
        return matches;
    }

    public List<Student> sortByName() {
        List<Student> sorted = new ArrayList<>(studentDAO.findAll());
        sorted.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public List<Student> sortByAge() {
        List<Student> sorted = new ArrayList<>(studentDAO.findAll());
        sorted.sort(Comparator.comparingInt(Student::getAge));
        return sorted;
    }

    public int getTotalCount() {
        return studentDAO.findAll().size();
    }

    public double getAverageAge() {
        List<Student> all = studentDAO.findAll();
        if (all.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Student student : all) {
            sum += student.getAge();
        }
        return (double) sum / all.size();
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
