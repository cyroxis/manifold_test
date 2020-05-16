package com.at;

import manifold.api.gen.AbstractSrcClass;
import manifold.api.gen.SrcClass;
import manifold.api.gen.SrcMethod;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.PrintUtil.print;

public class TypeCreatorProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return typeNames(TemplateWith.class);
    }

    private Set<String> typeNames(Class... cs) {
        return Arrays.stream(cs)
                .map(Class::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            print("Processing Annotation: %s", annotation);
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind().equals(ElementKind.CLASS)) {
                    createClass(roundEnv, (TypeElement) element);
                } else {
                    print("Unexpected element %s", element);
                }
            }
        }

        return true;
    }

    private void createClass(RoundEnvironment roundEnv, TypeElement sourceType) {
        String name = sourceType.getQualifiedName().toString() + "Winner";
        SrcClass srcClass = new SrcClass(name, SrcClass.Kind.Class);

        for (Element enclosedElement : sourceType.getEnclosedElements()) {
            if (enclosedElement.getKind().equals(ElementKind.FIELD)) {
                VariableElement field = (VariableElement) enclosedElement;
                if (isAnnotatedWith(field, MarkedField.class)) {
                    print("ee %s", field.asType());
                }
            }
        }

    }

    private boolean isAnnotatedWith(Element element, Class<?> annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotation.getCanonicalName().equals(annotation.getName())) {
                return true;
            }
        }

        return false;
    }



    @SuppressWarnings("unchecked")
    private <T> T getAnnotationAttribute(Element element, AnnotationMirror annotationMirror, String name) {
        return (T) annotationMirror.getElementValues().entrySet().stream()
                .filter(entry -> name.equals(entry.getKey().getSimpleName().toString()))
                .map(entry -> entry.getValue().getValue())
                .findFirst()
                .orElseGet(() -> {
                    processingEnv.getMessager()
                            .printMessage(Kind.ERROR, "Attribute [" + name + "] not found on", element, annotationMirror);
                    return null;
                });
    }


}
