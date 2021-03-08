package com.wangjstu.repository;

import com.wangjstu.entity.Student;

import java.util.List;


public interface StudentRepository {
    public List<Student> findAll();
    public Student findById(Long id);
    public void add(Student student);
    public void deleteById(Long id);
    public void updateStudent(Student student);
}
