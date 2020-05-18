package com.mt;

import manifold.api.host.IModule;
import manifold.internal.javac.InMemoryClassJavaFileObject;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.mt.PrintUtil.print;

public class InMemoryClassLoader extends URLClassLoader {

    private static final List<String> COMPILER_ARGS = Arrays
            .asList("-source", "8", "-g", "-nowarn", "-Xlint:none", "-proc:none", "-parameters");
    private final IModule module;

    public InMemoryClassLoader(IModule module) {
        super(new URL[] {}, module.getHost().getActualClassLoader());
        this.module = module;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return super.loadClass(name, true);
        } catch (ClassNotFoundException e) {
            return compileClass(name);
        }
    }

    private Class<?> compileClass(String name) {
        print("loading.... %s", name);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        InMemoryClassJavaFileObject result = module.getHost().getJavaParser().compile(name, COMPILER_ARGS, collector);
        for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
            print("COMPILATION ISSUE: %s", diagnostic.getMessage(Locale.getDefault()));
        }

        byte[] bytes = result.getBytes();
        print("loaded.... %s", name);
        return defineClass(name, bytes, 0, bytes.length);
    }
}
