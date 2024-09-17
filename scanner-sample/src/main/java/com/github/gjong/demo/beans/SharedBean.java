package com.github.gjong.demo.beans;

import com.github.gjong.cdi.Bean;
import com.github.gjong.cdi.Inject;
import com.github.gjong.cdi.Qualifier;

@Bean(name = "sharedBean")
public class SharedBean {

    private final BeanInterface dependentBean;

    @Inject
    public SharedBean(@Qualifier("dependentBean") BeanInterface dependentBean) {
        this.dependentBean = dependentBean;
    }

    public String computeName() {
        return "Shared bean with " + dependentBean.getBeanName();
    }
}
