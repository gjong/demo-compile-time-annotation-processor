package demo.scanner;

import demo.annotations.PipelineDriver;

import java.util.HashSet;
import java.util.Set;

public class DriverRegistry {

    private static final DriverRegistry INSTANCE = new DriverRegistry();

    private final Set<PipelineDriver> drivers;

    public DriverRegistry() {
        this.drivers = new HashSet<>();
    }

    public void registerDriver(PipelineDriver driver) {
        drivers.add(driver);
    }

    public Set<PipelineDriver> getDrivers() {
        return drivers;
    }

    public static DriverRegistry getInstance() {
        return INSTANCE;
    }
}
