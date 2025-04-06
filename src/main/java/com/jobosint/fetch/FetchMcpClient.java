package com.jobosint.fetch;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientBuilderConfigurer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FetchMcpClient {

    private final ChatClient chatClient;

    public FetchMcpClient(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        this.chatClient = chatClientBuilder
                .defaultSystem("You are useful assistant and can fetch web content.")
                .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }

    public String fetch(String url, String instructions) {
        return chatClient.prompt("fetch " + url + " " + instructions).call().entity(String.class);
    }
}
