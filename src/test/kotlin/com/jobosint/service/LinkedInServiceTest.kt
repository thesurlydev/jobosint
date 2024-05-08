package com.jobosint.service

import com.jobosint.model.LinkedInJobSearchRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class LinkedInServiceTest {

    @Autowired lateinit var linkedInService: LinkedInService

    @Test
    fun updateJobBoardIds() {
        linkedInService.updateJobBoardIds()
    }

    @Test
    fun getJobBoardIdFromUrl() {
        val url = "https://www.linkedin.com/jobs/view/3902691303"
        val actual = linkedInService.getJobBoardIdFromUrl(url)
        assertEquals("3902691303", actual)
    }

    @Test
    fun getJobBoardIdFromUrlBad() {
        val url = "https://www.linkedin.com/jobs/view/about"
        // should throw an exception
        val exception = assertThrows<IllegalArgumentException>("Should throw an Exception") {
            linkedInService.getJobBoardIdFromUrl(url)
        }
        exception.message?.let { assertTrue(it.startsWith("Invalid jobId")) }
    }

    @Test
    fun jobStillAcceptingApplications() {
        val jobId = "3902691303"
        val acceptingApps = linkedInService.jobStillAcceptingApplications(jobId)
        assertFalse(acceptingApps)
    }

    @Test
    fun jobStillAcceptingApplicationsTrue() {
        val jobId = "3912145792"
        val acceptingApps = linkedInService.jobStillAcceptingApplications(jobId)
        assertTrue(acceptingApps)
    }

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
        assertEquals("Arcadia", company.name)
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