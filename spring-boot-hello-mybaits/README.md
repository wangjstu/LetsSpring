1. 创建maven工程，修改pom.xml
    ```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-parent</artifactId>
        <version>2.3.0.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.2</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    ```

2. 创建数据库表
    ```sql
   CREATE DATABASE mytest;
   USE mytest;
   DROP TABLE `student`;
   CREATE TABLE `student` (
     `id` INT(11) NOT NULL AUTO_INCREMENT,
     `name` VARCHAR(20) DEFAULT NULL,
     `score` DOUBLE DEFAULT NULL,
     `birthday` DATE DEFAULT NULL,
     PRIMARY KEY (`id`)
   ) ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4
   
   INSERT INTO student(NAME, score, birthday) VALUES
   ("张三", '90', '2000-08-19'),
   ("段誉", '60', '2003-08-19'),
   ("王四", '90', '2001-08-19');
   
   SELECT * FROM student
    ```

3. 创建实体类
    ```java
    package com.wangjstu.entity;
    
    import lombok.Data;
    
    import java.util.Date;
    
    @Data
    public class Student {
        private Long id;
        private String name;
        private Double score;
        private Date birthday;
    }
    ```

4. 创建Respository(Dao)
    ```java
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
    ```

5. 在目录resources/mapping下创建Respository对应的StudentMapper.xml文件
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.wangjstu.repository.StudentRepository">
        <select id="findAll" resultType="Student">
            select * from student
        </select>
        <select id="findById" parameterType="java.lang.Long" resultType="Student">
            select * from student where id = #{id}
        </select>
        <insert id="add" parameterType="Student">
            INSERT INTO student(NAME, score, birthday) VALUES (#{name}, #{score}, #{birthday})
        </insert>
        <update id="updateStudent" parameterType="Student">
            update student set name = #{name}, score=#{score}, birthday=#{birthday} where id = #{id} limit 1
        </update>
        <delete id="deleteById" parameterType="java.lang.Long">
            delete from student where id=#{id}
        </delete>
    </mapper>
    ```

6. 编写controller请求
    ```java
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
    ```

7. 增加 application.yml。关于mybatis加日志参考[mybatis打开日志](https://www.cnblogs.com/islihy/p/13984844.html)
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/mytest?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
        username: root
        password: phpcj
        driver-class-name: com.mysql.cj.jdbc.Driver
    mybatis:
      mapper-locations: classpath:mapping/*.xml
      type-aliases-package: com.wangjstu.entity
      configuration:
        log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    ```

8. 创建Application
    ```java
    package com.wangjstu;
    
    import org.mybatis.spring.annotation.MapperScan;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    
    @SpringBootApplication
    @MapperScan("com.wangjstu.repository")
    public class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }
    ```
   
9. 测试`student.http`
    ```shell script
    DELETE http://127.0.0.1:8080/student/del/6
    Accept: application/json
    
    ###
    POST http://127.0.0.1:8080/student/update
    Content-Type: application/json
    
    {
      "id": 6,
      "name": "王语嫣",
      "score": 88.0,
      "birthday": "2003-08-18T16:00:00.000+00:00"
    }
    
    ###
    GET http://127.0.0.1:8080/student/findById/3
    Accept: application/json
    
    ###
    POST http://127.0.0.1:8080/student/add
    Content-Type: application/json
    
    {
      "name": "王语嫣",
      "score": 60.0,
      "birthday": "2003-08-18T16:00:00.000+00:00"
    }
    
    ###
    # get
    GET http://127.0.0.1:8080/student/findAll
    
    ```