package com.scube.document;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scube.document.permissions.TestUtils;
import com.scube.rabbit.core.AmqpGateway;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DocumentServiceApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithJwt("mock-keycloak.json")
@Import(TestConfig.class)
public abstract class SharedTestConfig {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private AmqpGateway amqpGateway;

    private TestUtils testUtils;

    public TestUtils getTestUtils() {
        if (testUtils != null) return testUtils;
        return testUtils = new TestUtils(mockMvc, objectMapper, amqpGateway);
    }
}