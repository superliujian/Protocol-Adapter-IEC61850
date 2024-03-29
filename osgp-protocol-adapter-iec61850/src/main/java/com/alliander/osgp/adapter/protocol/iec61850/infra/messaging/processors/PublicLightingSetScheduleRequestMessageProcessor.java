/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.dto.valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.ScheduleMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.ConnectionFailureException;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing public lighting set schedule request messages
 */
@Component("iec61850PublicLightingSetScheduleRequestMessageProcessor")
public class PublicLightingSetScheduleRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingSetScheduleRequestMessageProcessor.class);

    public PublicLightingSetScheduleRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_LIGHT_SCHEDULE);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting set schedule request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        Boolean isScheduled = null;
        int retryCount = 0;
        ScheduleMessageDataContainer scheduleMessageDataContainer = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            isScheduled = message.getBooleanProperty(Constants.IS_SCHEDULED);
            retryCount = message.getIntProperty(Constants.RETRY_COUNT);
            scheduleMessageDataContainer = (ScheduleMessageDataContainer) message.getObject();

        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("ipAddress: {}", ipAddress);
            LOGGER.debug("scheduled: {}", isScheduled);
            return;
        }

        try {

            final RequestMessageData requestMessageData = new RequestMessageData(scheduleMessageDataContainer, domain,
                    domainVersion, messageType, retryCount, isScheduled, correlationUid, organisationIdentification,
                    deviceIdentification);

            final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

                @Override
                public void handleResponse(final DeviceResponse deviceResponse) {
                    PublicLightingSetScheduleRequestMessageProcessor.this.handleEmptyDeviceResponse(deviceResponse,
                            PublicLightingSetScheduleRequestMessageProcessor.this.responseMessageSender,
                            requestMessageData.getDomain(), requestMessageData.getDomainVersion(),
                            requestMessageData.getMessageType(), requestMessageData.getRetryCount());
                }

                @Override
                public void handleException(final Throwable t, final DeviceResponse deviceResponse,
                        final boolean expected) {

                    if (expected) {
                        PublicLightingSetScheduleRequestMessageProcessor.this.handleExpectedError(
                                new ConnectionFailureException(ComponentType.PROTOCOL_IEC61850, t.getMessage()),
                                requestMessageData.getCorrelationUid(),
                                requestMessageData.getOrganisationIdentification(),
                                requestMessageData.getDeviceIdentification(), requestMessageData.getDomain(),
                                requestMessageData.getDomainVersion(), requestMessageData.getMessageType());
                    } else {
                        PublicLightingSetScheduleRequestMessageProcessor.this.handleUnExpectedError(deviceResponse, t,
                                requestMessageData.getMessageData(), requestMessageData.getDomain(),
                                requestMessageData.getDomainVersion(), requestMessageData.getMessageType(),
                                requestMessageData.isScheduled(), requestMessageData.getRetryCount());
                    }
                }
            };

            LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

            final SetScheduleDeviceRequest deviceRequest = new SetScheduleDeviceRequest(organisationIdentification,
                    deviceIdentification, correlationUid, scheduleMessageDataContainer, RelayType.LIGHT, domain,
                    domainVersion, messageType, ipAddress, retryCount, isScheduled);

            this.deviceService.setSchedule(deviceRequest, deviceResponseHandler);
        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain,
                    domainVersion, messageType, retryCount);
        }
    }

}
