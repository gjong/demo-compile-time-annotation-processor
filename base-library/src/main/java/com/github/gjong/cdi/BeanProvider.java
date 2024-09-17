package com.github.gjong.cdi;

public interface BeanProvider {

    <T> T provide(String qualifier);

    <T> T provide(Class<T> clazz);

    <T> Iterable<T>provideAll(Class<T> clazz);
}
