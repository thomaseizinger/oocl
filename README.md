# Object Oriented OpenCL (OOCL) Java Wrapper #

Simple JOCL based object oriented OpenCL wrapper.

## Example Usage ##

The following OpenCL kernel:

````c
__kernel void vecadd(__global int * A, __global int * B, __global int * C) {
    int tId = get_global_id(0);
    C[tId] = A[tId] + B[tId];
}
````

can be executed using the following code:

```java
import static com.github.thomaseizinger.oocl.CLProgram.BuildOption;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_WRITE_ONLY;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import com.github.thomaseizinger.oocl.CLCommandQueue;
import com.github.thomaseizinger.oocl.CLContext;
import com.github.thomaseizinger.oocl.CLDevice;
import com.github.thomaseizinger.oocl.CLKernel;
import com.github.thomaseizinger.oocl.CLMemory;
import com.github.thomaseizinger.oocl.CLPlatform;
import com.github.thomaseizinger.oocl.CLRange;

public class VectorAddition {

    private final int[] first;
    private final int[] second;

    public VectorAddition(int[] first, int[] second) {
        this.first = first;
        this.second = second;
    }

    public int[] compute() {

        assert first.length == second.length;

        final int[] result = new int[first.length];

        final CLPlatform platform = CLPlatform.getFirst().orElseThrow(IllegalStateException::new);
        final CLDevice device = platform.getDevice(CLDevice.DeviceType.GPU).orElseThrow(IllegalStateException::new);

        final URI resource = getKernelURI();

        try (CLContext context = device.createContext()) {
            try (CLKernel vecadd = context.createKernel(new File(resource), "vecadd", BuildOption.EMPTY)) {
                try (
                    CLMemory<int[]> firstBuffer = context.createBuffer(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, first);
                    CLMemory<int[]> secondBuffer = context.createBuffer(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, second);
                    CLMemory<int[]> resultBuffer = context.createBuffer(CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, result);
                ) {
                    vecadd.setArguments(firstBuffer, secondBuffer, resultBuffer);

                    final CLCommandQueue commandQueue = context.createCommandQueue();

                    commandQueue.execute(vecadd, 1, CLRange.of(first.length), CLRange.of(1));
                    commandQueue.finish();

                    commandQueue.readBuffer(resultBuffer);

                    return resultBuffer.getData();
                }
            }
        }
    }

    private static URI getKernelURI() {
        try {
            return VectorAddition.class.getResource("/vector_addition.cl").toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}

```

## Future Plans ##

- Test suite
- API redefinement
- More features from JOCL

