package demo.scanner;

import demo.annotations.Driver;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("demo.annotations.Driver")
public class DriverProcessor extends AbstractProcessor {

    private record DriverInfo(String name, String constructorCall) {

    }

    private boolean alreadyWritten = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (alreadyWritten) {
            return false;
        }

        // Get all classes annotated with @Driver and implement PipelineDriver
        alreadyWritten = true;
        var elements = roundEnv.getElementsAnnotatedWith(Driver.class);
        var driverCalls = elements.stream()
                .map(this::processDriver)
                .toList();

        writeToFile(driverCalls);

        return true;
    }

    private DriverInfo processDriver(Element element) {
        // Generate code to register the driver in the DriverRegistry
        return new DriverInfo(
                // create full class name
                processingEnv.getElementUtils().getPackageOf(element).getQualifiedName() + "." + element.getSimpleName(),
                String.format("DriverRegistry.getInstance().registerDriver(new %s());", element.getSimpleName().toString())
        );
    }

    private void writeToFile(List<DriverInfo> driverCalls) {
        // Create a new source file and write the driver calls to it
        try (PrintWriter writer = new PrintWriter(processingEnv.getFiler()
                .createSourceFile("DriverInitializer")
                .openWriter())) {

            writer.println("package demo.scanner;");
            writer.println();

            writer.println("import demo.scanner.DriverRegistry;");
            driverCalls.stream()
                    .map(DriverInfo::name)
                    .forEach(driver -> writer.println("import " + driver + ";"));

            writer.println("public class DriverInitializer {");

            writer.println("    public static void initializeDrivers() {");
            // add the driver calls
            driverCalls.stream()
                    .map(DriverInfo::constructorCall)
                    .forEach(writer::println);
            writer.println("    }");

            // add a static block to call the initializeDrivers method
            writer.println("    static {");
            writer.println("        initializeDrivers();");
            writer.println("    }");

            writer.println("}");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
