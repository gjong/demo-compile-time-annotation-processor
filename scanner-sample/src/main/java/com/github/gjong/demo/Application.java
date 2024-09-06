package com.github.gjong.demo;

import com.github.gjong.cdi.BeanContextFactory;
import com.github.gjong.demo.beans.SharedBean;

public class Application {

    public static void main(String[] args) {
        var beanContext = BeanContextFactory.getOrCreate();
        var injectedBean = beanContext.provide(SharedBean.class);

        System.out.println(injectedBean.computeName());
    }
}
