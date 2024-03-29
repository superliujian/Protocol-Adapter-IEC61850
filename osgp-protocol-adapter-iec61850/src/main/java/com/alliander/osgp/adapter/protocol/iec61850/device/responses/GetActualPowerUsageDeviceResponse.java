/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.responses;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.dto.valueobjects.PowerUsageData;

public class GetActualPowerUsageDeviceResponse extends EmptyDeviceResponse {

    private PowerUsageData actualPowerUsageData;

    public GetActualPowerUsageDeviceResponse(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final DeviceMessageStatus status,
            final PowerUsageData actualPowerUsageData) {
        super(organisationIdentification, deviceIdentification, correlationUid, status);
        this.actualPowerUsageData = actualPowerUsageData;
    }

    public PowerUsageData getActualPowerUsageData() {
        return this.actualPowerUsageData;
    }
}
