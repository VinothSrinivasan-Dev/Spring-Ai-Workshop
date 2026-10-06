package dev.vinothsrini.Spring_Ai_Workshop.output;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record Activity(@JsonPropertyDescription("Name of the place or activity")
                       String name,

                       @JsonPropertyDescription("One-line description of the activity")
                       String description,

                       @JsonPropertyDescription("Best time of day to do it, e.g. Morning, Sunset, Night")
                       String bestTime) {
}
