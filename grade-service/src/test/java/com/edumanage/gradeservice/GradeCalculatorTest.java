package com.edumanage.gradeservice;

import com.edumanage.gradeservice.service.GradeCalculator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class GradeCalculatorTest {

    private final GradeCalculator calculator = new GradeCalculator();

    @ParameterizedTest
    @CsvSource({
        "95.0, A+",
        "87.0, A",
        "82.0, A-",
        "76.0, B+",
        "71.0, B",
        "66.0, B-",
        "61.0, C+",
        "56.0, C",
        "51.0, C-",
        "46.0, D",
        "30.0, F"
    })
    void calculateGradeLetter(double percentage, String expectedGrade) {
        assertThat(calculator.calculateGradeLetter(percentage)).isEqualTo(expectedGrade);
    }

    @ParameterizedTest
    @CsvSource({
        "A+, 4.0",
        "A,  4.0",
        "A-, 3.7",
        "B+, 3.3",
        "B,  3.0",
        "F,  0.0"
    })
    void calculateGradePoints(String gradeLetter, double expectedPoints) {
        assertThat(calculator.calculateGradePoints(gradeLetter.trim())).isEqualTo(expectedPoints);
    }
}
