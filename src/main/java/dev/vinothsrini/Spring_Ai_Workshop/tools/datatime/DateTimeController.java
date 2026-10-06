package dev.vinothsrini.Spring_Ai_Workshop.tools.datatime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DateTimeController {

    private final ChatClient chatClient;

    public DateTimeController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/tools")
    public String getTools(){
        return chatClient.prompt()
                .user("What;s tommorrow date?")
                .tools(new DateTimeTools())
                .call()
                .content();
    }
}
