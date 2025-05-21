package com.pedrosilva.chatclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ShellComponent
public class ChatCommands {

    private final ChatClient chatClient;
    private final Map<String, PromptChatMemoryAdvisor> memory = new ConcurrentHashMap<>();

    public ChatCommands(ChatClient.Builder builder, /*ToolCallbackProvider tools, */VectorStore vectorStore) {
        this.chatClient = builder
//                .defaultTools(tools)
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
