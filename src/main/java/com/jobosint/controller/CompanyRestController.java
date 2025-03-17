package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.ai.CompanyDetail;
import com.jobosint.model.ai.CompanyFocus;
import com.jobosint.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyRestController {

    private final CompanyService companyService;
    private final ChatModel chatModel;

    @GetMapping()
    @Operation(summary = "Get all companies")
    public Iterable<Company> getCompanies() {
        return companyService.getAllSorted();
    }

    @GetMapping("/{name}/detail")
    @Operation(summary = "Return website given a company name")
    public CompanyDetail getCompanyWebsite(@PathVariable String name) {
        var outputParser = new BeanOutputConverter<>(CompanyDetail.class);

        String userMessage = """
                Get the official details for the company {name}.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("name", name, "format",
                outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatModel.call(prompt).getResult();

        return outputParser.convert(generation.getOutput().getText());
    }

    @GetMapping("/{name}/focus")
    @Operation(summary = "Given a company name, return company focus in paragraph form suitable for embedding in a cover letter")
    public CompanyFocus getCompanyFocus(@PathVariable String name) {
        var outputParser = new BeanOutputConverter<>(CompanyFocus.class);

        String userMessage = """
                I am a software engineer and writing a cover letter about why I want to work at a technology company.
                Given a name of a company, I want you to provide no more than three paragraphs specific to the company's focus
                that resonates with my professional ethos and makes me excited about the potential of working at the company.
                The paragraphs should be in the first person and addressed to the company. The returned companyName should be capitalized.

                The name of the company is: {name}

                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(
                userMessage,
                Map.of(
                        "name", name,
                        "format", outputParser.getFormat()
                )
        );
        Prompt prompt = promptTemplate.create();
        Generation generation = chatModel.call(prompt).getResult();

        return outputParser.convert(generation.getOutput().getText());
    }

    @PostMapping()
    @Operation(summary = "Save a company")
    public Company save(@RequestBody Company company) {
        return companyService.saveCompany(company);
    }
}
