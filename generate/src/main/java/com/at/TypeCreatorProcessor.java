package com.at;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import java.util.Arrays;
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
                for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                    print("amirror: %s", annotationMirror);

                    TypeMirror result = getAnnotationAttribute(element, annotationMirror, "value");
                    print("value: %s[%s]", result, result.getClass());

                }
                print("Processing Element: %s", element);
            }
        }

        return true;
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
