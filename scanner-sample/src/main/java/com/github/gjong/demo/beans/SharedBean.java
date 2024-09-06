package com.github.gjong.demo.beans;

import com.github.gjong.cdi.Bean;
import com.github.gjong.cdi.Inject;

@Bean(name = "sharedBean")
public class SharedBean {

    private final DependentBean dependentBean;

    @Inject
    public SharedBean(DependentBean dependentBean) {
        this.dependentBean = dependentBean;
    }

    public String computeName() {
        return "Shared bean with " + dependentBean.getBeanName();
    }
}
