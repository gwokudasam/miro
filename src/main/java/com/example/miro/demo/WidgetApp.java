package com.example.miro.demo;

import org.immutables.criteria.inmemory.InMemoryBackend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.util.*;

import static com.example.miro.demo.WidgetCriteria.widget;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;


@RestController
@RequestMapping(value = "/api", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}, consumes = APPLICATION_JSON_VALUE)
@SpringBootApplication(proxyBeanMethods = false)
public class WidgetApp {

    //language=RegExp
    public static final String UUID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";
    public static Clock CLOCK = Clock.systemDefaultZone();
    private static WidgetRepository WIDGET_REPOSITORY;

    public static void main(String... args) {
        SpringApplication.run(WidgetApp.class, args);
    }


    @PostConstruct
    public void init() {
        setWidgetRepository(new WidgetRepository(new InMemoryBackend()));
    }

    @PostMapping("/create")
    @PutMapping("/update")
    @PatchMapping("/update")
    public Widget create(@NotNull @Valid @RequestBody Widget widget) {
        var c = WidgetCriteria.widget;

        var shiftZOrder =
            getWidgetRepository()
                .find(c.z.atLeast(widget.z()))
                .fetch()
                .stream()
                .onClose(() -> getWidgetRepository().upsert(widget));

        try (shiftZOrder) {
            shiftZOrder
                .map(ImmutableWidget::copyOf)
                .map(ImmutableWidget.class::cast)
                .map(it -> it.withZ(Objects.requireNonNull(it.z()) + 1))
                .forEach(getWidgetRepository()::update);
        }

        return getWidgetRepository().find(WidgetCriteria.widget.id.is(widget.id())).one(); //we expect preceding operation was successful otherwise exception would have occur
    }

    @GetMapping("/{id:" + UUID + "}")
    public Optional<Widget> getById(@NotNull @PathVariable UUID id) {
        return getWidgetRepository().find(widget.id.is(id)).oneOrNone();
    }

    @GetMapping("/list")
    public List<Widget> getAll() {
        return getWidgetRepository().findAll().orderBy(widget.z.desc()).fetch();
    }

    /**
     *
     * @param id of Widget to delete
     * @return Amout of deletions taken affect by this operation (supposed to be 0..1)
     */
    @DeleteMapping("/{id:" + UUID + "}")
    public OptionalLong delete(@NotNull @PathVariable UUID id) {
        return getWidgetRepository().delete(widget.id.is(id)).deletedCount();
    }


    public static WidgetRepository getWidgetRepository() {
        return Objects.requireNonNullElseGet(WIDGET_REPOSITORY, () -> setWidgetRepository(new WidgetRepository(new InMemoryBackend())));
    }

    /*
     * For testing primarily, if we want to substitute it.
     */
    public static WidgetRepository setWidgetRepository(WidgetRepository widgetRepository) {
        return WIDGET_REPOSITORY = widgetRepository;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return config -> config.findModulesViaServiceLoader(true);
    }
}
