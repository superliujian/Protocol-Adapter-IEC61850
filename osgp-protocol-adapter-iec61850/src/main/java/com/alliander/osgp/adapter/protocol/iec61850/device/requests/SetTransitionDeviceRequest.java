/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.requests;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.TransitionMessageDataContainer;

public class SetTransitionDeviceRequest extends DeviceRequest {

    private TransitionMessageDataContainer transitionTypeContainer;

    public SetTransitionDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final TransitionMessageDataContainer transitionTypeContainer) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.transitionTypeContainer = transitionTypeContainer;
    }

    public SetTransitionDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final TransitionMessageDataContainer transitionTypeContainer,
            final String domain, final String domainVersion, final String messageType, final String ipAddress,
            final int retryCount, final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.transitionTypeContainer = transitionTypeContainer;
    }

    public TransitionMessageDataContainer getTransitionTypeContainer() {
        return this.transitionTypeContainer;
    }
}
