package com.back.domain.ai;


import com.back.domain.ai.chat.tools.DateTools;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MCPConfig {
    @Bean
    public ToolCallbackProvider myTools(DateTools dateTools){
        return MethodToolCallbackProvider.builder().toolObjects(
                dateTools
        ).build();
    }
}
