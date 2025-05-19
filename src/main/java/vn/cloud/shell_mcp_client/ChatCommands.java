package vn.cloud.shell_mcp_client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ChatCommands {

    private final ChatClient chatClient;

    public ChatCommands(ChatClient.Builder builder, ToolCallbackProvider tools) {
        this.chatClient = builder
                .defaultTools(tools)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @ShellMethod(key = "chat")
    public String interactiveChat(@ShellOption(defaultValue = "Hello MCP Client!") String question) {
        return this.chatClient.prompt(question).call().content();
    }
}
