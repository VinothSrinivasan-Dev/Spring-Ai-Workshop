package dev.vinothsrini.Spring_Ai_Workshop.rag;

import com.openai.models.vectorstores.VectorStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelsController {

    private final ChatClient chatClient;

    public ModelsController(ChatClient.Builder chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient
//                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }

    @GetMapping("rag/models")
    public Models faq(@RequestParam(value="message", defaultValue = "Give me a list of all the models from OpenAI along with their context windows") String message) {
        return  chatClient.prompt()
                .user(message)
                .call()
                .entity(Models.class);

    }
}
