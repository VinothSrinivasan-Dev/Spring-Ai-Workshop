package dev.vinothsrini.Spring_Ai_Workshop.output;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record Itineray( @JsonPropertyDescription("Destination city or region, e.g. Goa")
                        String destination,

                        @JsonPropertyDescription("Ordered list of things to do at the destination")
                        List<Activity> activities) {
}
