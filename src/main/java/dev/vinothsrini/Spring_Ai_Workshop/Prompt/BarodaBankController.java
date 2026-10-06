package dev.vinothsrini.Spring_Ai_Workshop.Prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/baroda")
public class BarodaBankController {

    private final ChatClient chatClient;

    public BarodaBankController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        var systemInstructions= """
                You are a customer service assitance for BarodaBank.
                You can ONLY Discuss:
                - Account balance and transactions
                - Branch locations abd hours
                - General banking services
                
                If asked about anything else, respond: "I can only help with banking-related questions."
                """;


        return chatClient.prompt()
                .user(message)
                .system(systemInstructions)
                .call()
                .content();
    }

}
