package com.jobosint.util

import com.jobosint.util.UrlUtils.removeQueryString

object LinkedInUtils {
    fun getJobBoardIdFromUrl(url: String): String {
        val baseUrl: String = removeQueryString(url)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val jobId = urlParts[5]
        // verify jobId is a number
        if (!jobId.matches("\\d+".toRegex())) {
            throw IllegalArgumentException("Invalid jobId: $jobId")
        }
        return jobId
    }

    fun getJobBoardIdFromPath(path: String): String {
        val baseUrl: String = removeQueryString(path)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val jobId = urlParts[3]
        // verify jobId is a number
        if (!jobId.matches("\\d+".toRegex())) {
            throw IllegalArgumentException("Invalid jobId: $jobId")
        }
        return jobId
    }

    // https://www.linkedin.com/company/sleeperhq/about/ -> sleeperhq
    fun getCompanyTokenFromUrl(url: String): String {
        val baseUrl = UrlUtils.removeQueryString(url)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val token = urlParts[4]
        return token
    }
}