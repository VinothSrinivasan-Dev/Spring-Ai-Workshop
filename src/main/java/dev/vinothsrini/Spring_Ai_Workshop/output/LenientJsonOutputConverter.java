package dev.vinothsrini.Spring_Ai_Workshop.output;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;

/**
 * A forgiving {@link StructuredOutputConverter} that tolerates markdown code fences.
 *
 * <p><b>Problem it solves:</b> models often reply like this:
 * <pre>
 * Here is your plan:
 * ```json
 * { "destination": "Goa", "activities": [ ... ] }
 * ```
 * </pre>
 * The built-in {@link BeanOutputConverter} expects pure JSON and would throw on
 * the first character. This converter extracts the JSON from inside the fence
 * (if any) and then delegates to a normal {@link BeanOutputConverter}.
 *
 * <p><b>Why {@link #getJsonSchema()} is delegated:</b> in Spring AI 2.0 that method
 * is the bridge that lets a custom converter take part in
 * {@code validateSchema()} and {@code useProviderStructuredOutput()}. If you
 * leave it at its default, both features become no-ops for this converter.
 *
 * @param <T> the target Java type
 */
public class LenientJsonOutputConverter<T> implements StructuredOutputConverter<T> {

    /** Matches ```json ... ``` or ``` ... ``` and captures the inner content. */
    private static final Pattern FENCE = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```");

    private final BeanOutputConverter<T> delegate;

    public LenientJsonOutputConverter(Class<T> targetType) {
        this.delegate = new BeanOutputConverter<>(targetType);
    }

    /** Format instructions appended to the prompt (prompt-based mode). */
    @Override
    public String getFormat() {
        return delegate.getFormat();
    }

    /** JSON Schema used by schema validation and native structured output. */
    @Override
    public String getJsonSchema() {
        return delegate.getJsonSchema();
    }

    /**
     * Strips markdown fences if present, then parses the JSON into the target type.
     *
     * @param source raw text returned by the model
     * @return the parsed object
     */
    @Override
    public T convert(String source) {
        Matcher matcher = FENCE.matcher(source);
        String json = matcher.find() ? matcher.group(1).trim() : source.trim();
        return delegate.convert(json);
    }
}

