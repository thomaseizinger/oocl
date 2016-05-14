package com.github.thomaseizinger.oocl;

import org.jocl.cl_program;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clReleaseProgram;

public class CLProgram implements Closeable {
    private final cl_program program;

    public CLProgram(final cl_program program) {
        this.program = program;
    }

    /**
     * Builds the OpenCL program with the given options.
     *
     * @param options
     */
    public void build(final BuildOption... options) {
        clBuildProgram(program, 0, null, Arrays.stream(options).filter(o -> o != null).map(o -> o.option).reduce("", (accu, o) -> accu + " " + o), null, null);
    }

    /**
     * Creates a kernel from this program with the given kernel name.
     *
     * @param kernelName
     * @return
     */
    public CLKernel createKernel(String kernelName) {
        return CLKernel.createKernel(this, kernelName);
    }

    /**
     * Returns the internal id.
     *
     * @return
     */
    public cl_program getId() {
        return program;
    }

    @Override
    public void close() throws IOException {
        clReleaseProgram(program);
    }

    /**
     * Represents a build option for an OpenCL program.
     */
    public static class BuildOption {
        public static final BuildOption CL20 = new BuildOption("-cl-std=CL2.0");
        public static final BuildOption MAD = new BuildOption("-cl-mad-enable");
        public static final BuildOption EMPTY = new BuildOption("");
        private final String option;

        public BuildOption(String option) {
            this.option = option;
        }

        // should be static?
        public BuildOption of(String option) {
            return new BuildOption(option);
        }
    }

}
