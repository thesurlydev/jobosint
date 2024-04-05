package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService
import com.jobosint.collaboration.annotation.Agent
import com.jobosint.collaboration.annotation.Tool
import com.jobosint.model.ai.CompanyDetail
import com.jobosint.service.ai.CompanyDetailsService
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.parser.BeanOutputParser
import java.util.Map

@Agent(
        goal = "Write professional resumes and cover letters",
        tools = ["CoverLetterWriter"]
)
class ResumeWriter(val chatClient: ChatClient,
                   val companyDetailsService: CompanyDetailsService): AgentService() {

    @Tool(name = "CoverLetterWriter", description="Write a cover letter for a job application")
    fun coverLetterWriter(companyName: String, jobTitle: String): String {



        val userMessage =
            """
                Write a professional cover letter for a job application.
                The company is $companyName and the job title is $jobTitle.
            """.trimIndent()

        val promptTemplate = PromptTemplate(
            userMessage, Map.of<String, Any>(
                "companyName", companyName,
                "jobTitle", jobTitle,
            )
        )
        val prompt = promptTemplate.create()
        val generation = chatClient.call(prompt).result
        return generation.output.content
    }
}
