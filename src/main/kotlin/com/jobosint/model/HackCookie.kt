package com.jobosint.model

import com.microsoft.playwright.options.Cookie
import kotlinx.serialization.Serializable

@Serializable
data class HackCookie(
    val Host: String,
    val Path: String,
    val KeyName: String,
    val Value: String,
    val IsSecure: Boolean,
    val IsHTTPOnly: Boolean,
    val HasExpire: Boolean,
    val IsPersistent: Boolean,
    val CreateDate: String,
    val ExpireDate: String,
) {
    fun toPlaywrightCookie(): Cookie {
        return Cookie(this.KeyName, this.Value)
            .setDomain(this.Host)
            .setPath(this.Path)
            .setHttpOnly(this.IsHTTPOnly)
            .setSecure(this.IsSecure)
    }
}
