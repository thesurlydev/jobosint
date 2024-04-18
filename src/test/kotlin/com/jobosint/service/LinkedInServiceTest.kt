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
    fun getCompanyTokenFromUrl() {
        val url = "https://www.linkedin.com/company/sleeperhq/about/"
        val actual = linkedInService.getCompanyTokenFromUrl(url)
        assertEquals("sleeperhq", actual)
    }

    @Test
    fun getCompanyFromToken() {
        val companyTag = "arcadiahq"
        val company = linkedInService.scrapeCompany(companyTag)
        println(company)
        assertEquals("Archadia", company.name)
        assertEquals(companyTag, company.linkedinToken)
    }

    @Test
    fun testSearchJobs() {
        val request = LinkedInJobSearchRequest("java")
        val results = linkedInService.searchJobs(request)
        assert(results.isNotEmpty())
        assertEquals(10, results.size)
    }
}