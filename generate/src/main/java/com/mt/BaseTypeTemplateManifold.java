package com.mt;

import com.github.javaparser.JavaParser;
import manifold.api.fs.IFile;
import manifold.api.host.IModule;
import manifold.api.host.RefreshKind;
import manifold.api.type.ClassType;
import manifold.api.type.ContributorKind;
import manifold.api.type.ISourceKind;
import manifold.api.type.ITypeManifold;
import manifold.api.type.TypeName;
import manifold.api.util.ManClassUtil;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mt.PrintUtil.print;

public abstract class BaseTypeTemplateManifold implements ITypeManifold {

    private IModule module;
    
    @Override
    public void init(IModule iModule) {
        this.module = module;
    }

    @Override
    public IModule getModule() {
        return module;
    }

    @Override
    public ISourceKind getSourceKind() {
        return ISourceKind.Java;
    }

    @Override
    public ContributorKind getContributorKind() {
        return ContributorKind.Primary;
    }

    @Override
    public boolean isType(String fqn) {
        print("isType: %s", fqn);
        return false;
    }

    @Override
    public boolean isTopLevelType(String fqn) {
        print("isTopLevelType: %s", fqn);
        return true;
    }

    @Override
    public boolean isPackage(String fqn) {
        print("isPackage: %s", fqn);
        return false;
    }

    @Override
    public ClassType getClassType(String fqn) {
        return ClassType.JavaClass;
    }

    @Override
    public String getPackage(String fqn) {
        return ManClassUtil.getPackage(fqn);
    }

    @Override
    public Collection<String> getAllTypeNames() {
        print("getAlltypeNames");
        return Collections.emptyList();
    }

    @Override
    public Collection<TypeName> getTypeNames(String namespace) {
        print("getTypeNames: %s", namespace);
        return Collections.emptyList();
    }

    @Override
    public List<IFile> findFilesForType(String fqn) {
        print("findFilesForType: %s", fqn);
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        module = null;
    }

    @Override
    public String[] getTypesForFile(IFile iFile) {
        print("getTypesForFile: %s", iFile);
        return new String[0];
    }

    @Override
    public RefreshKind refreshedFile(IFile iFile, String[] strings, RefreshKind refreshKind) {
        return refreshKind;
    }
}
