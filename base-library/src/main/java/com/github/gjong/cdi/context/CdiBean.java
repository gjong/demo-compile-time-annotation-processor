package com.github.gjong.cdi.context;

import com.github.gjong.cdi.BeanProvider;

public interface CdiBean<T> {
    T create(BeanProvider provider);

    Class<T> type();
}
