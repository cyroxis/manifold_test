package com.mt;

import com.at.TemplateWith;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SimpleTreeVisitor;
import manifold.api.fs.IFile;
import manifold.api.gen.SrcClass;
import manifold.api.gen.SrcMethod;
import manifold.api.host.IModule;
import manifold.api.type.JavaTypeManifold;
import manifold.api.util.StreamUtil;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mt.PrintUtil.print;
import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class TypeCreatorManifold extends JavaTypeManifold<TypeCreatorModel> {

    private static final Set<String> FILE_EXTENSIONS = new HashSet<>(Arrays.asList("java", "class"));
    private final Map<String, String> sourceTypeFqnCache = new HashMap<>();

    @Override
    public void init(IModule module) {
        init(module, (fqn, files) -> new TypeCreatorModel(module.getHost(), fqn, sourceTypeFqnCache.get(fqn), files));
    }

    @Override
    public boolean handlesFileExtension(String s) {
        return FILE_EXTENSIONS.contains(s.toLowerCase());
    }

    @Override
    public boolean handlesFile(IFile file) {
        boolean b = isAnnotatedWith(file, TemplateWith.class);
        if (b) {
            print("handlesFile %s - %b", file.getName(), b);
        }
        return b;
    }

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
    public String getTypeNameForFile(String fqn, IFile file) {
        String generatedType = getGeneratedTypeName(fqn, file);
        sourceTypeFqnCache.put(generatedType, fqn);
        return generatedType;
    }

    public abstract String getGeneratedTypeName(String fqn, IFile file);

    @Override
    protected String contribute(Location location, String topLevelFqn, boolean genStubs, String existing, TypeCreatorModel model,
            DiagnosticListener<JavaFileObject> errorHandler) {

        DiagnosticCollector<JavaFileObject> errCollector = new DiagnosticCollector<>();
        List<CompilationUnitTree> trees = new ArrayList<>();

        boolean success = model.getHost().getJavaParser().parseType(model.getSourceTypeFqn(), trees, new DiagnosticCollector<>());
        errCollector.getDiagnostics().forEach(errorHandler::report);

        if (success) {
            SrcClass srcClass = generateClass(model, trees.first(), errorHandler);
            return srcClass.render().toString();
        }

        return null;
    }

    protected abstract SrcClass generateClass(TypeCreatorModel model, CompilationUnitTree tree, DiagnosticListener<JavaFileObject> errorHandler);

    @Override
    public void clear() {
        sourceTypeFqnCache.clear();
        super.clear();
    }
}
