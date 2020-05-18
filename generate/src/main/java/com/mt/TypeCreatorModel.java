package com.mt;

import manifold.api.fs.IFile;
import manifold.api.gen.SrcClass;
import manifold.api.host.IManifoldHost;
import manifold.api.type.AbstractSingleFileModel;

import java.util.Set;

public class TypeCreatorModel extends AbstractSingleFileModel {

    private final TypeCreator typeCreator;

    public TypeCreatorModel(IManifoldHost host, String fqn, TypeCreator sourceFqn, Set<IFile> files) {
        super(host, fqn, files);
        this.typeCreator = sourceFqn;
    }

    public SrcClass generateClass(InMemoryClassLoader classLoader) {
        Class<?> type = classLoader.loadClass(getFqn());
        return typeCreator.generateClass(getFqn(), type);
    }
}
