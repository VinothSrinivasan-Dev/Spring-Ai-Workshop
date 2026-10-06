package dev.vinothsrini.Spring_Ai_Workshop.tools.datatime;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateAndTime(){
        return String.valueOf(LocalDateTime.now().atZone(ZoneId.of(LocaleContextHolder.getTimeZone().toZoneId().toString())));

    }
}
