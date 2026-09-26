package com.socialautomation.engine;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.socialautomation.engine.auth.JwtService;
import com.socialautomation.engine.user.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class SocialAutomationEngineApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Test
    void protectedEndpointWithoutJwtShouldReturn403() throws Exception {

        mockMvc.perform(
                get("/api/users/protected")
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointWithValidJwtShouldReturn200() throws Exception {

        String token = jwtService.generateToken("secure@example.com");

        mockMvc.perform(
                get("/api/users/protected")
                        .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk());
    }

    @Test
    void meEndpointWithValidJwtShouldReturnCurrentUser() throws Exception {

        String email = "test-" + UUID.randomUUID() + "@example.com";

        userService.createUser(email, "testpassword123");

        String token = jwtService.generateToken(email);

        mockMvc.perform(
                get("/api/users/me")
                        .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(email));
    }
}