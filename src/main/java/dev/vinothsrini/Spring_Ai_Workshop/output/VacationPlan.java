package dev.vinothsrini.Spring_Ai_Workshop.output;

import dev.vinothsrini.Spring_Ai_Workshop.config.AIConfig;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class VacationPlan {


    private final ChatClient defaultChatClient;
    private final ChatClient nativeChatClient;
    private final ChatClient strictChatClient;

    private static final String PROMPT =
            "I want to plan a trip to {destination}. Give me a list of things to do.";


public VacationPlan(
        @Qualifier("defaultChatClient") ChatClient defaultChatClient,
        @Qualifier("strictChatClient") ChatClient strictChatClient,
        @Qualifier("nativeChatClient") ChatClient nativeChatClient
) {

    this.defaultChatClient = defaultChatClient;
    this.nativeChatClient = nativeChatClient;
    this.strictChatClient = strictChatClient;
}


    @GetMapping("/vacation/unstructured")
    public String unstructured() {
        return defaultChatClient.prompt()
                .user("I want to plan a trip to Goa.Give me a list of things to do.")
                .call()
                .content();
    }
    // ---------------------------------------------------------------------
    // 1. BASIC
    // ---------------------------------------------------------------------

    /**
     * <b>1. Basic (prompt-based) structured output.</b>
     *
     * <p><b>What happens:</b> Spring AI generates a JSON Schema from {@link Itineray},
     * appends format instructions to the prompt, calls the model, and converts the text
     * reply into a Java object using {@code BeanOutputConverter}.
     *
     * <p><b>Guarantee:</b> none. The model is asked, not forced, to follow the schema.
     * Extra fields, missing fields or prose around the JSON make parsing throw.
     *
     * <p><b>Works on:</b> every model Spring AI supports.
     *
     * <p><b>Try:</b> {@code GET /vacation/structured/basic?destination=Goa}
     *
     * @param destination place to plan for (default Goa)
     * @return parsed Itineray
     */
    @GetMapping("/basic")
    public @Nullable Itineray basic(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(Itineray.class);
    }

    // ---------------------------------------------------------------------
    // 2. SCHEMA VALIDATION + SELF CORRECTION
    // ---------------------------------------------------------------------

    /**
     * <b>2. Schema validation with self-correction ({@code validateSchema()}).</b>
     *
     * <p><b>What happens:</b>
     * <ol>
     *   <li>The model responds.</li>
     *   <li>Spring AI validates the response against the {@link Itineray} schema.</li>
     *   <li>If valid, the typed object is returned.</li>
     *   <li>If invalid, the specific validation error (e.g. "missing required field",
     *       "expected array, got string") is appended to the user prompt and the call is
     *       repeated, up to <b>3 attempts by default</b>.</li>
     * </ol>
     * The model sees the exact error, so each retry is informed rather than blind.
     *
     * <p><b>Under the hood:</b> a recursive advisor, {@code StructuredOutputValidationAdvisor},
     * is auto-registered when you call {@code validateSchema()}.
     *
     * <p><b>Cost:</b> each retry is another paid LLM call, so worst-case tokens and latency
     * can be roughly 3x.
     *
     * <p><b>Works on:</b> every model (nothing provider-specific).
     *
     * @param destination place to plan for (default Goa)
     * @return validated Itineray
     */
    @GetMapping("/validated")
    public @Nullable Itineray validated(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(Itineray.class, spec -> spec.validateSchema());
    }

    // ---------------------------------------------------------------------
    // 3. CUSTOMIZING THE ADVISOR
    // ---------------------------------------------------------------------

    /**
     * <b>3. Customized validation advisor.</b>
     *
     * <p><b>What happens:</b> this uses {@code strictChatClient} (see {@link AIConfig}), which
     * has a hand-built {@code StructuredOutputValidationAdvisor} registered as a default
     * advisor with {@code maxRepeatAttempts(5)} instead of the default 3. An explicitly
     * registered advisor <i>replaces</i> the auto-registered one.
     *
     * <p>Note there is <b>no {@code validateSchema()} here</b>: validation and retry run
     * for every call made through that client.
     *
     * <p><b>Use when:</b> you need more retries, a custom {@code JsonMapper}, or a
     * pre-supplied schema. Check the builder's methods in your IDE for the extra options.
     *
     * <p><b>Design note:</b> the advisor is bound to one output type ({@link Itineray}),
     * so dedicate a client per type.
     *
     * @param destination place to plan for (default Goa)
     * @return validated Itineray
     */
    @GetMapping("/custom-advisor")
    public @Nullable Itineray customAdvisor(@RequestParam(defaultValue = "Goa") String destination) {
        return strictChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(Itineray.class);
    }

    // ---------------------------------------------------------------------
    // 4, 5, 6. PROVIDER NATIVE STRUCTURED OUTPUT
    // ---------------------------------------------------------------------

    /**
     * <b>4. Provider-native structured output, per call ({@code useProviderStructuredOutput()}).</b>
     *
     * <p><b>What changes on the wire:</b>
     * <ul>
     *   <li>The system prompt no longer carries a JSON format instruction (fewer tokens).</li>
     *   <li>The schema is sent to the provider as an API-level field.</li>
     *   <li>The provider's runtime enforces conformance; invalid output cannot be emitted.</li>
     * </ul>
     *
     * <p><b>Supported (2.0):</b> OpenAI, Anthropic, Google GenAI, Mistral AI, Ollama (model-specific).
     * Support is detected by checking whether the model's chat options implement
     * {@code StructuredOutputChatOptions}. If not, the flag is silently ignored and the
     * prompt-based default runs.
     *
     * <p><b>Why it is off by default:</b> support varies. Providers often accept only part of
     * JSON Schema ({@code $ref}, {@code anyOf}/{@code oneOf}, regex patterns, recursive types and
     * deep arrays are common problems).
     *
     * @param destination place to plan for (default Goa)
     * @return Itineray produced under provider-enforced schema
     */
    @GetMapping("/native")
    public @Nullable Itineray nativeOutput(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(Itineray.class, spec -> spec.useProviderStructuredOutput());
    }

    /**
     * <b>5. Native structured output via an advisor parameter.</b>
     *
     * <p>Same effect as endpoint 4, but switched on with
     * {@code AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT} on the request. The final
     * {@code .entity(Itineray.class)} call stays unchanged.
     *
     * <p><b>Use when:</b> you want to toggle native mode per request while keeping
     * the terminal call simple.
     *
     * @param destination place to plan for (default Goa)
     * @return Itineray produced under provider-enforced schema
     */
    @GetMapping("/native-param")
    public @Nullable Itineray nativeParam(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(Itineray.class);
    }

    /**
     * <b>6. Native structured output enabled globally on the client.</b>
     *
     * <p>Uses {@code nativeChatClient} (see {@link AIConfig}), built with
     * {@code defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)}. Every
     * {@code .entity(...)} through this client is native, with no per-call code.
     *
     * <p><b>Use when:</b> your whole application targets a provider with solid native support.
     *
     * @param destination place to plan for (default Goa)
     * @return Itineray produced under provider-enforced schema
     */
    @GetMapping("/native-global")
    public @Nullable Itineray nativeGlobal(@RequestParam(defaultValue = "Goa") String destination) {
        return nativeChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(Itineray.class);
    }

    // ---------------------------------------------------------------------
    // 7. BEST OF BOTH
    // ---------------------------------------------------------------------

    /**
     * <b>7. Native output plus self-correction (recommended for production).</b>
     *
     * <p>The two switches solve different problems and compose:
     * <ul>
     *   <li>{@code useProviderStructuredOutput()} is a <i>request-side</i> constraint that
     *       minimizes malformed output.</li>
     *   <li>{@code validateSchema()} is a <i>response-side</i> safety net that catches
     *       residual cases (provider edge cases, Ollama reasoning models emitting
     *       "thinking" text) and retries with the error message.</li>
     * </ul>
     *
     * <p><b>Use when:</b> a missing field or wrong type would corrupt state or fail later
     * in your code.
     *
     * @param destination place to plan for (default Goa)
     * @return Itineray that is provider-constrained and schema-validated
     */
    @GetMapping("/robust")
    public @Nullable Itineray robust(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(Itineray.class, spec -> spec
                        .useProviderStructuredOutput()
                        .validateSchema());
    }

    // ---------------------------------------------------------------------
    // 8, 9. GENERIC TYPES
    // ---------------------------------------------------------------------

    /**
     * <b>8. Generic types with {@link ParameterizedTypeReference}.</b>
     *
     * <p>{@code .entity(Class)} handles concrete classes. For {@code List<T>} or
     * {@code Map<K,V>} use a {@code ParameterizedTypeReference}. The same spec consumer
     * ({@code validateSchema()}) is available as an overload.
     *
     * <p>Top-level arrays work in the default prompt-based mode. They do <b>not</b> work
     * with native mode on OpenAI (see {@link #nativeList}).
     *
     * @param destination place to plan for (default Goa)
     * @return several validated itineraries
     */
    @GetMapping("/list")
    public @Nullable List<Itineray> list(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .user(u -> u.text("Give me 3 different trip plans for {destination}.")
                        .param("destination", destination))
                .call()
                .entity(new ParameterizedTypeReference<List<Itineray>>() {},
                        spec -> spec.validateSchema());
    }

    /**
     * <b>9. List with native mode, using a wrapper record.</b>
     *
     * <p>OpenAI's Structured Outputs API rejects a top-level array, so requesting
     * {@code List<Itineray>} natively would fail. The fix is a container object
     * ({@link ItinerayList}) whose field holds the list.
     *
     * @param destination place to plan for (default Goa)
     * @return wrapper containing several itineraries
     */
    @GetMapping("/native-list")
    public @Nullable ItinerayList nativeList(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .user(u -> u.text("Give me 3 different trip plans for {destination}.")
                        .param("destination", destination))
                .call()
                .entity(ItinerayList.class, spec -> spec.useProviderStructuredOutput());
    }

    // ---------------------------------------------------------------------
    // 10. FULL RESPONSE
    // ---------------------------------------------------------------------

    /**
     * <b>10. Entity plus raw {@link ChatResponse} ({@code responseEntity}).</b>
     *
     * <p>{@code .entity(...)} returns only the parsed object. {@code .responseEntity(...)}
     * returns both the parsed entity and the underlying {@code ChatResponse}, giving access
     * to token usage and other metadata. It has the same overloads as {@code .entity(...)}.
     *
     * <p><b>Use when:</b> you want to monitor cost, e.g. how many tokens self-correction
     * retries consumed.
     *
     * <p>Spring AI's {@code ResponseEntity} is different from Spring Web's; the import here
     * is {@code org.springframework.ai.chat.client.ResponseEntity}.
     *
     * @param destination place to plan for (default Goa)
     * @return map containing the Itineray and total token usage
     */
    @GetMapping("/with-usage")
    public Map<String, Object> withUsage(@RequestParam(defaultValue = "Goa") String destination) {
        ResponseEntity<ChatResponse, Itineray> result = defaultChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .responseEntity(Itineray.class, spec -> spec.validateSchema());

        Itineray Itineray = result.entity();
        ChatResponse raw = result.response();
        long totalTokens = raw.getMetadata().getUsage().getTotalTokens();

        return Map.of("Itineray", Itineray, "totalTokens", totalTokens);
    }

    // ---------------------------------------------------------------------
    // 11. CUSTOM CONVERTER
    // ---------------------------------------------------------------------

    /**
     * <b>11. Custom {@code StructuredOutputConverter}.</b>
     *
     * <p><b>Problem:</b> models sometimes wrap JSON in markdown code fences, which makes the
     * built-in {@code BeanOutputConverter} throw. {@link LenientJsonOutputConverter} strips
     * the fences, then delegates to the normal converter.
     *
     * <p>Because it delegates {@code getJsonSchema()}, {@code validateSchema()} still works,
     * giving resilient parsing plus self-correction with no extra wiring.
     *
     * <p>For non-JSON formats (YAML, CSV), implement {@code StructuredOutputConverter} from
     * scratch and leave {@code getJsonSchema()} at its default; validation and native mode
     * then sit out and the prompt-based path is used.
     *
     * @param destination place to plan for (default Goa)
     * @return Itineray parsed leniently and validated
     */
    @GetMapping("/custom-converter")
    public @Nullable Itineray customConverter(@RequestParam(defaultValue = "Goa") String destination) {
        return defaultChatClient.prompt()
                .user(u -> u.text(PROMPT).param("destination", destination))
                .call()
                .entity(new LenientJsonOutputConverter<>(Itineray.class),
                        spec -> spec.validateSchema());
    }
}
