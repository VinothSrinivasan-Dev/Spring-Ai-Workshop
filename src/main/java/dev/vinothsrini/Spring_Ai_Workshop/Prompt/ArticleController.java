package dev.vinothsrini.Spring_Ai_Workshop.Prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {

    private final ChatClient chatClient;


    public ArticleController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/posts/new")
    public String newPost(@RequestParam(value="topic", defaultValue = "JDK Virutual Threads") String topic) {

        var system= """
                Blog Post Generator Guidelines:
                
                Adhere strictly to the following parameters for every request:
                
                1. LENGTH & PURPOSE
                - Target Length: Maintain a length between 100 to 500 words unless the user specifies otherwise.
                - Primary Objective: Educate, engage, and retain the reader's attention while providing immediate, actionable value based on the user's topic.
                
                2. STRUCTURE
                - Title: Generate a compelling, click-worthy H1 title (do not use clickbait; focus on clarity and value).
                - Introduction: Start with a strong hook (1-2 sentences) that states the core problem and explicitly promises what the reader will learn.
                - Body Sections: Divide the content into logical sections using descriptive H2 and H3 subheadings.\s
                - Conclusion: End with a brief summary section titled "Final Thoughts" or "Conclusion".
                - Call to Action (CTA): Conclude the entire post with one clear, actionable next step for the reader.
                
                3. CONTENT REQUIREMENTS
                - Value Density: Avoid fluff, repetitive phrasing, or generic filler text. Every paragraph must advance the topic.
                - Deep Dives: Use bulleted or numbered lists to break down complex steps, strategies, or conceptual lists.
                - Examples: Provide practical, real-world examples or analogies to clarify abstract or technical concepts.
                
                4. TONE & STYLE
                - Voice: Write in a conversational, accessible, yet authoritative voice using first and second person ("I", "We", "You").
                - Readability: Keep sentences short and punchy. Use the active voice.\s
                - Paragraph Limits: Restrict paragraphs to a maximum of 3-4 sentences to ensure the post is highly scannable, especially on mobile devices.
                - Language: Avoid overly dense jargon unless explaining it immediately after introduction.
                
                5. RESPONSE FORMAT
                - Provide the final output using clean Markdown formatting (e.g., # For Title, ## For H2, ### For H3, **bold** for key concepts).
                - Do not include conversational filler before or after the blog post (e.g., do not say "Here is the blog post you requested:"). Start directly with the Markdown title.
                
                """;


        return chatClient.prompt()
                .user(u-> {
                    u.text("Write me a blog post about {topic}");
                    u.param("topic", topic);
                })
                .system(system)
                .call().
                content();


    }
}
