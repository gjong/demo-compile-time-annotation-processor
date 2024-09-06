package com.github.gjong.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.github.gjong.cdi.Bean") // 1
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BeanProcessor extends AbstractProcessor {

    private boolean processCompleted;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processCompleted) {
            return false;
        }

        try {
            var resolver = new TypeDependencyResolver();
            var beanDefinitions = annotations.parallelStream()
                    .map(roundEnv::getElementsAnnotatedWith)
                    .flatMap(element -> ElementFilter.typesIn(element).stream())
                    .map(typeElement -> resolver.resolve(typeElement, processingEnv.getMessager()))
                    .toList();

            writeProxyClasses(beanDefinitions);
            writeServiceLoader(beanDefinitions);
            processCompleted = true;
        } catch (Exception e) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "Exception occurred %s".formatted(e));
        }

        return false;
    }

    private void writeProxyClasses(List<BeanDefinition> beanDefinitions) {
        for (var beanDefinition : beanDefinitions) {
            new BeanProxyWriter(beanDefinition).writeSourceFile(processingEnv);
        }
    }

    private void writeServiceLoader(List<BeanDefinition> beanDefinitions) {
        try (var serviceWriter = new PrintWriter(processingEnv.getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT,
                        "",
                        "META-INF/services/com.github.gjong.cdi.context.CdiBean")
                .openWriter())) {
            beanDefinitions.forEach(type -> serviceWriter.println(
                    "%s.%s$Proxy".formatted(
                            processingEnv.getElementUtils().getPackageOf(type.type()).getQualifiedName(),
                            type.type().getSimpleName())));
        } catch (Exception e) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "Exception occurred %s".formatted(e));
        }
    }
}
