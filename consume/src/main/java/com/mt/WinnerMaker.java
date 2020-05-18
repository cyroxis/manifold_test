package com.mt;

import manifold.api.gen.AbstractSrcClass.Kind;
import manifold.api.gen.SrcClass;
import manifold.api.gen.SrcMethod;

import java.lang.reflect.Modifier;

public class WinnerMaker implements TypeCreator {

    @Override
    public String generateTypeName(String sourceFqn) {
        return sourceFqn + "Winner";
    }

    @Override
    public SrcClass generateClass(String fqn, Class<?> source) {
        SrcClass srcClass = new SrcClass(fqn, Kind.Class);

        srcClass.addMethod(new SrcMethod()
                .modifiers(Modifier.PUBLIC)
                .name("win")
                .returns(String.class)
                .body("return \"winning!!!\";"));

        return srcClass;
    }
}
