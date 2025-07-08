package com.back.domain.ai.chat.controller;

import com.back.domain.ai.chat.tools.DateTools;
import com.back.domain.ai.schedules.tools.ScheduleTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.aop.framework.adapter.AdvisorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/ai/chat")
@RequiredArgsConstructor
public class ApiV1AiChatController {
    private final OpenAiChatModel openAiChatModel;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    private final ChatClient dbChatClient;
    private final ChatMemory dbChatMemory;

    private final ScheduleTools scheduleTools;
    private final DateTools dateTools;

    @GetMapping("/write")
    public String write(String msg,String memory) {
        String conversationId = "default";
        if (Objects.equals(memory, "db")) {
            List<Message> memories = dbChatMemory.get(conversationId);
            String response = dbChatClient.prompt().tools(dateTools).messages(memories).user(msg).call().content();
            if (response == null || response.isEmpty()) {
                return "죄송합니다. 응답을 생성할 수 없습니다.";
            }
            return response;
        }else{
            String response = chatClient.prompt()
                    .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                    .tools(dateTools)
                    .user(msg).call().content();
            if (response == null || response.isEmpty()) {
                return "죄송합니다. 응답을 생성할 수 없습니다.";
            }
            return response;
        }
    }

    @GetMapping("/write2")
    public String write2(String msg,String memory) {
        String conversationId = "default2";
        if (Objects.equals(memory, "db")) {
            List<Message> memories = dbChatMemory.get(conversationId);
            String response = dbChatClient.prompt().tools(dateTools).messages(memories).user(msg).call().content();
            if (response == null || response.isEmpty()) {
                return "죄송합니다. 응답을 생성할 수 없습니다.";
            }
            return response;
        }else{
            String response = chatClient.prompt()
                    .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                    .tools(dateTools).user(msg).call().content();
            if (response == null || response.isEmpty()) {
                return "죄송합니다. 응답을 생성할 수 없습니다.";
            }
            return response;
        }
    }

    @GetMapping("/schedules")
    public String scheduleAI(String msg){
        String systemPrompt = """
                너는 일정 관리 AI야.
                - 주어진 도구를 가지고 사용자의 일정을 관리 할 수 있어
//                - 현재 날짜는 %s
                """.formatted(OffsetDateTime.now().toString());
        String conversationId = "scheduleAI";
        List<Message> memories = chatMemory.get(conversationId);
        return chatClient
                .prompt(systemPrompt)
                .tools(scheduleTools,dateTools)
                .messages(memories)
                .user(msg)
                .call().content();
    }
}
