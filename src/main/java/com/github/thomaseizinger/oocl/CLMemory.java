package com.github.thomaseizinger.oocl;

import static org.jocl.CL.clReleaseMemObject;

import java.io.Closeable;
import java.io.IOException;

import org.jocl.Pointer;
import org.jocl.cl_mem;

public class CLMemory<T> implements Closeable {

    private final cl_mem memory;
    private final Pointer pointer;
    private final long size;
    private final T data;

    public CLMemory(cl_mem memory, long size, Pointer pointer, T data) {
        super();
        this.memory = memory;
        this.size = size;
        this.pointer = pointer;
        this.data = data;
    }

    Pointer getPointer() {
        return pointer;
    }

    public long getSize() {
        return size;
    }

    public cl_mem getMemory() {
        return memory;
    }

    public T getData() {
        return data;
    }

    @Override
    public void close() throws IOException {
        clReleaseMemObject(memory);
    }
}
