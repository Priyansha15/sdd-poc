package com.example.sddpoc.contractor;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SavedContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SavedContractorRepository repository;

    private String requestJson(String name, String phone, String trade) throws Exception {
        return objectMapper.writeValueAsString(new SavedContractorRequest(name, phone, trade));
    }

    @Test
    void ac1_createReturns201WithGeneratedId() throws Exception {
        mockMvc.perform(post("/api/saved-contractors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("Jane Roofer", "+1 (512) 555-0100", "roofing")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Jane Roofer"))
                .andExpect(jsonPath("$.phone").value("+1 (512) 555-0100"))
                .andExpect(jsonPath("$.trade").value("roofing"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void ac2_listReturnsMostRecentFirst() throws Exception {
        Instant now = Instant.now();
        repository.save(SavedContractor.builder()
                .name("Older Contractor").phone("555-0100").trade("plumbing")
                .createdAt(now.minus(1, ChronoUnit.HOURS)).build());
        repository.save(SavedContractor.builder()
                .name("Newer Contractor").phone("555-0101").trade("electrical")
                .createdAt(now).build());

        mockMvc.perform(get("/api/saved-contractors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Newer Contractor"))
                .andExpect(jsonPath("$[1].name").value("Older Contractor"));
    }

    @Test
    void ac3_deleteRemovesItFromSubsequentList() throws Exception {
        SavedContractor saved = repository.save(SavedContractor.builder()
                .name("To Delete").phone("555-0102").trade("hvac")
                .createdAt(Instant.now()).build());

        mockMvc.perform(delete("/api/saved-contractors/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/saved-contractors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void ac4_deleteOfUnknownIdReturns404() throws Exception {
        mockMvc.perform(delete("/api/saved-contractors/{id}", 999_999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void ac5_blankNameReturns400() throws Exception {
        mockMvc.perform(post("/api/saved-contractors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("", "555-0100", "plumbing")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac5_blankPhoneReturns400() throws Exception {
        mockMvc.perform(post("/api/saved-contractors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("Jane Roofer", "", "plumbing")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac5_blankTradeReturns400() throws Exception {
        mockMvc.perform(post("/api/saved-contractors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("Jane Roofer", "555-0100", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac6_malformedPhoneReturns400() throws Exception {
        mockMvc.perform(post("/api/saved-contractors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("Jane Roofer", "not-a-phone-number", "plumbing")))
                .andExpect(status().isBadRequest());
    }
}
