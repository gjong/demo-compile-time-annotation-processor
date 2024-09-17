package com.github.gjong.cdi.context;

import com.github.gjong.cdi.BeanProvider;

public interface CdiBean<T> {

    /**
     * Create a new instance of the bean.
     *
     * @param provider the provider to use when needing to inject other beans.
     * @return the new instance of the bean.
     */
    T create(BeanProvider provider);

    /**
     * Get the type of the bean.
     *
     * @return the type of the bean.
     */
    Class<T> type();

    /**
     * Get the qualifier name of the bean.
     *
     * @return the qualifier name of the bean.
     */
    String qualifier();

}
