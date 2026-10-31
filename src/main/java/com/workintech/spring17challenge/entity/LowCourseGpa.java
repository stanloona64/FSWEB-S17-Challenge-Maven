package com.workintech.spring17challenge.entity;

import com.workintech.spring17challenge.model.CourseGpa;
import org.springframework.stereotype.Component;

@Component
public class LowCourseGpa implements CourseGpa {
    @Override
    public int getGpa() {
        return 3;
    }
}
