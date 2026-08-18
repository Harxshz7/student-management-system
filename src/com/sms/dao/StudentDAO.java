package com.sms.dao;

import java.util.List;
import java.util.Optional;

import com.sms.model.Student;

public interface StudentDAO {
    void save(Student student);
    List<Student> findAll();
    Optional<Student> findById(int id);
    boolean update(Student student);
    boolean delete(int id);
}
