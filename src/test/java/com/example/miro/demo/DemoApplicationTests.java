package com.example.miro.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedInteger;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WidgetApp.class)
@ExtendWith(SoftAssertionsExtension.class)
class DemoApplicationTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    WidgetApp app;

    @Autowired
    ObjectMapper mapper;

    JacksonTester<Widget> json;

    @BeforeEach
    void setup() {
        // this helps to bootstrap JacksonTester without need of @JsonTest
        JacksonTester.initFields(this, mapper);
    }


    @Test
    void providesValidationMessage() {
        assertThatThrownBy(() -> getRandomDetailedWidget().withHeight(null))
            .isInstanceOfAny(java.lang.AssertionError.class, ConstraintViolationException.class);
    }

    @Test
    void weCanConstructInvalidInstance() {
        assertThatThrownBy(() -> ModifiableWidget.create().from(getRandomDetailedWidget()).setHeight(null).toImmutable())
            .isInstanceOfAny(AssertionError.class, ConstraintViolationException.class)
            .hasMessageMatching(".*not.*null.*");
    }

    @Test
    void providesValidationMessageRest() throws Exception {
        mvc.perform(post("/api/create")
            /*language=JSON*/
            .content("""
                    {
                        "id":" """ + UUID.randomUUID() + """
                        ","x":""" + null + """
                        ,"y":""" + ThreadLocalRandom.current().nextInt(400) + """
                        ,"z":""" + ThreadLocalRandom.current().nextInt(400) + """
                        ,"width":""" + ThreadLocalRandom.current().nextInt(400) + """
                        ,"height":""" + ThreadLocalRandom.current().nextInt(400) + """
                    }""")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createsRandomValidWidget() throws Exception {
        var w = getRandomDetailedWidget();
        mvc.perform(post("/api/create")
            .content(json.write(w).getJson())
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(matchAll(
                jsonPath("$.id", is(w.id().toString()), String.class),
                jsonPath("$.x", is(w.x()), Integer.class),
                jsonPath("$.y", is(w.y()), Integer.class),
                jsonPath("$.z", is(w.z()), Integer.class),
                jsonPath("$.width", is(w.width().intValue()), Integer.class),
                jsonPath("$.height", is(w.height().intValue()), Integer.class)
            ));
    }

    @Test
    void createsGetsByIdAndDeletesCorrectly() {

        var stubs =
            IntStream.rangeClosed(1, 10)
                .mapToObj(__ -> getRandomDetailedWidget())
                .collect(Collectors.toUnmodifiableMap(Widget::id, Function.identity()));

        assertThat(
            stubs.values().stream()
                .map(app::create)
                .map(Widget::id)
                .map(app::getById)
                .flatMap(Optional::stream)
        ).containsExactlyInAnyOrderElementsOf(stubs.values());

        assertThat(
            stubs.keySet().stream()
            .map(app::delete)
            .flatMapToLong(OptionalLong::stream)
        ).hasSize(stubs.keySet().size())
        .containsOnly(1L);

        assertThat(
            stubs.keySet().stream()
                .map(app::getById)
                .flatMap(Optional::stream)
        ).isEmpty();
    }

    static ImmutableWidget getRandomDetailedWidget() {
        return (ImmutableWidget)
            ImmutableWidget.of(
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextInt(400),
                ThreadLocalRandom.current().nextInt(400),
                ThreadLocalRandom.current().nextInt(400),
                UnsignedInteger.valueOf(ThreadLocalRandom.current().nextLong(5_000L)),
                UnsignedInteger.valueOf(ThreadLocalRandom.current().nextLong(5_000L))
            );
    }

    static ImmutableWidget getRandomMinimalWidget() {
        return (ImmutableWidget)
            ImmutableWidget.of(
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextInt(400),
                ThreadLocalRandom.current().nextInt(400),
                ThreadLocalRandom.current().nextInt(400),
                UnsignedInteger.valueOf(ThreadLocalRandom.current().nextLong(5_000L)),
                UnsignedInteger.valueOf(ThreadLocalRandom.current().nextLong(5_000L))
            );
    }


    @TestConfiguration
    static class LogAll {
        @Bean
        public LoggingAspect loggingAspect(Environment env) {

            return new LoggingAspect(env);
        }
    }
}
