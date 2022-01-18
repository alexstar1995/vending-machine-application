package com.mvpfactory.vendingmachine.facade;

import lombok.SneakyThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("integrationTest")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private static String USER_SELLER_JSON;
    private static String USER_SELLER_RESPONSE_JSON;
    private static String USER_BUYER_JSON;
    private static String USER_BUYER_RESPONSE_JSON;
    private static String USER1_UPDATE_WRONG_DEPOSIT_JSON;
    private static String USER1_UPDATE_VALID_DEPOSIT_JSON;
    private static String USER1_UPDATE_RESPONSE_JSON;
    private static String DEPOSIT_VALID_COIN_REQUEST_JSON;
    private static String DEPOSIT_VALID_COIN_RESPONSE_JSON;
    private static String DEPOSIT_INVALID_COIN_REQUEST_JSON;
    private static final String NON_EXISTING_USER = "non-existing-user";
    private static final String NON_EXISTING_PASSWORD = "nonExistingPass";
    private static final String USER_SELLER_NAME = "alex-2";
    private static final String USER_SELLER_PASSWORD = "pass1";
    private static final String USER_BUYER_NAME = "alex-1";
    private static final String USER_BUYER_PASSWORD = "pass2";
    private static String ALL_USERS_RESPONSE_JSON;

    static {
        try {
            USER_SELLER_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/user1-request.json")));
            USER_SELLER_RESPONSE_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/user1-response.json")));
            USER_BUYER_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/user2-request.json")));
            USER_BUYER_RESPONSE_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/user2-response.json")));
            ALL_USERS_RESPONSE_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/all-users-response.json")));
            USER1_UPDATE_WRONG_DEPOSIT_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/user1-update-wrong-deposit.json")));
            USER1_UPDATE_VALID_DEPOSIT_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/user1-update-valid-deposit.json")));
            USER1_UPDATE_RESPONSE_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/user1-update-response.json")));
            DEPOSIT_VALID_COIN_REQUEST_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/deposit-coin-request.json")));
            DEPOSIT_INVALID_COIN_REQUEST_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/deposit-wrong-coin-request.json")));
            DEPOSIT_VALID_COIN_RESPONSE_JSON = new String(Files.readAllBytes(Paths.get("src/integrationTest/resources/deposit-coin-response.json")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @SneakyThrows
    private MvcResult signUp(String USER) {

        return mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/signup")
                        .content(USER)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void signUpNewUser_thenReturnHttp200() {
        MvcResult result = signUp(USER_SELLER_JSON);
        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(USER_SELLER_RESPONSE_JSON);
    }

    @Test
    @SneakyThrows
    public void signUpExistingUser_thenReturnHttp400() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/signup")
                        .content(USER_SELLER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void getAllUsersWithNonExistingCredentials_returnHttp401() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((NON_EXISTING_USER + ":" + NON_EXISTING_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void getAllUsersWithCorrectCredentials_returnHttp200() {
        signUp(USER_SELLER_JSON);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                                    .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                        Base64Utils
                                            .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                            .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(ALL_USERS_RESPONSE_JSON);
    }

    @Test
    @SneakyThrows
    public void getUserWithCorrectCredentials_returnHttp200() {
        signUp(USER_SELLER_JSON);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/name/" + USER_SELLER_NAME)
                                  .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                        Base64Utils
                                            .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                            .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(USER_SELLER_RESPONSE_JSON);
    }

    @Test
    @SneakyThrows
    public void deleteUserWithCorrectCredentials_returnHttp200() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void deleteUserWithCorrectCredentialsAndRetryRequest_returnHttp401() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void updateUserWithCorrectCredentialsWithInvalidDeposit_returnHttp400() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users")
                        .content(USER1_UPDATE_WRONG_DEPOSIT_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                            Base64Utils
                                .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void updateUserWithCorrectCredentialsAndValidDeposit_returnHttp200() {
        signUp(USER_SELLER_JSON);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users")
                            .content(USER1_UPDATE_VALID_DEPOSIT_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(USER1_UPDATE_RESPONSE_JSON);
    }

    @Test
    @SneakyThrows
    public void updateUserWithCorrectCredentialsAndValidDepositAndRetryRequest_returnHttp401() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users")
                        .content(USER1_UPDATE_VALID_DEPOSIT_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users")
                        .content(USER1_UPDATE_VALID_DEPOSIT_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void depositCoinWithCorrectCredentialsAndValidCoin_returnHttp200() {
        signUp(USER_BUYER_JSON);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/deposit")
                        .content(DEPOSIT_VALID_COIN_REQUEST_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_BUYER_NAME + ":" + USER_BUYER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(DEPOSIT_VALID_COIN_RESPONSE_JSON);
    }

    @Test
    @SneakyThrows
    public void depositCoinWithCorrectCredentialsAndInvalidCoin_returnHttp400() {
        signUp(USER_BUYER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/deposit")
                        .content(DEPOSIT_INVALID_COIN_REQUEST_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_BUYER_NAME + ":" + USER_BUYER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void depositCoinWithWrongCredentials_returnHttp403() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/deposit")
                        .content(DEPOSIT_INVALID_COIN_REQUEST_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void resetDepositWithCorrectCredentials_returnHttp200() {
        signUp(USER_BUYER_JSON);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/deposit")
                        .content(DEPOSIT_VALID_COIN_REQUEST_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_BUYER_NAME + ":" + USER_BUYER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/deposit/reset")
                            .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                    Base64Utils
                                        .encodeToString((USER_BUYER_NAME + ":" + USER_BUYER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(USER_BUYER_RESPONSE_JSON);
    }

    @Test
    @SneakyThrows
    public void resetDepositWithWrongCredentials_returnHttp403() {
        signUp(USER_SELLER_JSON);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/deposit/reset")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64Utils
                                        .encodeToString((USER_SELLER_NAME + ":" + USER_SELLER_PASSWORD)
                                        .getBytes())))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }
}