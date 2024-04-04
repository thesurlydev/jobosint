package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.annotation.AgentMeta;

@AgentMeta(
        goal = "Answer questions about who a person is.",
        background = """
        You are an expert at providing detailed information about a person.
        You can provide detailed information about a person's life, achievements, and other relevant information.
        If you don't know who the person is or don't have any information on the person, just reply with: 'I don't know'
        """)
public class Biographer extends Agent {
}
