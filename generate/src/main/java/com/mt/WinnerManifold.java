package com.mt;

import com.at.MarkedField;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import manifold.api.fs.IFile;
import manifold.api.gen.SrcClass;
import manifold.api.gen.SrcMethod;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.mt.PrintUtil.print;

public class WinnerManifold extends TypeCreatorManifold {

    @Override
    public String getGeneratedTypeName(String fqn, IFile file) {
        return fqn + "Winner";
    }

    @Override
    public SrcClass generateClass(TypeCreatorModel model, CompilationUnitTree tree, DiagnosticListener<JavaFileObject> errorHandler) {

        SrcClass srcClass = new SrcClass(model.getFqn(), SrcClass.Kind.Class);

        tree.accept(new GenerateMethods(), srcClass);

        return srcClass;
    }

    private static class GenerateMethods extends TreeScanner<Void, SrcClass> {

        private List<String> imports = new ArrayList<>();

        @Override
        public Void visitImport(ImportTree node, SrcClass srcClass) {
            imports.add(node.getQualifiedIdentifier().toString());
            return super.visitImport(node, srcClass);
        }

        @Override
        public Void visitVariable(VariableTree node, SrcClass srcClass) {
            Boolean annotated = node.accept(new FindAnnotation(MarkedField.class), imports);

            if (annotated) {
                String name = node.getName().toString();
                String type = node.getType().toString();
                print("%s is annotated", node.getName());

                print("type %s", node.getType());

                srcClass.addMethod(new SrcMethod()
                        .modifiers(Modifier.PUBLIC)
                        .name(name)
                        .returns(String.class)
                        .body("return \"winning\";"));
            }

            return null;
        }
    }

    private static class FindAnnotation extends TreeScanner<Boolean, List<String>> {

        private final Class<? extends Annotation> type;

        private FindAnnotation(Class<? extends Annotation> type) {
            this.type = type;
        }

        @Override
        public Boolean visitAnnotation(AnnotationTree node, List<String> imports) {
            String name = node.getAnnotationType().toString();

            print("checking %s in %s", type, imports);
            return imports.stream()
                    .map(s -> s.replace("*",name))
                    .anyMatch(s -> s.equalsIgnoreCase(type.getName()));
        }

        @Override
        public Boolean reduce(Boolean r1, Boolean r2) {
            return (r1 != null && r1) || (r2 != null && r2);
        }
    }

}
