package com.jobosint.service

import com.jobosint.model.LinkedInJobSearchRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class LinkedInServiceTest {

    @Autowired lateinit var linkedInService: LinkedInService

    @Test
    fun testSearchJobs() {
        val request = LinkedInJobSearchRequest("java")
        val results = linkedInService.searchJobs(request)
        assert(results.isNotEmpty())
        assertEquals(10, results.size)
    }
}