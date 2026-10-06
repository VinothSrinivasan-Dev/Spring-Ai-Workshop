package dev.vinothsrini.Spring_Ai_Workshop.config;

import dev.vinothsrini.Spring_Ai_Workshop.output.Itineray;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    /**
     * Plain client with no special advisors. Used for the basic, per-call
     * {@code validateSchema()} and per-call {@code useProviderStructuredOutput()} demos.
     */
    @Bean
    ChatClient defaultChatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    /**
     * Client with a <b>customized</b> {@link StructuredOutputValidationAdvisor}.
     *
     * <p>Defaults of the auto-registered advisor (when you call {@code validateSchema()}):
     * 3 retry attempts and Spring AI's default JsonMapper. Registering your own instance
     * replaces the auto-registered one.
     *
     * <p>Here we allow 5 attempts. The advisor is bound to one output type
     * ({@link Itineray}), so this client is dedicated to that type.
     */
    @Bean
    ChatClient strictChatClient(ChatClient.Builder builder) {
        var validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .outputType(Itineray.class)   // schema to validate against
                .maxRepeatAttempts(5)          // default is 3
                .build();

        return builder
                .defaultAdvisors(validationAdvisor)
                .build();
    }

    /**
     * Client with provider-native structured output enabled <b>globally</b>.
     * Every {@code .entity(...)} call made with this client uses the provider's
     * native structured output API (if the model's options support it; otherwise
     * Spring AI silently falls back to prompt-based mode).
     */
    @Bean
    ChatClient nativeChatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .build();
    }
}
