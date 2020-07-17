package com.example.miro.demo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.immutables.value.Value;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Optional;

import static com.example.miro.demo.WidgetApp.CLOCK;
import static com.example.miro.demo.WidgetApp.getWidgetRepository;
import static com.example.miro.demo.WidgetCriteria.widget;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

/**
 * Different mixins with descriptive names
 */
public record Mixins() { }

interface WithSuppliedValidator {

    @Value.Lazy
    @Nullable
    @JsonIgnore
    default Validator getValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Value.Check
    @JsonIgnore
    default void check() {
        @Nullable
        var validator = getValidator();
        if (validator == null) {
            return;
        }
        var violations =
            validator.validate(this, Default.class, WithSuppliedValidator.class);
        assert violations.isEmpty() : violations;
    }
}

/**
 * Audit normalization
 */
interface WithAudit {

    //hide to allow typed method calls
    <ID extends Serializable & Comparable<ID>> ID id();

    /**
     * We could provide
     */
    @JsonProperty(access = READ_ONLY)
    @Nullable
    @Value.Derived
    default Instant lastModificationDate() {
        var date = getWidgetRepository()
            .find(widget.id.is(Optional.ofNullable(id())))
            .select(widget.lastModificationDate)
            .limit(1)
            .fetch();
        return date.isEmpty() ? null : date.get(0);
    }

    /**
     * Immutable copy of
     */
    @SuppressWarnings("unchecked")
    static <T extends Temporal> T immutableCopyOf(T audited) {
        return audited != null ? audited : (T) Instant.now(CLOCK);
    }
}

/**
 * Needed for proper type reification at implementation side
 */
interface Defaults {

    /*
     * (non-Javadoc)
     * no-op
     */
    static <T> T immutableCopyOf(T any) {
        return any;
    }
}
