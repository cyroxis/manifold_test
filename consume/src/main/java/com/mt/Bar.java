package com.mt;

import com.at.BuilderProperty;
import com.at.MarkedField;
import com.at.TemplateWith;
import com.mt.extensions.java.FooCreator;
import com.mt.*;

import java.lang.annotation.Native;

@TemplateWith
public class Bar {

    @MarkedField(4)
    private String foo;

}
