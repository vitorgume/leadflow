package com.gumeinteligencia.api_intermidiaria;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiIntermidiariaApplicationTest {

    @Test
    void mainDeveConfigurarTimezoneEInicializarAplicacao() {
        TimeZone timezoneOriginal = TimeZone.getDefault();

        try (MockedStatic<SpringApplication> spring = Mockito.mockStatic(SpringApplication.class)) {
            spring.when(() -> SpringApplication.run(ApiIntermidiariaApplication.class))
                    .thenReturn(null);

            assertDoesNotThrow(() -> ApiIntermidiariaApplication.main(new String[]{}));

            spring.verify(() -> SpringApplication.run(ApiIntermidiariaApplication.class));
            assertEquals(TimeZone.getTimeZone("America/Sao_Paulo"), TimeZone.getDefault());
        } finally {
            TimeZone.setDefault(timezoneOriginal);
        }
    }
}
