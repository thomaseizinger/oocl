package com.github.thomaseizinger.oocl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_CPU;
import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.CL_DEVICE_VERSION;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clGetDeviceInfo;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;

public class CLDevice {

    private final cl_device_id id;
    private final CLPlatform platform;

    public CLDevice(cl_device_id id, CLPlatform platform) {
        this.id = id;
        this.platform = platform;
    }

    /**
     * Returns the OpenCL device version.
     */
    public float getDeviceVersion() {
        final String deviceVersion = getDeviceInfoString(CL_DEVICE_VERSION);
        final String versionString = deviceVersion.substring(7, 10);
        final float version = Float.parseFloat(versionString);
        return version;
    }

    /**
     * Returns the value of the device info parameter with the given name.
     */
    public long getLong(int paramName) {
        return getLongs(paramName, 1)[0];
    }

    private long[] getLongs(int paramName, int numValues) {
        long values[] = new long[numValues];
        clGetDeviceInfo(id, paramName, Sizeof.cl_long * numValues, Pointer.to(values), null);
        return values;
    }

    /**
     * Returns the opencl device info string.
     */
    public String getDeviceInfoString(int param) {
        long size[] = new long[1];
        clGetDeviceInfo(id, param, 0, null, size);

        byte buffer[] = new byte[(int) size[0]];
        clGetDeviceInfo(id, param, buffer.length, Pointer.to(buffer), null);

        return new String(buffer, 0, buffer.length - 1);
    }

    /**
     * Creates a new {@link CLContext} using the given {@link cl_context_properties}.
     */
    public CLContext createContext(cl_context_properties contextProperties) {
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform.getPlatformId());

        final cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[] {id}, null, null, null);

        return new CLContext(context, this);
    }

    /**
     * Creates a new {@link CLContext}.
     */
    public CLContext createContext() {
        final cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform.getPlatformId());

        final cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[] {id}, null, null, null);

        return new CLContext(context, this);
    }

    /**
     * Returns the internal id.
     */
    public cl_device_id getId() {
        return id;
    }

    public enum DeviceType {
        CPU(CL_DEVICE_TYPE_CPU),
        GPU(CL_DEVICE_TYPE_GPU);

        private final long type;

        DeviceType(long type) {
            this.type = type;
        }

        public long getType() {
            return type;
        }
    }
}
