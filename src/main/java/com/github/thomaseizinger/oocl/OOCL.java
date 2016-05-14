package com.github.thomaseizinger.oocl;

import org.jocl.CL;

public final class OOCL {

    private OOCL() { }

    static {
        enableExceptions();
    }

    public static void enableExceptions() {
        CL.setExceptionsEnabled(true);
    }

    public static void disableExceptions() {
        CL.setExceptionsEnabled(false);
    }
}
