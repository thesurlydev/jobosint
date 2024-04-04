package com.jobosint.model

data class LinkedInJobSearchRequest(val term: String,
                                    val maxResults: Int = 10)
