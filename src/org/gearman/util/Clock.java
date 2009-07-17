package org.gearman.util;

import java.util.Date;

public interface Clock {
    Date newDate();

    long currentTimeMillis();
}
