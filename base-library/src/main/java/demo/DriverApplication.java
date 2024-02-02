package demo;

import demo.scanner.DriverRegistry;

public class DriverApplication {

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("Drivers: ");

        Class.forName("demo.scanner.DriverInitializer");
        DriverRegistry.getInstance()
                .getDrivers()
                .forEach(System.out::println);
    }
}
