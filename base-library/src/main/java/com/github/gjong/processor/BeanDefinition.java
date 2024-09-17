package com.github.gjong.processor;

import com.github.gjong.cdi.Bean;
import com.github.gjong.cdi.Qualifier;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public record BeanDefinition(TypeElement type, List<ArgumentDefinition> dependencies) {

    public record ArgumentDefinition(TypeMirror type, Qualifier qualifier) {
    }

    public Bean getBean() {
        return type.getAnnotation(Bean.class);
    }
}
