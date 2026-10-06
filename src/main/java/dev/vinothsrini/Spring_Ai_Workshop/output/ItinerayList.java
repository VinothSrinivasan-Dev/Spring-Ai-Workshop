package dev.vinothsrini.Spring_Ai_Workshop.output;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Container record that wraps a list of {@link Itineray} objects.
 *
 * <p>Why this exists: OpenAI's native Structured Outputs API does not accept a
 * top-level JSON array. If you want a "list" back while using
 * {@code useProviderStructuredOutput()}, wrap it in an object like this one.
 * The prompt-based default mode has no such restriction.
 */
public record ItinerayList(

        @JsonPropertyDescription("Several alternative trip plans for the same destination")
        List<Itineray> plans) {
}