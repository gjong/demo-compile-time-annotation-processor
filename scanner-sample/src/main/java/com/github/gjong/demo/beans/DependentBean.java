package com.github.gjong.demo.beans;

import com.github.gjong.cdi.Bean;

@Bean(name = "dependentBean", shared = false)
public class DependentBean {

    public String getBeanName() {
        return "dependentBean";
    }
}
