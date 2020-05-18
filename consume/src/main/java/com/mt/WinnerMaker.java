package com.mt;

import manifold.api.gen.AbstractSrcClass.Kind;
import manifold.api.gen.SrcClass;

import static com.mt.PrintUtil.print;

public class WinnerMaker implements TypeCreator {

    @Override
    public String generateTypeName(String sourceFqn) {
        return sourceFqn + "Winner";
    }

    @Override
    public SrcClass generateClass(String fqn, Class<?> source) {
        SrcClass srcClass = new SrcClass(fqn, Kind.Class);
        print("WE DO IT");
        return srcClass;
    }
}
