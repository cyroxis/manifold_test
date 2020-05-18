package com.mt;

import manifold.api.fs.IFile;
import manifold.api.host.IModule;
import manifold.api.host.RefreshKind;
import manifold.api.type.JavaTypeManifold;
import manifold.api.util.StreamUtil;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mt.PrintUtil.print;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TypeTemplateManifold extends JavaTypeManifold<TypeCreatorModel> {

    private static final Set<String> FILE_EXTENSIONS = new HashSet<>(Arrays.asList("java", "class"));
    private final Map<String, List<TypeCreator>> typeCreatorCache = new HashMap<>();
    private final Map<String, TypeCreator> typeCreatorLookup = new HashMap<>();
    private InMemoryClassLoader classLoader;

    @Override
    public void init(IModule module) {
        init(module, (fqn, files) -> new TypeCreatorModel(module.getHost(), fqn, typeCreatorLookup.get(fqn), files));
        classLoader = new InMemoryClassLoader(module);
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
        return namesFor(fqn, file)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected Set<String> getAdditionalTypes(String fqn, IFile file) {
        return namesFor(fqn, file)
                .skip(1)
                .collect(Collectors.toSet());
    }

    private Stream<String> namesFor(String fqn, IFile file) {
        return getTypeCreators(fqn, file).stream()
                .map(typeCreator -> {
                    String generatedFqn = typeCreator.generateTypeName(fqn);
                    typeCreatorLookup.put(generatedFqn, typeCreator);
                    return generatedFqn;
                });
    }

    private List<TypeCreator> getTypeCreators(String fqn, IFile file) {
        if (typeCreatorCache.containsKey(fqn)) {
            return typeCreatorCache.get(fqn);
        }

        List<TypeCreator> typeCreators = new ArrayList<>();
        // Add empty list to protect against recursive name lookups during next steps
        typeCreatorCache.put(fqn, typeCreators);

        Class<?> type = classLoader.loadClass(fqn);
        for (TemplateWith templateWith : type.getAnnotationsByType(TemplateWith.class)) {
            //TODO add error handling and logging
            TypeCreator typeCreator = templateWith.value().newInstance();
            typeCreators.add(typeCreator);
        }

        super.refreshedFile(file, null, RefreshKind.MODIFICATION);

        return typeCreatorCache.get(fqn);
    }

    @Override
    protected String contribute(Location location, String topLevelFqn, boolean genStubs, String existing, TypeCreatorModel model,
            DiagnosticListener<JavaFileObject> errorHandler) {
        print("generate");
        return model.generateClass(classLoader).render().toString();
    }

    @Override
    public RefreshKind refreshedFile(IFile file, String[] types, RefreshKind kind) {
        classLoader = new InMemoryClassLoader(getModule());
        return super.refreshedFile(file, types, kind);
    }

    @Override
    public void clear() {
        typeCreatorCache.clear();
        typeCreatorLookup.clear();
        classLoader = null;
        super.clear();
    }
}
