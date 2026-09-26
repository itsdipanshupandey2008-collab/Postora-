
package com.socialautomation.engine.media;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.socialautomation.engine.auth.JwtService;
import com.socialautomation.engine.user.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Test
    void fakeImageShouldBeRejected() throws Exception {

        String email = "media-test-" + UUID.randomUUID() + "@example.com";

        userService.createUser(email, "testpassword123");

        String token = jwtService.generateToken(email);

        byte[] fakeImage = "this is not an image"
                .getBytes(StandardCharsets.UTF_8);

        mockMvc.perform(
                multipart("/api/media")
                        .file("file", fakeImage)
                        .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isConflict());
    }

    @Test
    void validImageShouldBeAccepted() throws Exception {

        String email = "media-test-" + UUID.randomUUID() + "@example.com";

        userService.createUser(email, "testpassword123");

        String token = jwtService.generateToken(email);

        byte[] validPng = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D,
                0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x01,
                0x08, 0x06, 0x00, 0x00,
                0x00, 0x1F, 0x15, (byte) 0xC4,
                (byte) 0x89
        };

        mockMvc.perform(
                multipart("/api/media")
                        .file("file", validPng)
                        .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk());
    }
}