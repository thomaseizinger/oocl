package com.github.thomaseizinger.oocl;

import org.jocl.Pointer;
import org.jocl.cl_mem;

import java.io.Closeable;
import java.io.IOException;

import static org.jocl.CL.clReleaseMemObject;

public class CLMemory<T> implements Closeable {
    private cl_mem memory;
    private Pointer pointer;
    private long size;
    private T data;

    public CLMemory(cl_mem memory, long size, Pointer pointer, T data) {
        super();
        this.memory = memory;
        this.size = size;
        this.pointer = pointer;
        this.data = data;
    }

    /* default */Pointer getPointer() {
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

    public void release() {
        clReleaseMemObject(memory);
        memory = null;
    }
}
