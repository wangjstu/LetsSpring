package com.wangjstu.controller;

import com.wangjstu.entity.Student;
import com.wangjstu.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/findAll")
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Student findById(@PathVariable("id") Long id) {
        return studentRepository.findById(id);
    }

    @PostMapping("/add")
    public String addStudent(@RequestBody Student student) {
        studentRepository.add(student);
        return "success";
    }

    @DeleteMapping("/del/{id}")
    public String delStudent(@PathVariable("id") Long id) {
        studentRepository.deleteById(id);
        return "success";
    }

    @PostMapping("/update")
    public String updateStudent(@RequestBody Student student) {
        studentRepository.updateStudent(student);
        return "success";
    }
}
