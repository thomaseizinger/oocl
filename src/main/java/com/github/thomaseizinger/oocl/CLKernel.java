package com.github.thomaseizinger.oocl;

import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;

public class CLKernel implements AutoCloseable {

    private final cl_kernel kernel;
    private final String kernelName;

    public CLKernel(cl_kernel kernel, String kernelName) {
        this.kernel = kernel;
        this.kernelName = kernelName;
    }

    /**
     * Creates a {@link CLKernel} from the given {@link CLProgram} with the given kernel name.
     */
    public static CLKernel createKernel(CLProgram program, String kernelName) {
        cl_kernel kernel = clCreateKernel(program.getId(), kernelName, null);

        return new CLKernel(kernel, kernelName);
    }

    /**
     * Sets the arguments of this kernel. The arguments must be in order required by the kernel.
     */
    public void setArguments(CLMemory<?>... memory) {
        for (int memoryIndex = 0; memoryIndex < memory.length; ++memoryIndex) {
            clSetKernelArg(kernel, memoryIndex, Sizeof.cl_mem, Pointer.to(memory[memoryIndex].getMemory()));
        }
    }

    /**
     * Returns the internal kernel id.
     */
    public cl_kernel getKernel() {
        return kernel;
    }

    /**
     * Returns the name of the kernel.
     */
    public String getKernelName() {
        return kernelName;
    }

    @Override
    public void close() throws Exception {
        clReleaseKernel(kernel);
    }
}
