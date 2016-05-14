package com.github.thomaseizinger.oocl;

import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clEnqueueAcquireGLObjects;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clEnqueueReleaseGLObjects;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clFlush;

import org.jocl.cl_command_queue;
import org.jocl.cl_mem;

public class CLCommandQueue {

    private cl_command_queue queue;

    public CLCommandQueue(cl_command_queue queue) {
        this.queue = queue;
    }

    public void execute(CLKernel kernel, int dimensions, CLRange globalWorkSize, CLRange localWorkSize) {
        clEnqueueNDRangeKernel(
                queue,
                kernel.getKernel(),
                dimensions,
                null,
                globalWorkSize.toArray(),
                localWorkSize.toArray(),
                0,
                null,
                null
        );
    }

    public void readBuffer(CLMemory<?> memory) {
        clEnqueueReadBuffer(
                queue,
                memory.getMemory(),
                CL_TRUE,
                0,
                memory.getSize(),
                memory.getPointer(),
                0,
                null,
                null
        );
    }

    public void enqueAcquireGLObject(CLMemory<?> memory) {
        clEnqueueAcquireGLObjects(queue, 1, new cl_mem[] {memory.getMemory()}, 0, null, null);
    }

    public void enqueueReleaseGLObject(CLMemory<?> memory) {
        clEnqueueReleaseGLObjects(queue, 1, new cl_mem[] {memory.getMemory()}, 0, null, null);
    }

    public void flush() {
        clFlush(queue);
    }

    public void finish() {
        clFinish(queue);
    }
}
