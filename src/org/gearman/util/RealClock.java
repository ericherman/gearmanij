package org.gearman.util;

import java.util.Date;

public class RealClock implements Clock {

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public Date newDate() {
        return new Date();
    }

}
