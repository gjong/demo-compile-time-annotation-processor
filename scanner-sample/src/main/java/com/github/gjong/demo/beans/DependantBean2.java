package com.github.gjong.demo.beans;

import com.github.gjong.cdi.Bean;

@Bean(name = "dependantBean2", shared = false)
public class DependantBean2 implements BeanInterface {

    public String getBeanName() {
        return "DependantBean2";
    }

}
