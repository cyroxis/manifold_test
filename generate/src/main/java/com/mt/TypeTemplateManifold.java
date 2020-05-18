package com.mt;

import com.at.TemplateWith;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.validator.ProblemReporter;
import com.github.javaparser.ast.validator.VisitorValidator;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.utils.SourceRoot;
import manifold.api.fs.IFile;
import manifold.api.gen.SrcClass;
import manifold.api.host.IModule;
import manifold.api.type.JavaTypeManifold;
import manifold.api.util.ManClassUtil;
import manifold.api.util.StreamUtil;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.PrintUtil.print;
import static java.nio.charset.StandardCharsets.UTF_8;
import static manifold.api.util.ManClassUtil.getShortClassName;

public class TypeTemplateManifold extends JavaTypeManifold<TypeCreatorModel> {

    private static final Set<String> FILE_EXTENSIONS = new HashSet<>(Arrays.asList("java", "class"));
    private final Map<String, String> sourceTypeFqnCache = new HashMap<>();
    private InMemoryClassLoader inMemoryClassLoader;
    private TemplateAnnotationParser annotationParser;


    @Override
    public void init(IModule module) {
        init(module, (fqn, files) -> new TypeCreatorModel(module.getHost(), fqn, sourceTypeFqnCache.get(fqn), files));
        inMemoryClassLoader = new InMemoryClassLoader(module);
        annotationParser = new TemplateAnnotationParser(module);
    }

    @Override
    public boolean handlesFileExtension(String s) {
        return FILE_EXTENSIONS.contains(s.toLowerCase());
    }

    @Override
    public boolean handlesFile(IFile file) {
        boolean b = containsAnnotation(file, TemplateWith.class);
        if (b) {
            print("handlesFile %s - %b", file.getName(), b);
        }
        return b;
    }

    protected boolean containsAnnotation(IFile file, Class type) {
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
        if (generatedType != null) {
            sourceTypeFqnCache.put(generatedType, fqn);
        }
        return generatedType;
    }

    public String getGeneratedTypeName(String fqn, IFile file) {
        if ("java".equalsIgnoreCase(file.getExtension())) {
            return getGeneratedTypeNameFromSource(fqn, file);
        } else {
            return getGeneratedTypeNameFromClass(fqn, file);
        }
    }

    protected String getGeneratedTypeNameFromSource(String fqn, IFile file) {
//        List<String> templateTypes = annotationParser.getTemplateTypes(fqn, file);
//        print("templates: %s", templateTypes);
        String source = file.openInputStream()
                .bufferedReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));

        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> result = parser.parse(source);
        print("parced: %b", result);

        getModule().getSourcePath();
        if (result.isSuccessful()) {
            CompilationUnit cu = result.getResult()
                    .orElseThrow(IllegalStateException::new);

            for (TypeDeclaration<?> type : cu.getTypes()) {
                print("type: %s", type.getName());
                for (AnnotationExpr annotation : type.getAnnotations()) {
                    annotation.getMetaModel().getQualifiedClassName();
                    print("annotation: %s", annotation.getName());
                    print("annotation: %s", annotation.getChildNodes());
                    if (annotation.getNameAsString().equals(TemplateWith.class.getSimpleName())) {

                    }
                }
            }
        }

        return null;
    }


    protected String getGeneratedTypeNameFromClass(String fqn, IFile file) {
        return null; //TODO implement
    }

    @Override
    protected String contribute(Location location, String topLevelFqn, boolean genStubs, String existing, TypeCreatorModel model,
            DiagnosticListener<JavaFileObject> errorHandler) {
        return generateClass(model).render().toString();
    }

    protected SrcClass generateClass(TypeCreatorModel model) {
        SrcClass srcClass = new SrcClass(model.getFqn(), SrcClass.Kind.Class);
        //TODO yo
        return srcClass;
    }

    @Override
    public void clear() {
        sourceTypeFqnCache.clear();
        inMemoryClassLoader = null;
        annotationParser = null;
        super.clear();
    }
}
