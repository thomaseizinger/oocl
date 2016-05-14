package com.github.thomaseizinger.oocl;

import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clReleaseContext;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.jocl.CL;
import org.jocl.CLException;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_mem;
import org.jocl.cl_program;

public class CLContext implements AutoCloseable {

    private final cl_context context;
    private final CLDevice device;

    public CLContext(final cl_context context, final CLDevice device) {
        this.context = context;
        this.device = device;
    }

    public CLCommandQueue createCommandQueue() {
        @SuppressWarnings("deprecation")
        final cl_command_queue commandQueue = clCreateCommandQueue(context, device.getId(), 0, null);
        return new CLCommandQueue(commandQueue);
    }

    /**
     * Creates a {@link CLProgram} from the given Files.
     *
     * @throws UncheckedIOException If there was a problem loading the given file.
     */
    public CLProgram createProgram(final File... file) {
        final String[] programs = Arrays.stream(file).map(f -> {
            try {
                return Files
                        .lines(f.toPath())
                        .reduce("", (accu, l) -> accu + l + System.lineSeparator());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).toArray(String[]::new);

        return createProgram(programs);
    }

    /**
     * Returns the kernel from the given program file. This method automatically loads and builds the program and
     * returns the kernel.
     */
    public CLKernel createKernel(File file, String kernel, CLProgram.BuildOption... options) {
        final CLProgram program = createProgram(file);
        program.build(options);
        return program.createKernel(kernel);
    }

    /**
     * Creates a program with the given sources.
     */
    public CLProgram createProgram(final String... sources) {

        final cl_program program = clCreateProgramWithSource(context, 1, sources, null, null);

        return new CLProgram(program);
    }

    /**
     * Creates a buffer memory object from the given int array.
     * For a list of possible flags see {@link CL}.
     */
    public CLMemory<Void> createEmptyBuffer(final long flags, int size) {
        final cl_mem mem = clCreateBuffer(context, flags, size, null, null);
        return new CLMemory<>(mem, Sizeof.cl_mem, Pointer.to(mem), null);
    }

    /**
     * Creates a buffer memory object from the given int array.
     * For a list of possible flags see {@link CL}.
     */
    public CLMemory<int[]> createBuffer(final long flags, final int[] data) {
        final Pointer pointer = Pointer.to(data);
        final cl_mem mem = clCreateBuffer(context, flags, Sizeof.cl_int * data.length, pointer, null);
        return new CLMemory<>(mem, Sizeof.cl_int * data.length, pointer, data);
    }

    /**
     * Creates a buffer memory object from the given long array.
     * For a list of possible flags see {@link CL}.
     */
    public CLMemory<long[]> createBuffer(final long flags, final long[] data) {
        final Pointer pointer = Pointer.to(data);
        final cl_mem mem = clCreateBuffer(context, flags, Sizeof.cl_ulong * data.length, pointer, null);
        return new CLMemory<>(mem, Sizeof.cl_ulong * data.length, pointer, data);
    }

    /**
     * Creates a buffer memory object from the given float array.
     * For a list of possible flags see {@link CL}.
     */
    public CLMemory<float[]> createBuffer(long flags, float[] data) {
        final Pointer pointer = Pointer.to(data);
        final cl_mem mem = clCreateBuffer(context, flags, Sizeof.cl_float * data.length, pointer, null);
        return new CLMemory<>(mem, Sizeof.cl_float * data.length, pointer, data);

    }

    /**
     * Returns the internal id.
     */
    public cl_context getContext() {
        return context;
    }

    @Override
    public void close() throws CLException {
        clReleaseContext(context);
    }
}
