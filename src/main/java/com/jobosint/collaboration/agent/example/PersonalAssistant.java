package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.annotation.Tool;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;

@AgentMeta(
        goal = "Provide assistance to an individual in their personal life",
        tools = {"MyAge", "MyLinkedInProfile"}
)
public class PersonalAssistant extends Agent {
    @Tool(name = "MyAge", description = "Get my age in years")
    public Integer getAge() {
        String specificDateString = "1974-02-05";
        LocalDate specificDate = LocalDate.parse(specificDateString);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(specificDate, currentDate);
        return period.getYears();
    }

    @Tool(name = "MyLinkedInProfile", description = "Get my LinkedIn profile URL")
    public URL linkedInProfile() {
        try {
            return new URL("https://www.linkedin.com/in/shanewitbeck/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
