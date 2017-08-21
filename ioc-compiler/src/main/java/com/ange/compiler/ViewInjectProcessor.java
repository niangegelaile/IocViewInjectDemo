package com.ange.compiler;

import com.ange.annotation.Bind;


import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by ange on 2017/9/19.
 */

public class ViewInjectProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Map<String,ProxyInfo> mProxyMap=new HashMap<>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager=processingEnvironment.getMessager();
        elementUtils=processingEnvironment.getElementUtils();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE,"process...");
        mProxyMap.clear();
        Set<? extends Element> elementsBind= roundEnvironment.getElementsAnnotatedWith(Bind.class);
        for (Element element:elementsBind){
            checkAnnotationValid(element,Bind.class);
            //变量
            VariableElement variableElement= (VariableElement) element;
            //变量所在的类
            TypeElement classElement= (TypeElement) variableElement.getEnclosingElement();
            //类的全名
            String fgClassName=classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo=mProxyMap.get(fgClassName);
            if(proxyInfo==null){
                proxyInfo=new ProxyInfo(elementUtils,classElement);
                mProxyMap.put(fgClassName,proxyInfo);
            }
            Bind bindAnnotation =variableElement.getAnnotation(Bind.class);
            int id=bindAnnotation.value();
            proxyInfo.injectVariables.put(id,variableElement);

        }
        for(String key:mProxyMap.keySet()){
            ProxyInfo proxyInfo=mProxyMap.get(key);
            try {
                JavaFileObject jfo=processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer=jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                error(proxyInfo.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(), e.getMessage());
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes=new HashSet<>();
        supportTypes.add(Bind.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean checkAnnotationValid(Element annotatedElement , Class clazz){
        if(annotatedElement.getKind()!= ElementKind.FIELD){
            error(annotatedElement,"%s must be declared on field.",clazz.getSimpleName());
            return false;
        }
        if(ClassValidator.isPrivate(annotatedElement)){
            error(annotatedElement,"%s() must can not be private.",annotatedElement.getSimpleName());
            return false;
        }
        return true;
    }

    private void error(Element element,String message ,Object... args){
        if(args.length>0){
            message=String.format(message,args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,message,element);
    }



}
