package com.back.domain.ai.chat.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class DateTools {
    @Tool
    public String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }
}
