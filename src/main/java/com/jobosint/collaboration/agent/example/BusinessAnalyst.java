package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.tool.Tool;
import com.jobosint.model.ai.CompanyDetail;
import com.jobosint.service.ai.CompanyDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.Map;

@Agent(
        goal = "Provide business analysis of companies"
)
@RequiredArgsConstructor
public class BusinessAnalyst extends AgentService {

    private final CompanyDetailsService companyDetailsService;

    @Value("classpath:/prompts/agent-company-focus.st")
    private Resource companyFocusUserPrompt;

    @Tool(name = "GetCompanyDetail", description = "Given a company name, get the details about the company including website URL")
    public CompanyDetail getCompanyDetails(String name) {
        return companyDetailsService.getCompanyDetails(name);
    }

    @Tool(name = "GetCompanyFocus", description = "Given the name of a company, return the focus of the company")
    public String getCompanyFocus(String companyName) {
        Prompt prompt = createPrompt(companyFocusUserPrompt, Map.of(
                "companyName", companyName
        ));
        return callPromptForString(prompt);
    }
}
