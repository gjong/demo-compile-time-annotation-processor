package com.github.gjong.cdi.context;

import com.github.gjong.cdi.BeanProvider;

import java.util.ArrayList;
import java.util.List;
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
    public <T> T provide(Class<T> clazz) {
        return knownBeans.stream()
                .filter(b -> clazz.isAssignableFrom(b.type()))
                .findFirst()
                .map(bean -> bean.create(this))
                .map(clazz::cast)
                .orElseThrow(() -> new IllegalArgumentException("No bean found for " + clazz));
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
