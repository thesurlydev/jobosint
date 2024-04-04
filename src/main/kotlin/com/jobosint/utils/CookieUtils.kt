package com.jobosint.utils

import com.jobosint.model.HackCookie
import com.microsoft.playwright.options.Cookie
import kotlinx.serialization.json.Json
import java.io.File

fun getAllCookies(): List<Cookie> {
    val fileContent = File("data/cookies/chrome_default_cookie.json").readText()
    val hackCookies: List<HackCookie> = Json.decodeFromString(fileContent)
    val playwrightCookies = convertToPlaywrightCookies(hackCookies)
    return playwrightCookies
}

private fun convertToPlaywrightCookies(hackCookies: List<HackCookie>): List<Cookie> {
    return hackCookies.map { it.toPlaywrightCookie() }
}

fun getCookiesForHost(host: String): List<Cookie> {
    val cookies = getAllCookies()
    return cookies.filter { it.domain.contains(host) }
}