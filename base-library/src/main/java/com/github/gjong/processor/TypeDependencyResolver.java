package com.github.gjong.processor;

import com.github.gjong.cdi.Inject;
import com.github.gjong.cdi.Qualifier;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.Optional;

import static javax.tools.Diagnostic.Kind.ERROR;

class TypeDependencyResolver {
    private static final IllegalStateException RESOLVE_FAILED = new IllegalStateException("Failed to resolve dependency.");

    public BeanDefinition resolve(TypeElement typeElement, Messager messager) {
        if (typeElement.getKind().isClass() && !typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
            return resolveClass(typeElement, messager);
        }

        messager.printMessage(ERROR, "Class %s must not be abstract".formatted(typeElement));
        throw RESOLVE_FAILED;
    }

    private BeanDefinition resolveClass(TypeElement typeElement, Messager messager) {
        var constructor = resolveConstructor(typeElement)
                .orElseThrow(() -> {
                    messager.printMessage(ERROR, "Class %s must have a public constructor".formatted(typeElement));
                    return RESOLVE_FAILED;
                });

        var dependencies = constructor.getParameters()
                .stream()
                .map(arg -> new BeanDefinition.ArgumentDefinition(arg.asType(), arg.getAnnotation(Qualifier.class)))
                .toList();

        return new BeanDefinition(typeElement, dependencies);
    }

    private Optional<ExecutableElement> resolveConstructor(TypeElement typeElement) {
        var constructors = ElementFilter.constructorsIn(typeElement.getEnclosedElements());
        constructors.removeIf(constructor -> constructor.getModifiers().contains(Modifier.PRIVATE));

        var noArgsConstructor = constructors.stream()
                .filter(constructor -> constructor.getParameters().isEmpty())
                .findFirst();
        var injectConstructor = constructors.stream()
                .filter(constructor -> constructor.getAnnotation(Inject.class) != null)
                .findFirst();

        return injectConstructor.or(() -> noArgsConstructor);
    }
}
