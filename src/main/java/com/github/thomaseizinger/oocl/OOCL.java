package com.github.thomaseizinger.oocl;

import org.jocl.CL;

public final class OOCL {

    private OOCL() {
    }

    public static void enableExceptions() {
        CL.setExceptionsEnabled(true);
    }

    public static void disableExceptions() {
        CL.setExceptionsEnabled(false);
    }
}
