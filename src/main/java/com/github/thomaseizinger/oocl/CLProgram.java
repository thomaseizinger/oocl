package com.github.thomaseizinger.oocl;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clReleaseProgram;

import java.util.Arrays;

import org.jocl.cl_program;

public class CLProgram implements AutoCloseable {

    private final cl_program program;

    public CLProgram(final cl_program program) {
        this.program = program;
    }

    private static String concatWithSpace(String existing, String other) {
        return existing + " " + other;
    }

    /**
     * Builds the OpenCL program with the given options.
     */
    public void build(final BuildOption... options) {
        clBuildProgram(
                program,
                0,
                null,
                Arrays
                        .stream(options)
                        .filter(o -> o != null)
                        .map(BuildOption::getOption)
                        .reduce(CLProgram::concatWithSpace)
                        .orElseGet(BuildOption.EMPTY::getOption),
                null,
                null
        );
    }

    /**
     * Creates a kernel from this program with the given kernel name.
     */
    public CLKernel createKernel(String kernelName) {
        return CLKernel.createKernel(this, kernelName);
    }

    /**
     * Returns the internal id.
     */
    public cl_program getId() {
        return program;
    }

    @Override
    public void close() throws Exception {
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

        public static BuildOption of(String option) {
            return new BuildOption(option);
        }

        public String getOption() {
            return option;
        }
    }
}
