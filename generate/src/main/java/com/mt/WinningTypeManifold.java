package com.mt;

import manifold.api.fs.IFile;
import manifold.api.gen.SrcClass;
import manifold.api.gen.SrcMethod;
import manifold.api.host.IModule;

import java.lang.reflect.Modifier;
import java.util.Set;

public class WinningTypeManifold extends AnnotationTypeManifold<WinnerModel> {

    @Override
    protected Class getTargetAnnotation() {
        return Winner.class;
    }

    @Override
    protected String getClassNameSuffix(String defaultFqn, IFile file) {
        return "Winner";
    }

    @Override
    protected WinnerModel createModel(IModule module, String fqn, Set<IFile> files) {
        return new WinnerModel(module.getHost(), fqn, files);
    }

    @Override
    protected SrcClass generateClass(WinnerModel model) {
        SrcClass srcClass = new SrcClass(model.getFqn(), SrcClass.Kind.Class);
        srcClass.addMethod(new SrcMethod()
            .modifiers(Modifier.PUBLIC)
            .name("win")
            .returns(String.class)
            .body("return \"winning\";"));

        return srcClass;
    }
}
