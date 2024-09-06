package com.github.gjong.cdi;

import com.github.gjong.cdi.context.BeanContext;

public class BeanContextFactory {
    private BeanContextFactory() {
    }

    private static BeanProvider instance;
    private static ThreadLocal<BeanContext> localContext = new ThreadLocal<>();

    public static BeanProvider getOrCreate() {
        if (instance == null) {
            var beanContext = new BeanContext();
            beanContext.initialize();
            instance = beanContext;
        }
        return instance;
    }

    public static BeanProvider localContext() {
        if (localContext.get() == null) {
            localContext.set(new BeanContext());
            localContext.get().initialize();
        }

        return localContext.get();
    }
}
