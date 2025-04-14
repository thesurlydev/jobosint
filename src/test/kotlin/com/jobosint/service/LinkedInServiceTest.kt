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

    /*@Test
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
    }*/

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

    /*@Test
    fun getCompanyTokenFromUrl() {
        val url = "https://www.linkedin.com/company/sleeperhq/about/"
        val actual = linkedInService.getCompanyTokenFromUrl(url)
        assertEquals("sleeperhq", actual)
    }*/
/*
    @Test
    fun getCompanyFromToken() {
        val companyTag = "arcadiahq"
        val company = linkedInService.scrapeCompany(companyTag)
        println(company)
        assertEquals("Arcadia", company?.name)
        assertEquals(companyTag, company?.linkedinToken)
    }*/

    @Test
    fun testSearchJobs() {
        val request = LinkedInJobSearchRequest("java", 10)
        val results = linkedInService.searchJobs(request)
        assert(results.isNotEmpty())
        assertEquals(10, results.size)
    }

    @Test
    fun getQueryIdFromJobId() {
        val jobId = "3902691303"
        val queryId = linkedInService.getQueryIdFromJobId(jobId)
        assertEquals("c07b0d44515bceba51a9b73c01b0cecb", queryId)
    }
/*
    @Test
    fun getJobBoardIdFromPath() {
        val jobId = linkedInService.getJobBoardIdFromPath("/jobs/view/4206793248/?eBP=CwEAAAGWLzX6dpBuJwQnX078RT1H9vE_RSM-edpkhoXceRCnE-yd9uSHINr-xMA2XwyJt-d9PLMfBLiDBxf9Bj_yXRsn7Bv-qRv7gZl50HV5XBRCqyYypks2YYjHBlFHRcBrLGni-odlqLrBbAGObskMgnTAuk7Sb8eZBdQAeizLUtBlJ1UD5CjGODxJeV9rojqxaUG2KbAeiFoogLxjoLgAjHmTcGvkvvfE6b7lcaYtn2XpXzXWpbBflqZwpHQmaLHQSgcv90PfKnift6lsxhY-0WOtSMvixMSHb_QM4zqN2H9F5dh8JHRs4OXc3nwd7OR3mFK-NAuDHqB-B010WZnt1jGtTAf3JCTWLGjiSJ3J2_8ZQk9LuuswUCNFPCVgs9fC7bFt7iD-qJhKRQdw-t4TxZi_anWFFfbVXC6NjN0H7V4EmwbrP6_-MbkeRv3ezXBsSiJc_7FjL3Vn8yc2XInVyYVBb5ubyJ15Vgd45TlzJMU8eKEYEy3LEkaeYFQlvOcQew&refId=b%2FoXkB5C157NM08fejUh%2Bg%3D%3D&trackingId=rGmrOiwFO0ySTvs1VF3t1w%3D%3D&trk=flagship3_search_srp_jobs")
        assertEquals("4206793248", jobId)
    }*/
}