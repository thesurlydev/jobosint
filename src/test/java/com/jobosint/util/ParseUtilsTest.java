package com.jobosint.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ParseUtilsTest {

    @Test
    public void testParseSalaryRange() {
        String jobDescription = "The salary range for this position is $133,500.00 to $143,500.00.";
        String[] salaryRange = ParseUtils.parseSalaryRange(jobDescription);

        assertNotNull(salaryRange);
        assertEquals("133.5", salaryRange[0]);
        assertEquals("143.5", salaryRange[1]);
    }

    @Test
    public void testParseSalaryRange2() {
        String jobDescription = "The salary range for this position is $170,800 - 239,100";
        String[] salaryRange = ParseUtils.parseSalaryRange(jobDescription);

        assertNotNull(salaryRange);
        assertEquals("170.8", salaryRange[0]);
        assertEquals("239.1", salaryRange[1]);
    }

    @Test
    public void testParseSalaryRange3() {
        String jobDescription = "$204,000 - $313,000 a year\n" +
                "The base salary range for new hires in this role is $235,000 for a level 4 and $270,000 for a level 5 . ";
        String[] salaryRange = ParseUtils.parseSalaryRange(jobDescription);

        assertNotNull(salaryRange);
        assertEquals("204.0", salaryRange[0]);
        assertEquals("313.0", salaryRange[1]);
    }

    @Test
    public void testParseSalaryRangeNone() {
        String jobDescription = "The salary range for this position is.";
        String[] salaryRange = ParseUtils.parseSalaryRange(jobDescription);
        assertEquals(0, salaryRange.length);
    }
}