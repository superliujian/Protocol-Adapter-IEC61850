/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.DeviceMessageFailedException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.DeviceMessageRejectedException;

@Service
public class DeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseService.class);

    public void handleDeviceMessageStatus(final DeviceMessageStatus status) throws DeviceMessageRejectedException,
            DeviceMessageFailedException {
        switch (status) {
        case FAILURE:
            LOGGER.info("Failure device message status received: {}", status);
            throw new DeviceMessageFailedException();
        case REJECTED:
            LOGGER.info("Rejected device message status received: {}", status);
            throw new DeviceMessageRejectedException();
        case OK:
            LOGGER.info("OK device message status received: {}", status);
            break;
        default:
            LOGGER.warn("Unknown device message status received: {}", status);
            break;
        }
    }
}
