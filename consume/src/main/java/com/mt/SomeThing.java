package com.mt;

import com.at.BuilderProperty;

public class SomeThing {

    private int bog;

    @BuilderProperty
    public void setBog(int bog) {
        this.bog = bog;
    }
}
