package com.jobosint.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GoogleJobService {

    public void getJobs(String q) {
        Map<String, String> parameter = new HashMap<>();

        parameter.put("api_key", "49ff9f12c38520b2f0229505659f337afb2c912d888355c3674207e82d5a1808");
        parameter.put("engine", "google_jobs");
        parameter.put("ltype", "1");
        parameter.put("q", "rust jobs");
        parameter.put("google_domain", "google.com");
        parameter.put("chips", "date_posted:week");
        parameter.put("gl", "us");
        parameter.put("hl", "en");


        /*GoogleSearch search = new GoogleSearch(parameter);

        try
        {
            JsonObject results = search.getJson();
        }
        catch (SerpApiClientException ex)
        {
            Console.WriteLine("Exception:");
            Console.WriteLine(ex.ToString());
        }*/
    }
}
