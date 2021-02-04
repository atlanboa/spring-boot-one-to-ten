package com.plee.auth.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "prod")
public class DatabasePropertiesProdIntegrationTest {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Test
    public void whenSimplePropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("prod", databaseProperties.getUsername(),
                "Incorrectly bound Username property");
        Assertions.assertEquals("prodpw", databaseProperties.getPassword(),
                "Incorrectly bound Password property");
    }

    @Test
    public void whenNestedPropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("prodip", databaseProperties.getServer().getIp(),
                "Incorrectly bound Server IP nested property");
        Assertions.assertEquals(1234, databaseProperties.getServer().getPort(),
                "Incorrectly bound Server Port nested property");
    }
}