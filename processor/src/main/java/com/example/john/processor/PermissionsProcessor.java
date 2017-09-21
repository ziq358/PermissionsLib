package com.example.john.processor;

import com.example.john.lib.NeedsPermission;
import com.example.john.lib.OnNeverAskAgain;
import com.example.john.lib.OnPermissionDenied;
import com.example.john.lib.RunTimePermissions;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class PermissionsProcessor extends AbstractProcessor {

    String CLASS_SUFFIX = "PermissionsDispatcher";
    String METHOD_SUFFIX = "WithCheck";
     String REQUEST_CODE_PREFIX = "REQUEST_";
     String PERMISSION_PREFIX = "PERMISSION_";


    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(RunTimePermissions.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(RunTimePermissions.class);
        for (Element runtimePermissionElement: annotatedElements) {
            try {
                JavaFile javaFile = generatePermissionFile((TypeElement)runtimePermissionElement);
                javaFile.writeTo(filer);
            } catch (IOException e) {
            }
        }
        return false;
    }


    private JavaFile generatePermissionFile(TypeElement element) throws IOException{

        String qualifiedName = element.getQualifiedName().toString();

        String packageName = getPackageName(qualifiedName);
        String className = getClassName(qualifiedName) + CLASS_SUFFIX;

        List<ExecutableElement> needPermissionList = findMethods(element, NeedsPermission.class);
        List<ExecutableElement> onPermissionDeniedList = findMethods(element, OnPermissionDenied.class);
        List<ExecutableElement> onNeverAskAgainList = findMethods(element, OnNeverAskAgain.class);


        //field
        List<FieldSpec> needPermissionFields = createFields(needPermissionList);


        //method
        List<MethodSpec> needPermissionMethod = createMethods(needPermissionList);


        //class
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.FINAL,Modifier.PUBLIC)
                .addFields(needPermissionFields)
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
                .addMethods(needPermissionMethod)
                .build();


        return JavaFile.builder(packageName, typeSpec)
                .build();
    }

     String getPackageName(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

     String getClassName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

     String getRequestCodeFieldName(String name) {
        return REQUEST_CODE_PREFIX + name.toUpperCase();
    }

     String getPermissionFieldName(String name) {
        return PERMISSION_PREFIX + name.toUpperCase();
    }


     List<ExecutableElement> findMethods(Element rootElement, Class<? extends Annotation> clazz){
        List<ExecutableElement> methods = new ArrayList<>();
        //获得子元素，分析子元素上的注解, 找出有clazz 注解的 函数
        for (Element enclosedElement : rootElement.getEnclosedElements()) {
            Annotation annotation = enclosedElement.getAnnotation(clazz);
            if (annotation != null) {
                methods.add((ExecutableElement) enclosedElement);
            }
        }
        return methods;
    }

     List<FieldSpec> createFields(List<ExecutableElement> elements) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        int requestCodeIndex = 0;
        for (ExecutableElement element : elements) {
            String requestCodeFieldName = getRequestCodeFieldName(element.getSimpleName().toString());
            String permissionFieldName = getPermissionFieldName(element.getSimpleName().toString());
            fieldSpecs.add(FieldSpec.builder(int.class, requestCodeFieldName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$L", requestCodeIndex++)
                        .build()
            );
            fieldSpecs.add(FieldSpec.builder(String[].class, permissionFieldName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$N", "new String[]{\""+element.getAnnotation(NeedsPermission.class).value()+"\"}")
                    .build()
            );
        }
        return fieldSpecs;
    }

    List<MethodSpec> createMethods(List<ExecutableElement> elements){
        List<MethodSpec> methods = new ArrayList<>();
        for (ExecutableElement element : elements) {
            methods.add(MethodSpec.methodBuilder(element.getSimpleName().toString()+METHOD_SUFFIX)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .build());
        }
        return methods;
    }

}
