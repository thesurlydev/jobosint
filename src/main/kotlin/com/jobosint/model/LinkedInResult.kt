package com.jobosint.model

import kotlinx.serialization.Serializable

@Serializable
data class LinkedInResult(val href: String, val text: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinkedInResult) return false
        return href == other.href // Compare only the 'href' property
    }

    override fun hashCode(): Int {
        return href.hashCode()
    }
}
