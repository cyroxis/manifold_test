package com.mt;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import manifold.api.fs.IFile;
import manifold.api.fs.IResource;
import manifold.api.fs.ResourcePath;
import manifold.api.host.IModule;
import manifold.api.util.ManClassUtil;
import manifold.api.util.PathUtil;
import manifold.api.util.SourcePathUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mt.PrintUtil.print;

public class TemplateAnnotationParser {

    private final IModule module;
    private List<SourceRoot> sourceRoots;

    public TemplateAnnotationParser(IModule module) {
        this.module = module;

//        sourceRoots = module.getSourcePath().stream()
//                .map(IResource::toURI)
//                .map(Paths::get)
////                .map(x -> {
////                    print("ROOT: %s", x);
////                    return x;
////                })
//                .map(SourceRoot::new)
//                .collect(Collectors.toList());
    }

    public List<String> getTemplateTypes(String fqn, IFile file) {
        for (SourceRoot sourceRoot : sourceRoots) {
            print("PARCING: %b", sourceRoot);
            ParseResult<CompilationUnit> result = sourceRoot.tryToParse(ManClassUtil.getPackage(fqn), file.getName());
            print("PARCED: %b", result.isSuccessful());
        }

        return null;
    }
}
