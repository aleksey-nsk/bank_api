package com.example.bank_api;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Log4j2
public class SpringProfileTest {

    @Autowired
    private Environment environment;

    @Test
    public void springProfileTest() {
        for (String profileName : environment.getActiveProfiles()) {
            log.debug("Активный профиль: " + profileName);
        }

        String expected = "test";
        String actual = environment.getActiveProfiles()[0];
        Assertions.assertEquals(expected, actual);
    }
}
