package dev.vinothsrini.Spring_Ai_Workshop.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean
    ChatMemory chatMemory(ChatMemoryRepository repository ) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(5)
                .build();

    }
    /**
     * NO MEMORY: a plain, stateless client. Marked {@code @Primary} so that any other class
     * that injects a bare {@code ChatClient} (without a qualifier) still resolves.
     *
     * <p>Every call is independent. The model has no idea what you said before.
     */
    @Bean
    ChatClient statelessChatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

    /**
     * ADVISOR TYPE 1: {@link MessageChatMemoryAdvisor}
     *
     * <p><b>How it injects memory:</b> history is added to the request as real chat
     * messages, in order: {@code USER, ASSISTANT, USER, ASSISTANT, ..., current USER}.
     * This matches how chat models are designed to be used.
     *
     * <p><b>Best for:</b> most applications. Models understand roles properly, and tool
     * calling works cleanly.
     *
     * <p><b>Downside:</b> the whole window is sent every time (token cost).
     */
    @Bean
    ChatClient messageMemoryChatClient(ChatClient.Builder builder, ChatMemory  chatMemory) {
        return builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

    }

}
