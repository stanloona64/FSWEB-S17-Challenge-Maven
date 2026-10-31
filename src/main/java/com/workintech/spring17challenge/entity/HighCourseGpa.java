package com.workintech.spring17challenge.entity;

import com.workintech.spring17challenge.model.CourseGpa;
import org.springframework.stereotype.Component;

@Component
public class HighCourseGpa implements CourseGpa {
    @Override
    public int getGpa() {
        return 10;
    }
}
