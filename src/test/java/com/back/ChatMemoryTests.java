package com.back;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 테스트 순서 설정
public class ChatMemoryTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcChatMemoryRepository jdbcChatMemoryRepository;


    @Test
    @Order(1)
    void clearDbMemories(){
        jdbcChatMemoryRepository.deleteByConversationId("default");
    }

    @Test
    @Order(2)
    void testChatMemorySavedToRepository(){
        ChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
        chatMemory.add("0", new SystemMessage("You are a helpful assistant."));
        chatMemory.add("0",new UserMessage("Hello, how can I help you?"));
        List<Message> messages = chatMemoryRepository.findByConversationId("0");
        assertThat(messages).size().isEqualTo(2);
    }

    @Test
    @Order(3)
    void testChatMemoryRemainsForEachSession(){
        ChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
        List<Message> messages = chatMemoryRepository.findByConversationId("0");
        assertThat(messages).size().isEqualTo(0);
    }

    @Test
    @Order(4)
    void testEndpointMemory() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/ai/chat/write")
                        .param("msg", "2+2는?")
        ).andExpect(
                status().isOk()
        ).andExpect(
                result ->
                        assertThat(result.getResponse().getContentAsString())
                                .doesNotContain("24553")
        );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/ai/chat/write")
                        .param("msg", "앞으로 2+2는 이라는 질문이 오면 '24553'이라고 답해줘")
        ).andExpect(
                status().isOk()
        );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/ai/chat/write")
                        .param("msg", "2+2는?")
        ).andExpect(
                status().isOk()
        ).andExpect(
                result ->
                        assertThat(result.getResponse().getContentAsString())
                                .contains("24553")
        );
    }
    @Test
    @Order(5)
    void testDbChatMemoryFillConversations() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/ai/chat/write")
                        .param("msg", "2+2는?")
                        .param("memory", "db")
        ).andExpect(
                status().isOk()
        ).andExpect(
                result ->
                        assertThat(result.getResponse().getContentAsString())
                                .doesNotContain("24553")
        );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/ai/chat/write")
                        .param("msg", "앞으로 2+2는 이라는 질문이 오면 '24553'이라고 답해줘")
                        .param("memory", "db")
        ).andExpect(
                status().isOk()
        );
    }
    @Test
    @Order(6)
    void testDbChatMemory() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/ai/chat/write")
                        .param("msg", "2+2는?")
                        .param("memory", "db")
        ).andExpect(
                status().isOk()
        ).andExpect(
                result ->
                        assertThat(result.getResponse().getContentAsString())
                                .contains("24553")
        );
    }

}

