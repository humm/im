package com.hoomoomoo.im.timer;

import java.util.Timer;

public class ImTimer extends Timer {

    private final String name;

    public ImTimer(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
