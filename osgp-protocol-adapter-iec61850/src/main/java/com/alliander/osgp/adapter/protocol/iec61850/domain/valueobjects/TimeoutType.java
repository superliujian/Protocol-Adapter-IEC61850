/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects;

/**
 * The timeout type, used by the Device schedule
 */
public enum TimeoutType {

    FIX(0),
    SENSOR(1),
    AUTONOME(2);

    private int index;

    private TimeoutType(final int index) {
        this.index = index;
    }

    public static TimeoutType getByIndex(final int index) {

        for (final TimeoutType deviceRelayType : values()) {
            if (deviceRelayType.index == index) {
                return deviceRelayType;
            }
        }

        throw new IllegalArgumentException(String.valueOf(index));
    }

    public int getIndex() {
        return this.index;
    }

}