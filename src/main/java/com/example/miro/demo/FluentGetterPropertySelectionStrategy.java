package com.example.miro.demo;

import org.hibernate.validator.internal.properties.DefaultGetterPropertySelectionStrategy;
import org.hibernate.validator.spi.properties.ConstrainableExecutable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

public class FluentGetterPropertySelectionStrategy extends DefaultGetterPropertySelectionStrategy {

    /**
     * Ignore methods from {@link Object}
     */
    private final Set<String> methodNamesToIgnore =
        Arrays.stream(Object.class.getDeclaredMethods())
            .map(Method::getName)
            .collect(toUnmodifiableSet());

    @Override
    public Optional<String> getProperty(ConstrainableExecutable executable) {
        return Optional.ofNullable(executable)
            .filter(it -> !methodNamesToIgnore.contains(it.getName()))
            .filter(it -> void.class != it.getReturnType())
            .filter(it -> it.getParameterTypes().length <= 0)
            .map(ConstrainableExecutable::getName)
            .or(() -> super.getProperty(executable));
    }

    @Override
    public Set<String> getGetterMethodNameCandidates(String propertyName) {
        // As method name == property name, there always is just one possible name for a method
        var standardGetterMethodNameCandidates
            = super.getGetterMethodNameCandidates(propertyName);
        standardGetterMethodNameCandidates.add(propertyName);
        return Set.copyOf(standardGetterMethodNameCandidates);
    }
}
