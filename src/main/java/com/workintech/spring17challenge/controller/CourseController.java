package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.exceptions.ApiException;
import com.workintech.spring17challenge.model.Course;
import com.workintech.spring17challenge.model.CourseGpa;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private List<Course> courses;
    private CourseGpa lowCourseGpa;
    private CourseGpa mediumCourseGpa;
    private CourseGpa highCourseGpa;

    @Autowired
    public CourseController(@Qualifier("lowCourseGpa") CourseGpa lowCourseGpa,
                            @Qualifier("mediumCourseGpa") CourseGpa mediumCourseGpa,
                            @Qualifier("highCourseGpa") CourseGpa highCourseGpa) {
        this.lowCourseGpa = lowCourseGpa;
        this.mediumCourseGpa = mediumCourseGpa;
        this.highCourseGpa = highCourseGpa;
    }

    @PostConstruct
    public void init() {
        courses = new ArrayList<>();
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courses;
    }

    @GetMapping("/{name}")
    public Course getCourseByName(@PathVariable String name) {
        return courses.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElseThrow(() ->new ApiException("Course name: " + name + "not found ", HttpStatus.NOT_FOUND));
    }

    private double calculateTotalGpa(Course course) {
        int credit = course.getCredit();
        int coefficient = course.getGrade().getCoefficient();

        if (credit <= 2) {
            return coefficient * credit * lowCourseGpa.getGpa();
        } else if (credit == 3) {
            return coefficient * credit * mediumCourseGpa.getGpa();
        } else if (credit == 4) {
            return coefficient * credit * highCourseGpa.getGpa();
        }
        return 0.0;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object addCourse(@RequestBody Course course) {
        if (course.getCredit() == null || course.getCredit() < 0 || course.getCredit() > 4) {
            throw new ApiException("Credit must be between 0 and 4", HttpStatus.BAD_REQUEST);
        }
        if (course.getName() != null) {
            courses.removeIf(c -> c.getName() != null && c.getName().equalsIgnoreCase(course.getName()));
        }
        courses.add(course);
        double totalGpa = calculateTotalGpa(course);

        Map<String, Object> response = new HashMap<>();
        response.put("course", course);
        response.put("totalGpa", totalGpa);
        return response;
    }

    @DeleteMapping("/{id}")
    public Course deleteCourse(@PathVariable int id) {
        Course courseToDelete = courses.stream().filter(c -> c.getId() == id).findFirst().orElseThrow(() -> new ApiException("Course " + id, HttpStatus.NOT_FOUND));
        courses.remove(courseToDelete);
        return courseToDelete;
    }

    @PutMapping("/{id}")
    public Object updateCourse(@PathVariable int id, @RequestBody Course updatedCourse) {
        if (updatedCourse.getCredit() == null || updatedCourse.getCredit() < 0 || updatedCourse.getCredit() > 4) {
            throw new ApiException("Credit must be between 0 and 4", HttpStatus.BAD_REQUEST);
        }
        Course existingCourse = courses.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ApiException("Course not found with id: " + id, HttpStatus.NOT_FOUND));

        existingCourse.setName(updatedCourse.getName());
        existingCourse.setCredit(updatedCourse.getCredit());
        existingCourse.setGrade(updatedCourse.getGrade());

        double totalGpa = calculateTotalGpa(existingCourse);

        Map<String, Object> response = new HashMap<>();
        response.put("course", existingCourse);
        response.put("totalGpay", totalGpa);
        return response;
    }
}
