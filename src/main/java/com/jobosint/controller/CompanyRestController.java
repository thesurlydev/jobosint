package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.ai.CompanyDetail;
import com.jobosint.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyRestController {

    private final CompanyService companyService;
    private final ChatClient chatClient;

    @GetMapping()
    @Operation(summary = "Get all companies")
    public Iterable<Company> getCompanies() {
        return companyService.getAllSorted();
    }

    @GetMapping("/{name}/detail")
    @Operation(summary = "Return website given a company name")
    public CompanyDetail getCompanyWebsite(@PathVariable String name) {
        var outputParser = new BeanOutputParser<>(CompanyDetail.class);

        String userMessage =
                """
                Get the official details for the company {name}.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("name", name, "format",
                outputParser.getFormat() ));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();

        return outputParser.parse(generation.getOutput().getContent());
    }

    @PostMapping()
    @Operation(summary = "Save a company")
    public Company save(@RequestBody Company company) {
        return companyService.saveCompany(company);
    }
}
