package com.cabbooking.model.enums;

import lombok.Getter;

@Getter
public enum DriverStatus {

    /** The Driver is not logged in or not accepting rides. */
    OFFLINE("Offline"),

    /** The Driver is active and waiting for a booking request. */
    AVAILABLE("Available"),

    /**The Driver has accepted a request and is busy. */
    BUSY("Busy"),

    /** The Driver is currently on a break. */
    ON_BREAK("On Break"),

    /** The Account is suspended due to violations or documents. */
    SUSPENDED("Suspended");

    private final String displayName;

    DriverStatus(String displayName) {
        this.displayName = displayName;
    }
}