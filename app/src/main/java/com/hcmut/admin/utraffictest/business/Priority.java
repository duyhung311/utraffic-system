package com.hcmut.admin.utraffictest.business;

/**
 * Priority levels
 */
public enum Priority {
    /**
     * NOTE: DO NOT CHANGE ORDERING OF THOSE CONSTANTS UNDER ANY CIRCUMSTANCES.
     * Doing so will make ordering incorrect.
     */

    /**
     * Lowest priority level. Used for  Prefetches of data. low priority
     */
    LOW,

    /**
     * Medium priority level. Used for  Warming of data that might soon get visible.
     */
    MEDIUM,

    /**
     * Highest priority level. Used for  Data that are currently visible on screen.
     */
    HIGH,

    /**
     * Highest priority level. Used for data that are required instantly(mainly for  Emergency). highest priority
     */
    IMMEDIATE;
}

