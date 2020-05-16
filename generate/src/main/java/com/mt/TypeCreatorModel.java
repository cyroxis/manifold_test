package com.mt;

import manifold.api.fs.IFile;
import manifold.api.host.IManifoldHost;
import manifold.api.type.AbstractSingleFileModel;

import java.util.Set;

public class TypeCreatorModel extends AbstractSingleFileModel {

    private final String sourceTypeFqn;

    public TypeCreatorModel(IManifoldHost host, String fqn, String sourceTypeFqn, Set<IFile> files) {
        super(host, fqn, files);
        this.sourceTypeFqn = sourceTypeFqn;
    }

    public String getSourceTypeFqn() {
        return sourceTypeFqn;
    }
}
