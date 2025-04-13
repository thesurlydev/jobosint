package com.jobosint.playwright;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobosint.config.ScrapeConfig;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CookieService {

    private final ScrapeConfig config;

    /**
     * Loads cookies from a file path and converts them to Playwright cookie format
     *
     * @param filePath Path to the JSON file containing cookies
     * @return List of Playwright cookies
     * @throws IOException If there's an error reading the file
     */
    public List<Cookie> loadPlaywrightCookiesFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (InputStream is = Files.newInputStream(path)) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> cookiesData = mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {
            });

            return convertToPlaywrightCookies(cookiesData);
        }
    }

    public List<Map<String, String>> loadLinkedInCookies() throws IOException {
        return loadCookiesFromFile("linkedin.json");
    }


    public List<Map<String, String>> loadCookiesFromFile(String filePath) throws IOException {
        Path path = Paths.get(config.cookieAuthDir(), filePath);
        try (InputStream is = Files.newInputStream(path)) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(is, new TypeReference<List<Map<String, String>>>() {
            });
        }
    }

    /**
     * Loads cookies from the auth directory and converts them to Playwright cookie format
     *
     * @param filename Name of the cookie file in the auth directory
     * @return List of Playwright cookies
     * @throws IOException If there's an error reading the file
     */
    public List<Cookie> loadPlaywrightCookiesFromAuthDirectory(String filename) throws IOException {
        Path path = Paths.get(config.cookieAuthDir(), filename);
        if (!Files.exists(path)) {
            throw new IOException("Cookie file not found: " + path);
        }
        return loadPlaywrightCookiesFromFile(path.toString());
    }

    /**
     * Loads cookie string directly from a file in the auth directory
     *
     * @param filename Name of the text file containing cookie string in the auth directory
     * @return Raw cookie string from the file
     * @throws IOException If there's an error reading the file
     */
    public String loadRawCookieStringFromAuthDirectory(String filename) throws IOException {
        Path path = Paths.get(config.cookieAuthDir(), filename);
        if (!Files.exists(path)) {
            throw new IOException("Cookie file not found: " + path);
        }
        return Files.readString(path).trim();
    }

    /**
     * Loads LinkedIn cookies from the auth directory
     *
     * @return List of Playwright cookies for LinkedIn
     * @throws IOException If there's an error reading the file
     */
    public List<Cookie> loadLinkedInPlaywrightCookies() throws IOException {
        return loadPlaywrightCookiesFromAuthDirectory("linkedin.json");
    }

    /**
     * Loads LinkedIn raw cookie string from the auth directory
     *
     * @return Raw cookie string for LinkedIn
     * @throws IOException If there's an error reading the file
     */
    public String loadLinkedInRawCookieString() throws IOException {
        return loadRawCookieStringFromAuthDirectory("linkedin.txt");
    }

    /**
     * Converts generic cookie data to Playwright cookie format
     *
     * @param cookiesData List of cookie data maps
     * @return List of Playwright cookies
     */
    private List<Cookie> convertToPlaywrightCookies(List<Map<String, Object>> cookiesData) {
        List<Cookie> playwrightCookies = new ArrayList<>();

        for (Map<String, Object> cookieData : cookiesData) {
            String name = (String) cookieData.get("name");
            String value = (String) cookieData.get("value");
            String domain = (String) cookieData.get("domain");
            String path = (String) cookieData.get("path");

            // Create cookie with required fields
            Cookie cookie = new Cookie(name, value);
            cookie.domain = domain;
            cookie.path = path;

            // Set optional fields
            if (cookieData.containsKey("expirationDate")) {
                Number expiry = (Number) cookieData.get("expirationDate");
                cookie.expires = expiry.doubleValue();
            }

            if (cookieData.containsKey("httpOnly")) {
                cookie.httpOnly = (Boolean) cookieData.get("httpOnly");
            }

            if (cookieData.containsKey("secure")) {
                cookie.secure = (Boolean) cookieData.get("secure");
            }

            if (cookieData.containsKey("sameSite")) {
                String sameSite = (String) cookieData.get("sameSite");
                if (sameSite != null) {
                    switch (sameSite.toLowerCase()) {
                        case "lax":
                            cookie.sameSite = SameSiteAttribute.LAX;
                            break;
                        case "strict":
                            cookie.sameSite = SameSiteAttribute.STRICT;
                            break;
                        case "none":
                        case "no_restriction":
                            cookie.sameSite = SameSiteAttribute.NONE;
                            break;
                    }
                }
            }

            playwrightCookies.add(cookie);
        }

        return playwrightCookies;
    }

    /**
     * Maps a list of cookie maps to Playwright cookies
     *
     * @param cookieMaps List of maps containing cookie data
     * @return List of Playwright cookies
     */
    public List<Cookie> mapCookiesToPlaywright(List<Map<String, String>> cookieMaps) {
        List<Cookie> playwrightCookies = new ArrayList<>();

        for (Map<String, String> cookieMap : cookieMaps) {
            if (cookieMap.containsKey("name") && cookieMap.containsKey("value")) {
                Cookie cookie = new Cookie(cookieMap.get("name"), cookieMap.get("value"));

                // Set required domain and path properties
                if (cookieMap.containsKey("domain")) {
                    cookie.domain = cookieMap.get("domain");
                }

                if (cookieMap.containsKey("path")) {
                    cookie.path = cookieMap.get("path");
                }

                // Set optional properties
                if (cookieMap.containsKey("expirationDate")) {
                    try {
                        cookie.expires = Double.parseDouble(cookieMap.get("expirationDate"));
                    } catch (NumberFormatException e) {
                        // Skip setting expires if invalid
                    }
                }

                if (cookieMap.containsKey("httpOnly")) {
                    cookie.httpOnly = Boolean.parseBoolean(cookieMap.get("httpOnly"));
                }

                if (cookieMap.containsKey("secure")) {
                    cookie.secure = Boolean.parseBoolean(cookieMap.get("secure"));
                }

                if (cookieMap.containsKey("sameSite")) {
                    String sameSite = cookieMap.get("sameSite");
                    if ("lax".equalsIgnoreCase(sameSite)) {
                        cookie.sameSite = SameSiteAttribute.LAX;
                    } else if ("strict".equalsIgnoreCase(sameSite)) {
                        cookie.sameSite = SameSiteAttribute.STRICT;
                    } else if ("none".equalsIgnoreCase(sameSite) || "no_restriction".equalsIgnoreCase(sameSite)) {
                        cookie.sameSite = SameSiteAttribute.NONE;
                    }
                }

                playwrightCookies.add(cookie);
            }
        }

        return playwrightCookies;
    }
}
