package com.scube.document.permissions;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public class MockMvcHelper {

    public static MvcResult perform(MockMvc mockMvc, HttpMethod method, String endpoint, String contentType, Map<String, Object> body) throws Exception {
        MockHttpServletRequestBuilder perform = MockMvcRequestBuilders
                .request(method, endpoint)
                .contentType(contentType);
        if (body != null) {
            if (contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
                JSONObject jsonBody = new JSONObject(body);
                var jsonBodyString = "{}";
                if (!ObjectUtils.isEmpty(jsonBody) && !ObjectUtils.isEmpty(jsonBodyString))
                    jsonBodyString = jsonBody.toString();
                perform.content(jsonBodyString);
            }
            if (contentType.equalsIgnoreCase(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                MockMultipartHttpServletRequestBuilder multipartPerform = MockMvcRequestBuilders
                        .multipart(endpoint);
                body.forEach((key, value) -> {
                    if (value instanceof MultipartFile file) {
                        try {
                            multipartPerform.file(key, file.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        multipartPerform.param(key, value.toString());
                    }
                });
                perform = multipartPerform;
            }
        }

        return mockMvc
                .perform(perform)
                .andReturn();
    }

    /*
        POST
     */
    public static MvcResult performJsonPost(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.POST, endpoint, MediaType.APPLICATION_JSON_VALUE, null);
    }

    public static MvcResult performJsonPost(MockMvc mockMvc, String endpoint, Map<String, Object> body) throws Exception {
        return perform(mockMvc, HttpMethod.POST, endpoint, MediaType.APPLICATION_JSON_VALUE, body);
    }

    public static MvcResult performFormDataPost(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.POST, endpoint, MediaType.MULTIPART_FORM_DATA_VALUE, null);
    }

    public static MvcResult performFormDataPost(MockMvc mockMvc, String endpoint, Map<String, Object> body) throws Exception {
        return perform(mockMvc, HttpMethod.POST, endpoint, MediaType.MULTIPART_FORM_DATA_VALUE, body);
    }

    /*
        PATCH
     */
    public static MvcResult performJsonPatch(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.PATCH, endpoint, MediaType.APPLICATION_JSON_VALUE, null);
    }

    public static MvcResult performJsonPatch(MockMvc mockMvc, String endpoint, Map<String, Object> body) throws Exception {
        return perform(mockMvc, HttpMethod.PATCH, endpoint, MediaType.APPLICATION_JSON_VALUE, body);
    }

    public static MvcResult performFormDataPatch(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.PATCH, endpoint, MediaType.MULTIPART_FORM_DATA_VALUE, null);
    }

    public static MvcResult performFormDataPatch(MockMvc mockMvc, String endpoint, Map<String, Object> body) throws Exception {
        return perform(mockMvc, HttpMethod.PATCH, endpoint, MediaType.MULTIPART_FORM_DATA_VALUE, body);
    }

    /*
        PUT
     */
    public static MvcResult performJsonPut(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.PUT, endpoint, MediaType.APPLICATION_JSON_VALUE, null);
    }

    public static MvcResult performJsonPut(MockMvc mockMvc, String endpoint, Map<String, Object> body) throws Exception {
        return perform(mockMvc, HttpMethod.PUT, endpoint, MediaType.APPLICATION_JSON_VALUE, body);
    }

    public static MvcResult performFormDataPut(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.PUT, endpoint, MediaType.MULTIPART_FORM_DATA_VALUE, null);
    }

    public static MvcResult performFormDataPut(MockMvc mockMvc, String endpoint, Map<String, Object> body) throws Exception {
        return perform(mockMvc, HttpMethod.PUT, endpoint, MediaType.MULTIPART_FORM_DATA_VALUE, body);
    }

    /*
        DELETE
     */
    public static MvcResult performDelete(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.DELETE, endpoint, MediaType.APPLICATION_JSON_VALUE, null);
    }

    /*
        GET
     */
    public static MvcResult performGet(MockMvc mockMvc, String endpoint) throws Exception {
        return perform(mockMvc, HttpMethod.GET, endpoint, MediaType.APPLICATION_JSON_VALUE, null);
    }

    public static final String START_JSON = """
            {
                "iss": "http://localhost:8443/realms/master",
                "sub": "b3e58a7d-3795-43bc-9487-7b6ba7b1a68f",
                "iat": 1695992542,
                "exp": 1695992642,
                "preferred_username": "ch4mpy",
                "realm_access": {
                    "roles": [
                        "admin",
                        "ROLE_AUTHORIZED_PERSONNEL",
            """;
    public static final String END_JSON = """           
                    ]
                },
                "email": "ch4mp@c4-soft.com",
                "scope": "openid email"
            }
            """;
}
