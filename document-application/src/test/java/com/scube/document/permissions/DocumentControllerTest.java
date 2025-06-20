package com.scube.document.permissions;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import com.scube.document.SharedTestConfig;
import com.scube.document.permission.Permissions;
import org.junit.jupiter.api.Test;

import static com.scube.document.permissions.MockMvcHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DocumentControllerTest extends SharedTestConfig {

    @Test
    @WithJwt(json = START_JSON + Permissions.Document.GET_FILE + END_JSON)
    void testGetFile_Success() throws Exception {
        var result = performGet(mockMvc, "/download?documentUUID=f3e58a7d-3795-43bc-9487-7b6ba7b1a68f");
        assertNotEquals(403, result.getResponse().getStatus());
    }

    @Test
    void testGetFile_Failure() throws Exception {
        var result = performGet(mockMvc, "/download?documentUUID=f3e58a7d-3795-43bc-9487-7b6ba7b1a68f");
        assertEquals(403, result.getResponse().getStatus());
    }
}