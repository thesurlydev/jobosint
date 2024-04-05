package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService
import com.jobosint.collaboration.annotation.Agent
import com.jobosint.collaboration.annotation.Tool
import org.springframework.ai.chat.ChatClient

@Agent(
        goal = "Write professional resumes and cover letters",
        tools = ["CoverLetterWriter"]
)
class ResumeWriter(val chatClient: ChatClient): AgentService() {

    @Tool(name = "CoverLetterWriter", description="Write a cover letter for a job application")
    fun coverLetterWriter(companyName: String, jobTitle: String) {
        // TODO Write a cover letter

    }

}
