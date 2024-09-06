package com.github.gjong.cdi.context;

import com.github.gjong.cdi.BeanProvider;

public interface SingletonProvider <T> {
    T provide(BeanProvider provider);
}
