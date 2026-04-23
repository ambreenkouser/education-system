package com.edumanage.gradeservice.service;

import org.springframework.stereotype.Component;

@Component
public class GradeCalculator {

    public String calculateGradeLetter(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 85) return "A";
        if (percentage >= 80) return "A-";
        if (percentage >= 75) return "B+";
        if (percentage >= 70) return "B";
        if (percentage >= 65) return "B-";
        if (percentage >= 60) return "C+";
        if (percentage >= 55) return "C";
        if (percentage >= 50) return "C-";
        if (percentage >= 45) return "D";
        return "F";
    }

    public double calculateGradePoints(String gradeLetter) {
        return switch (gradeLetter) {
            case "A+"     -> 4.0;
            case "A"      -> 4.0;
            case "A-"     -> 3.7;
            case "B+"     -> 3.3;
            case "B"      -> 3.0;
            case "B-"     -> 2.7;
            case "C+"     -> 2.3;
            case "C"      -> 2.0;
            case "C-"     -> 1.7;
            case "D"      -> 1.0;
            default       -> 0.0;
        };
    }
}
