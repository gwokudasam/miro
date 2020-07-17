@ImmutablesStyle
@InjectComponent
package com.example.miro.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.annotate.InjectAnnotation;
import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.immutables.annotate.InjectAnnotation.Where.MODIFIABLE_TYPE;
import static org.immutables.value.Value.Style.ImplementationVisibility.PUBLIC;
import static org.immutables.value.Value.Style.ValidationMethod.NONE;

@JsonSerialize
@JsonDeserialize
@Serial.Version(-1L)
@Value.Style(
    allParameters = true,
    attributeBuilderDetection = true,
    buildOrThrow = "buildOrThrow",
    clearBuilder = true,
    deepImmutablesDetection = true,
    validationMethod = NONE,
    overshadowImplementation = true,
    defaultAsDefault = true,
    visibility = PUBLIC,
    immutableCopyOfRoutines = { Defaults.class, WithAudit.class },
    defaults = @Value.Immutable(lazyhash = true)
) @interface ImmutablesStyle { }

/**
 * Default integration is also looks a bit uglier
 */
@InjectAnnotation(type = Component.class, target = MODIFIABLE_TYPE)
@InjectAnnotation(type = Scope.class, code = "(org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE)", target = MODIFIABLE_TYPE)
//@InjectAnnotation(type = JsonCreator.class, target = CONSTRUCTOR)
//@InjectAnnotation(type = JsonProperty.class, target = CONSTRUCTOR_PARAMETER)
//@InjectAnnotation(type = JsonProperty.class, target = ACCESSOR)
@interface InjectComponent { }
