package com.jobosint.brave;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BraveMcpClient {

    private final ChatClient chatClient;

    public BraveMcpClient(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        this.chatClient = chatClientBuilder
                .defaultSystem("You are useful assistant and can perform web searches Brave's search API to reply to your questions.")
                .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }

    public BraveSearchResponse search(String query) {
        return chatClient.prompt(query).call().entity(BraveSearchResponse.class);
    }
}
