package com.example;

import com.example.john.lib.RunTimePermissions;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        System.out.println("ziq PermissionsProcessor---------------1");
        System.out.println("ziq PermissionsProcessor---------------2");
        Collection<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(RunTimePermissions.class);
        Iterator<? extends Element> iterator = annotatedElements.iterator();
        while (iterator.hasNext()){
            System.out.println("ziq has RunTimePermissions---------------");
            Element element = iterator.next();
            try {
                JavaFile javaFile = generateHelloworld(element.getSimpleName().toString());
                javaFile.writeTo(filer);
            } catch (IOException e) {
            }
        }
        return false;
    }


    private JavaFile generateHelloworld(String name) throws IOException{
        MethodSpec main = MethodSpec.methodBuilder("main") //main代表方法名
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)//Modifier 修饰的关键字
                .addParameter(String[].class, "args") //添加string[]类型的名为args的参数
                .addStatement("$T.out.println($S)", System.class,"Hello World, "+name)//添加代码，这里$T和$S后面会讲，这里其实就是添加了System,out.println("Hello World");
                .build();
        TypeSpec typeSpec = TypeSpec.classBuilder("HelloWorld")//HelloWorld是类名
                .addModifiers(Modifier.FINAL,Modifier.PUBLIC)
                .addMethod(main)  //在类中添加方法
                .build();
        return JavaFile.builder("com.example.john.permissionslib", typeSpec)
                .build();
    }

}
