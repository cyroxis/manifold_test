package com.mt;

import manifold.api.fs.IFile;
import manifold.api.gen.SrcClass;
import manifold.api.host.IModule;
import manifold.api.type.IModel;
import manifold.api.type.JavaTypeManifold;
import manifold.api.util.StreamUtil;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class AnnotationTypeManifold<M extends IModel> extends JavaTypeManifold<M> {

    private static final Set<String> FILE_EXTENSIONS = new HashSet<>(Arrays.asList("java", "class"));

    @Override
    public void init(IModule module) {
        init(module, (fqn, files) -> createModel(module, fqn, files));
    }

    @Override
    public boolean handlesFileExtension(String s) {
        return FILE_EXTENSIONS.contains(s.toLowerCase());
    }

    @Override
    public boolean handlesFile(IFile file) {
        return isAnnotatedWith(file, getTargetAnnotation());
    }

    protected abstract Class getTargetAnnotation();

    protected boolean isAnnotatedWith(IFile file, Class type) {
        try {
            if (file.getExtension().equalsIgnoreCase("java")) {
                String content = StreamUtil.getContent(new InputStreamReader(file.openInputStream(), UTF_8));
                if (content.contains("@" + type.getSimpleName()) && content.contains(type.getPackage().getName())) {
                    return true;
                }
            } else {
                String content = StreamUtil.getContent(new InputStreamReader(file.openInputStream(), UTF_8));
                if (content.contains(type.getName().replace('.', '/'))) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isInnerType(String topLevel, String relativeInner) {
        return false;
    }

    @Override
    public String getTypeNameForFile(String defaultFqn, IFile file) {
        return defaultFqn + getClassNameSuffix(defaultFqn, file);
    }

    protected abstract String getClassNameSuffix(String defaultFqn, IFile file);

    protected abstract M createModel(IModule module, String fqn , Set<IFile> files);

    @Override
    protected String contribute( Location location, String topLevelFqn, boolean genStubs, String existing, M model, DiagnosticListener<JavaFileObject> errorHandler ) {
        return generateClass(model).render().toString();
    }

    protected abstract SrcClass generateClass(M m);

}
