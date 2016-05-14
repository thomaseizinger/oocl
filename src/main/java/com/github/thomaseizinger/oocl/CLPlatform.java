package com.github.thomaseizinger.oocl;

import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.thomaseizinger.oocl.CLDevice.DeviceType;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;

public class CLPlatform implements Closeable {

    private cl_platform_id platformId;

    private CLPlatform(cl_platform_id id) {
        this.platformId = id;
    }

    /**
     * Returns the number of available platforms.
     *
     * @return
     */
    public static int getNumberOfPlatforms() {
        final int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        return numPlatformsArray[0];
    }

    /**
     * Returns the first available platform or null if there are none.
     *
     * @return
     */
    public static CLPlatform getFirst() {
        return getPlatforms().stream().findFirst().orElse(null);
    }

    /**
     * Returns a {@link List} of all available opencl platforms.
     *
     * @return
     */
    public static List<CLPlatform> getPlatforms() {
        final cl_platform_id platforms[] = new cl_platform_id[getNumberOfPlatforms()];
        clGetPlatformIDs(platforms.length, platforms, null);

        return Arrays.stream(platforms).map(CLPlatform::new).collect(Collectors.toList());
    }

    /**
     * Returns the first device matching the given filter or null if none
     * exists.
     *
     * @param deviceType
     * @param filters
     * @return
     */
    public Optional<CLDevice> getDevice(DeviceType deviceType, Predicate<CLDevice>... filters) {
        final Predicate<CLDevice> concatenated = Arrays.stream(filters).reduce((d) -> true, Predicate::and);
        return getDevices(deviceType).stream().filter(concatenated).findFirst();
    }

    /**
     * Returns all devices associated with this platform.
     *
     * @param deviceType
     * @return
     */
    public List<CLDevice> getDevices(DeviceType deviceType) {
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platformId, deviceType.getType(), 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain the all device IDs
        cl_device_id allDevices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platformId, deviceType.getType(), numDevices, allDevices, null);

        return Arrays.stream(allDevices).map(id -> new CLDevice(id, this)).collect(Collectors.toList());
    }

    public cl_platform_id getPlatformId() {
        return platformId;
    }

    @Override
    public void close() throws IOException {
    }

}
