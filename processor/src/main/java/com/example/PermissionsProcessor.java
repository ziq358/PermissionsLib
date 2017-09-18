package com.example;

import com.example.john.lib.RunTimePermissions;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;


@SupportedAnnotationTypes({"com.example.john.lib.RunTimePermissions"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PermissionsProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("ziq PermissionsProcessor---------------");
        System.out.println("ziq PermissionsProcessor---------------");
        System.out.println("ziq PermissionsProcessor---------------");
        System.out.println("ziq PermissionsProcessor---------------");
        return false;
    }
}
