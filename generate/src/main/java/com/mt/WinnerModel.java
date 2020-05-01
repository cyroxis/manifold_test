package com.mt;

import manifold.api.fs.IFile;
import manifold.api.host.IManifoldHost;
import manifold.api.type.AbstractSingleFileModel;

import java.util.Set;

public class WinnerModel extends AbstractSingleFileModel {
    public WinnerModel(IManifoldHost host, String fqn, Set<IFile> files) {
        super(host, fqn, files);
    }
}
