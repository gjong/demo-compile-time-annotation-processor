package com.github.gjong.processor;

import com.github.gjong.cdi.Bean;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public record BeanDefinition(TypeElement type, List<TypeMirror> dependencies) {

    public Bean getBean() {
        return type.getAnnotation(Bean.class);
    }
}
