package com.mt;

import manifold.api.gen.SrcClass;

import java.util.HashSet;
import java.util.Set;

public interface TypeCreator {

    String generateTypeName(String sourceFqn);

    SrcClass generateClass(String fqn, Class<?> source);
}
