package com.devcat.watchdogplugin

public class WatchDogExtension {

    def enabled = true;

    def setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    def getEnabled() {
        return enabled;
    }
}