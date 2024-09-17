package com.github.gjong.cdi.context;

import com.github.gjong.cdi.BeanProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

public class BeanContext implements BeanProvider {
    private final List<CdiBean<?>> knownBeans = new ArrayList<>();

    public void initialize() {
        ServiceLoader.load(CdiBean.class)
                .forEach(knownBeans::add);
    }

    public void register(CdiBean<?> bean) {
        knownBeans.add(bean);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provide(String qualifier) {
        return (T) knownBeans.stream()
                .filter(b -> Objects.equals(qualifier, b.qualifier()))
                .findFirst()
                .map(b -> b.create(this))
                .orElseThrow(() -> new IllegalArgumentException("No bean found for " + qualifier));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provide(Class<T> clazz) {
        var matchingBeans = knownBeans.stream()
                .filter(b -> clazz.isAssignableFrom(b.type()))
                .toList();
        if (matchingBeans.size() > 1) {
            throw new IllegalArgumentException("Multiple beans found for " + clazz);
        } else if (matchingBeans.isEmpty()) {
            throw new IllegalArgumentException("No bean found for " + clazz);
        }

        return (T) matchingBeans.getFirst()
                .create(this);
    }

    @Override
    public <T> Iterable<T> provideAll(Class<T> clazz) {
        return knownBeans.stream()
                .filter(bean -> clazz.isAssignableFrom(bean.type()))
                .map(bean -> bean.create(this))
                .map(clazz::cast)
                .toList();
    }
}
