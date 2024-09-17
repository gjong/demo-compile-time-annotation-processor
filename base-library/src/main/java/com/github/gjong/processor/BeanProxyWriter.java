package com.github.gjong.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Function;

class BeanProxyWriter {
    private final BeanDefinition definition;
    private final String definedClassName;
    private final Function<BeanDefinition.ArgumentDefinition, String> parameterResolver;

    public BeanProxyWriter(BeanDefinition definition) {
        this.definition = definition;
        this.definedClassName = "%s$Proxy".formatted(definition.type().getSimpleName());
        this.parameterResolver = typeMirror -> Optional.ofNullable(typeMirror.qualifier())
                    .map(qualifier -> "provider.provide(\"%s\")".formatted(qualifier.value()))
                    .orElseGet(() -> "provider.provide(%s.class)".formatted(typeMirror.type()));
    }

    void writeSourceFile(ProcessingEnvironment environment) {
        var packageName = environment.getElementUtils().getPackageOf(definition.type()).getQualifiedName();
        var fileName = packageName + "." + definedClassName;
        var beanClass = definition.type();
        var bean = definition.getBean();

        try (var writer = new PrintWriter(environment.getFiler().createSourceFile(fileName).openWriter())) {
            writer.println("package %s;".formatted(packageName));
            writer.println();
            writer.println("import com.github.gjong.cdi.BeanProvider;");
            writer.println("import com.github.gjong.cdi.context.CdiBean;");
            writer.println("import com.github.gjong.cdi.context.SingletonProvider;");
            writer.println();
            writer.println("import %s;".formatted(beanClass));
            writer.println();
            writer.println("public class %s implements CdiBean<%s> {".formatted(definedClassName, beanClass.getSimpleName()));
            writer.println();

            writer.println("    private final SingletonProvider<%s> singletonProvider;".formatted(beanClass.getSimpleName()));

            writer.println();
            writer.println("    public %s() {".formatted(definedClassName));
            if (bean.shared()) {
                writer.println("         singletonProvider = new SingletonProvider() {");
                writer.println("             private %s instance;".formatted(beanClass.getSimpleName()));
                writer.println("             public %s provide(BeanProvider provider) {".formatted(beanClass.getSimpleName()));
                writer.println("                 if (instance == null) {");
                writer.print("                     instance = ");
                createConstructor(writer, beanClass);
                writer.println(";");
                writer.println("                 }");
                writer.println("                 return instance;");
                writer.println("            }");
                writer.println("        };");
            } else {
                writer.println("         singletonProvider = new SingletonProvider() {");
                writer.println("            public %s provide(BeanProvider provider) {".formatted(beanClass.getSimpleName()));
                writer.print("                return ");
                createConstructor(writer, beanClass);
                writer.println(";");
                writer.println("            }");
                writer.println("        };");
            }
            writer.println("    }");

            writer.println("    public %s create(BeanProvider provider) {".formatted(beanClass.getSimpleName()));
            writer.println("        return singletonProvider.provide(provider);");
            writer.println("    }");
            writer.println();
            writer.println("    public Class<%s> type() {".formatted(beanClass.getSimpleName()));
            writer.println("        return %s.class;".formatted(beanClass.getSimpleName()));
            writer.println("    }");
            writer.println();
            writer.println("    public String qualifier() {");
            writer.println("        return \"%s\";".formatted(bean.name()));
            writer.println("    }");
            writer.println("}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createConstructor(PrintWriter writer, TypeElement solutionClass) {
        writer.print("new %s(".formatted(solutionClass.getSimpleName()));
        for (var it = definition.dependencies().iterator(); it.hasNext(); ) {
            writer.print(parameterResolver.apply(it.next()));
            if (it.hasNext()) {
                writer.print(",");
            }
        }
        writer.print(")");
    }
}
