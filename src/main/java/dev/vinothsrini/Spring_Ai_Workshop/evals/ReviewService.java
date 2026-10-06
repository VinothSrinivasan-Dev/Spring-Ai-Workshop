package dev.vinothsrini.Spring_Ai_Workshop.evals;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ChatClient chatClient;

    public ReviewService(ChatClient.Builder chatClient) {
        this.chatClient = chatClient
//                .defaultOptions((ChatOptions.Builder) OpenAiChatOptions.builder().temperature(0.1d).build())
                .build();
    }

    public Sentiment classifySentiment(String review){
        String systemPrompt= """
                Classify the sentiment of the following text POSITIVE,NEGATIVE, or NEUTRAL \
                Your response must be only one of three words
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(review)
                .call()
                .entity(Sentiment.class);

    }
}
