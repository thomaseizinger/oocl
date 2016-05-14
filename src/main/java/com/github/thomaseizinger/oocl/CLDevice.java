package com.github.thomaseizinger.oocl;

import org.jocl.*;

import static org.jocl.CL.*;

public class CLDevice {
    private cl_device_id id;
    private CLPlatform platform;

    public CLDevice(cl_device_id id, CLPlatform platform) {
        this.id = id;
        this.platform = platform;
    }


    /**
     * Returns the OpenCL device version.
     *
     * @return the OpenCL device version.
     */
    public float getDeviceVersion() {
        final String deviceVersion = getDeviceInfoString(CL_DEVICE_VERSION);
        final String versionString = deviceVersion.substring(7, 10);
        final float version = Float.parseFloat(versionString);
        return version;
    }

    /**
     * Returns the value of the device info parameter with the given name
     *
     * @param paramName The parameter name
     * @return The value
     */
    public long getLong(int paramName) {
        return getLongs(paramName, 1)[0];
    }

    /**
     * Returns the values of the device info parameter with the given name
     *
     * @param paramName The parameter name
     * @param numValues The number of values
     * @return The value
     */
    private long[] getLongs(int paramName, int numValues) {
        long values[] = new long[numValues];
        clGetDeviceInfo(id, paramName, Sizeof.cl_long * numValues, Pointer.to(values), null);
        return values;
    }

    /**
     * Returns the opencl device info string.
     *
     * @param param
     * @return
     */
    public String getDeviceInfoString(int param) {
        long size[] = new long[1];
        clGetDeviceInfo(id, param, 0, null, size);

        byte buffer[] = new byte[(int) size[0]];
        clGetDeviceInfo(id, param, buffer.length, Pointer.to(buffer), null);

        return new String(buffer, 0, buffer.length - 1);
    }

    /**
     * Creates a new {@link CLContext}
     *
     * @return
     */
    public CLContext createContext(cl_context_properties contextProperties) {
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform.getPlatformId());

        final cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[]{id}, null, null, null);

        return new CLContext(context, this);
    }

    /**
     * Creates a new {@link CLContext}
     *
     * @return
     */
    public CLContext createContext() {
        final cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform.getPlatformId());

        final cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[]{id}, null, null, null);

        return new CLContext(context, this);
    }

    /**
     * Returns the internal id.
     *
     * @return
     */
    public cl_device_id getId() {
        return id;
    }

    public enum DeviceType {
        CPU(CL_DEVICE_TYPE_CPU), GPU(CL_DEVICE_TYPE_GPU);

        private final long type;

        private DeviceType(long type) {
            this.type = type;
        }

        public long getType() {
            return type;
        }

    }

}
