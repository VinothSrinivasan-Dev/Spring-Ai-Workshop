package dev.vinothsrini.Spring_Ai_Workshop.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/memory")
public class MemoryController {

    private final ChatClient  statelessChatClient;
    private final ChatClient messageMemoryChatClient;
    private final ChatMemory chatMemory;

    public MemoryController(@Qualifier("statelessChatClient") ChatClient statelessChatClient, @Qualifier("messageMemoryChatClient") ChatClient messageMemoryChatClient, ChatMemory chatMemory) {
        this.statelessChatClient = statelessChatClient;
        this.messageMemoryChatClient = messageMemoryChatClient;
        this.chatMemory = chatMemory;
    }


    /**
     * <b>No memory (baseline).</b> Each call is independent, so the model cannot recall
     * anything from earlier calls. Use it to prove that memory is what makes the difference.
     *
     * @param message the user message
     * @return the model reply
     */

    @GetMapping("/none")
    public String none(@RequestParam String message) {
        return statelessChatClient.prompt()
                .user(message)
                .call()
                .content();

    }
    /**
     * <b>{@code MessageChatMemoryAdvisor}.</b> History is sent as real USER and ASSISTANT
     * messages. This is the recommended default.
     *
     * @param conversationId key that separates one conversation from another
     * @param text the user message
     * @return the model reply, aware of earlier messages in this conversation
     */

    @GetMapping("/message")
    public String message(@RequestParam String text,@RequestParam String conversationId) {
        return messageMemoryChatClient.prompt()
                .user(text)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .call()
                .content();

    }

    /**
     * <b>Inspect stored memory.</b> Reads directly from {@link ChatMemory}. With a window
     * of 10 you will never see more than 10 messages here, which shows the eviction working.
     *
     * @param conversationId the conversation to inspect
     * @return list of {role, text} entries, oldest first
     */
    @GetMapping("/history")
    public List<Map<String,String>> history(@RequestParam String conversationId) {
        List<Message> messages = chatMemory.get(conversationId);
        return messages.stream()
                .map(m -> Map.of("role", m.getMessageType().name(), "text", String.valueOf(m.getText())))
                .toList();
    }

    @DeleteMapping("/{conversationId}")
    public String delete(@PathVariable String conversationId) {
        chatMemory.clear(conversationId);
        return "Cleared Conversation: " + conversationId;
    }

    /**
     * <b>User-scoped conversation IDs.</b> Shows the recommended way to build the key in a
     * real application: combine a user identifier with a chat identifier, so one user can have
     * many chats and users can never read each other's history.
     *
     * <p>In production, take {@code userId} from your security context (e.g. the JWT subject),
     * not from a request parameter.
     *
     * @param userId the authenticated user (demo: passed as a parameter)
     * @param chatId which chat thread of this user
     * @param message the user message
     * @return the model reply
     */
    @GetMapping("user-scoped")
    public String userScoped(@RequestParam String userId,
                             @RequestParam String message,
                             @RequestParam(defaultValue = "default") String chatId) {
        String conversationId = userId+":"+message;

        return messageMemoryChatClient.prompt()
                .user(message)
                .advisors(a-> a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .call()
                .content();

    }


}
