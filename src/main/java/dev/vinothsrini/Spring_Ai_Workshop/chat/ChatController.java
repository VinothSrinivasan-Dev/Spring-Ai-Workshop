package dev.vinothsrini.Spring_Ai_Workshop.chat;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private  final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat(){
        return chatClient.prompt()
                .user("Tell me intersting fact about Java")
                .call()
                .content();
    }

    @GetMapping("/stream")
    public Flux<String> stream(){
        return chatClient.prompt()
                .user("I am Visiting Los Vegas Soon,can you please give me 10 places i must visit?")
                .stream()
                .content();
    }


    @GetMapping("joke")
    public @Nullable ChatResponse joke(){
        return chatClient.prompt()
                .user("Tell me a dad joke about dogs")
                .call()
                .chatResponse();

    }

}
