package com.jobosint.util;

import com.jobosint.model.SalaryRange;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ParseUtilsTest {

    @Test
    public void testParseSalaryRange() {
        String jobDescription = "The salary range for this position is $133,500.00 to $143,500.00.";
        SalaryRange salaryRange = ParseUtils.parseSalaryRange(jobDescription);

        assertNotNull(salaryRange);
        assertEquals("133.5", salaryRange.min());
        assertEquals("143.5", salaryRange.max());
    }

    @Test
    public void testParseSalaryRange2() {
        String jobDescription = "The salary range for this position is $170,800 - 239,100";
        SalaryRange salaryRange = ParseUtils.parseSalaryRange(jobDescription);

        assertNotNull(salaryRange);
        assertEquals("170.8", salaryRange.min());
        assertEquals("239.1", salaryRange.max());
    }

    @Test
    public void testParseSalaryRange3() {
        String jobDescription = "$204,000 - $313,000 a year\n" +
                "The base salary range for new hires in this role is $235,000 for a level 4 and $270,000 for a level 5 . ";
        SalaryRange salaryRange = ParseUtils.parseSalaryRange(jobDescription);

        assertNotNull(salaryRange);
        assertEquals("204.0", salaryRange.min());
        assertEquals("313.0", salaryRange.max());
    }

    @Test
    public void testParseSalaryRangeNone() {
        String jobDescription = "The salary range for this position is.";
        SalaryRange salaryRange = ParseUtils.parseSalaryRange(jobDescription);
        assertNull(salaryRange.min());
        assertNull(salaryRange.max());
    }
}
