package com.pedrosilva.chatclient;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ShellComponent
public class ChatCommands {

    private final ChatClient chatClient;
    private final Map<String, PromptChatMemoryAdvisor> memory = new ConcurrentHashMap<>();

    public ChatCommands(ChatClient.Builder builder, List<McpSyncClient> mcpSyncClients, VectorStore vectorStore) {
        var system = """
                You are an AI powered assistant that helps lawyers find information. The lawyers firm is  \s
                called Advogados Lda. You have information about some of the firms cases in the context.\s
                You can also get the users favorite person if the user asks for it. Each time you are asked\s
                for a favorite person, call the appropriate tool. Do not return the previous answer.
                """;

        this.chatClient = builder
                .defaultSystem(system)
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }

    @ShellMethod(key = "chat")
    public String interactiveChat(@ShellOption(defaultValue = "Hello MCP Client!") String question) {
        String user = "1";
        var advisor = this.memory
                .computeIfAbsent(user, u -> PromptChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().chatMemoryRepository(new InMemoryChatMemoryRepository()).build()).build());
        return this.chatClient.prompt()
                .user(question)
                .advisors(advisor).call().content();
    }
}
