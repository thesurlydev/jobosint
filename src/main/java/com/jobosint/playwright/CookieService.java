package com.jobosint.playwright;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CookieService {

    /**
     * Loads cookies from a JSON file and converts them to Playwright cookie format
     * 
     * @param resourcePath Path to the JSON file containing cookies
     * @return List of Playwright cookies
     * @throws IOException If there's an error reading the file
     */
    public List<Cookie> loadCookiesFromResource(String resourcePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> cookiesData = mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
            
            return convertToPlaywrightCookies(cookiesData);
        }
    }
    
    /**
     * Loads cookies from a file path and converts them to Playwright cookie format
     * 
     * @param filePath Path to the JSON file containing cookies
     * @return List of Playwright cookies
     * @throws IOException If there's an error reading the file
     */
    public List<Cookie> loadCookiesFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (InputStream is = Files.newInputStream(path)) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> cookiesData = mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
            
            return convertToPlaywrightCookies(cookiesData);
        }
    }
    
    /**
     * Loads LinkedIn cookies specifically from the resources directory
     * 
     * @return List of Playwright cookies for LinkedIn
     * @throws IOException If there's an error reading the file
     */
    public List<Cookie> loadLinkedInCookies() throws IOException {
        return loadCookiesFromResource("cookies/linkedin.json");
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
}
