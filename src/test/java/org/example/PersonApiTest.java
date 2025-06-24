package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersonApiTest {

    static class Person {
        public Long id;
        public String firstName;
        public String lastName;
        public int age;
        public String email;
        public String phone;
        public String address;
        public boolean active;
    }

    @Test
    void testGetPersonById() throws Exception {
        // 1. Postavenie URI
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8081/api/people")
                .path("/{id}")
                .buildAndExpand("1")
                .toUri();

        // 2. Príprava Basic Auth hlavičky
        String username = "admin";
        String password = "password";
        String plainCreds = username + ":" + password;
        String base64Creds = Base64.getEncoder()
                .encodeToString(plainCreds.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Creds);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        HttpEntity<String> request = new HttpEntity<>(headers);

        // 3. Zavolanie API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        // 4. Kontrola status kódu
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 5. Parsovanie JSON
        ObjectMapper objectMapper = new ObjectMapper();
        Person person = objectMapper.readValue(response.getBody(), Person.class);

        // 6. Kontrola údajov
        assertEquals(1L, person.id);
        assertEquals("Pavol", person.firstName);
        assertEquals("Martinák", person.lastName);
        assertEquals(30, person.age);
        assertEquals("pavol.martinak@example.com", person.email);
        assertEquals("+421123456789", person.phone);
        assertEquals("Bratislava", person.address);
        assertTrue(person.active);

    }
}
