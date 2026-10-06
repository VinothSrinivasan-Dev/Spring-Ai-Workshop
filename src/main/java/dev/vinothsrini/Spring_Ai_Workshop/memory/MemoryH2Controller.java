package dev.vinothsrini.Spring_Ai_Workshop.memory;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/memory/h2/")
public class MemoryH2Controller {

    private final ChatMemoryRepository chatMemoryRepository;
    private final JdbcTemplate jdbcTemplate;

    public MemoryH2Controller(ChatMemoryRepository chatMemoryRepository, JdbcTemplate jdbcTemplate) {
        this.chatMemoryRepository = chatMemoryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * List Every conversation ID that has stored messages , via Spring Ai Repository
     * @return conversation IDS
     */
    @GetMapping("/conversations")
    public List<String> conversations() {
        return chatMemoryRepository.findConversationIds();
    }

    @GetMapping("/rows")
    public List<Map<String,Object>> rows(@RequestParam(defaultValue = "50") int limit) {
        return jdbcTemplate.queryForList("SELECT * FROM SPRING_AI_CHAT_MEMORY WHERE LIMIT = ?",limit);
    }

    @GetMapping("/counts")
    public List<Map<String,Object>> counts() {
        return jdbcTemplate.queryForList(
                "SELECT CONVERSATION_ID, COUNT(*) AS TOTAL FROM SPRING_AI_CHAT_MEMORY GROUP BY CONVERSATION_ID");
    }

}
