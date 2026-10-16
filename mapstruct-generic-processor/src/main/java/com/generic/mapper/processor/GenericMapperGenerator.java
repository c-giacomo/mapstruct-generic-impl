package com.generic.mapper.processor;

import com.generic.mapper.annotation.Pojo;
import com.generic.mapper.interfaces.BaseMapper;
import com.generic.mapper.model.Item;
import com.generic.mapper.model.MethodItem;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class GenericMapperGenerator extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Types types;
    private String targetPackage;

    // LinkedHashMap per evitare duplicati tra i round
    private final Map<String, Item> items = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.filer = env.getFiler();
        this.messager = env.getMessager();
        this.elements = env.getElementUtils();
        this.types = env.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Pojo.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element candidate : roundEnv.getElementsAnnotatedWith(Pojo.class)) {
            if (!(candidate instanceof TypeElement mapper)) {
                messager.printMessage(Diagnostic.Kind.WARNING, "@Pojo must be placed on a type", candidate);
                continue;
            }

            Optional<DeclaredType> baseMapper = findBaseMapper(mapper.asType());
            if (baseMapper.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "@Pojo annotated class should implement a BaseMapper", candidate);
                continue;
            }

            List<? extends TypeMirror> args = baseMapper.get().getTypeArguments();
            if (args.size() != 2) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "BaseMapper must be parameterized with <Source, Target>", candidate);
                continue;
            }

            String mapperName = mapper.getQualifiedName().toString();
            items.put(mapperName, new Item(
                    mapperName,
                    erased(args.get(0)),
                    erased(args.get(1)),
                    collectMethods(mapper)));

            String pkg = elements.getPackageOf(mapper).getQualifiedName().toString();
            if (targetPackage == null) {
                targetPackage = pkg;
            } else if (!targetPackage.equals(pkg)) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "@Pojo mapper in package " + pkg + ": GenericBeansMapper is generated in " + targetPackage, candidate);
            }
        }

        if (roundEnv.processingOver() && !items.isEmpty()) {
            write();
        }
        return false;
    }

    /** Risale i supertipi fino a BaseMapper<S,D>, con i generics già risolti. */
    private Optional<DeclaredType> findBaseMapper(TypeMirror type) {
        for (TypeMirror superType : types.directSupertypes(type)) {
            if (!(superType instanceof DeclaredType declared)) continue;
            TypeElement el = (TypeElement) declared.asElement();
            if (el.getQualifiedName().contentEquals(BaseMapper.class.getName())) {
                return Optional.of(declared);
            }
            Optional<DeclaredType> found = findBaseMapper(superType);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }

    /**
     * Tutti i metodi del mapper: quelli di BaseMapper (serialize, deserialize, update)
     * e quelli custom dichiarati dall'utente, con i tipi generici già sostituiti.
     */
    private List<MethodItem> collectMethods(TypeElement mapper) {
        DeclaredType mapperType = (DeclaredType) mapper.asType();
        List<MethodItem> result = new ArrayList<>();

        for (Element member : elements.getAllMembers(mapper)) {
            if (member.getKind() != ElementKind.METHOD) continue;
            ExecutableElement method = (ExecutableElement) member;

            Set<Modifier> mods = method.getModifiers();
            if (mods.contains(Modifier.STATIC) || mods.contains(Modifier.PRIVATE)) continue;
            if (((TypeElement) method.getEnclosingElement()).getQualifiedName()
                    .contentEquals("java.lang.Object")) continue;

            ExecutableType resolved = (ExecutableType) types.asMemberOf(mapperType, method);
            if (resolved.getReturnType().getKind() == TypeKind.VOID) continue;
            if (resolved.getParameterTypes().size() != 1) continue; // dispatch per nome: 1 parametro

            result.add(new MethodItem(
                    method.getSimpleName().toString(),
                    erased(resolved.getParameterTypes().getFirst()),
                    erased(resolved.getReturnType())));
        }
        return result;
    }

    private String erased(TypeMirror t) {
        return types.erasure(t).toString();
    }

    private void write() {
        try {
            JavaFileObject file = filer.createSourceFile(targetPackage + ".GenericBeansMapper");
            try (Writer writer = file.openWriter()) {
                Configuration cfg = new Configuration(new Version("2.3.21"));
                cfg.setClassForTemplateLoading(GenericMapperGenerator.class, "/");
                cfg.setDefaultEncoding("UTF-8");

                Map<String, Object> data = new HashMap<>();
                data.put("packageName", targetPackage);
                data.put("items", new ArrayList<>(items.values()));
                cfg.getTemplate("GenericBeansMapper.ftl").process(data, writer);
            }
        } catch (TemplateException | IOException ex) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Cannot generate GenericBeansMapper: " + ex);
        }
    }
}
