package com.scube.document.permissions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scube.rabbit.core.AmqpGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;

@RequiredArgsConstructor
public class TestUtils {
    public final MockMvc mockMvc;
    public final ObjectMapper objectMapper;
    private final AmqpGateway amqpGateway;
}