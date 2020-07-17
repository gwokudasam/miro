package com.example.miro.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedInteger;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.immutables.criteria.Criteria;
import org.immutables.value.Value;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.example.miro.demo.WidgetApp.getWidgetRepository;
import static com.example.miro.demo.WidgetCriteria.widget;

@Criteria
@Criteria.Repository
@Value.Immutable                             //abstract type mapping (e.i. com.fasterxml.jackson.databind.module.SimpleModule.addAbstractTypeMapping)
@Value.Modifiable
@JsonSerialize(as = ImmutableWidget.class)//on Jackson's Typeresolver does not work that well.
@JsonDeserialize(as = ImmutableWidget.class) //annotations directly on type works better in practice.
public interface Widget extends WithSuppliedValidator, WithAudit {

    @Nullable
    @Criteria.Id
    @Override
    default UUID id() {
        return UUID.randomUUID();
    }

    @NotNull
    Integer x();

    @NotNull
    Integer y();

    @Nullable
    default Integer z() {
        return getWidgetRepository()
            .findAll()
            .orderBy(widget.z.desc())
            .select(widget.z)
            .limit(1L)
            .oneOrNone()
            .map(z -> z + 1)
            .orElse(1);
    }

    @NotNull
    UnsignedInteger width();

    @NotNull
    UnsignedInteger height();
}
