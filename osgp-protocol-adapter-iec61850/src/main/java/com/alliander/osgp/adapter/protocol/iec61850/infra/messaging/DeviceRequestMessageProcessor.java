/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetConfigurationDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetStatusDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.DeviceService;
import com.alliander.osgp.adapter.protocol.iec61850.services.DeviceResponseService;
import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.dto.valueobjects.DeviceStatus;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.MessageProcessorMap;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class DeviceRequestMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageProcessor.class);

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected DeviceResponseMessageSender responseMessageSender;

    @Autowired
    protected DeviceResponseService deviceResponseService;

    @Autowired
    @Qualifier("iec61850DeviceRequestMessageProcessorMap")
    protected MessageProcessorMap iec61850RequestMessageProcessorMap;

    protected final DeviceRequestMessageType deviceRequestMessageType;

    protected final static String UNEXPECTED_EXCEPTION = "Unexpected exception while retrieving response message";

    /**
     * Each MessageProcessor should register it's MessageType at construction.
     *
     * @param deviceRequestMessageType
     *            The MessageType the MessageProcessor implementation can
     *            process.
     */
    protected DeviceRequestMessageProcessor(final DeviceRequestMessageType deviceRequestMessageType) {
        this.deviceRequestMessageType = deviceRequestMessageType;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.iec61850RequestMessageProcessorMap.addMessageProcessor(this.deviceRequestMessageType.ordinal(),
                this.deviceRequestMessageType.name(), this);
    }

    // IDEA: change these two methods handleEmptyDeviceResponse and
    // handleScheduledEmptyDeviceResponse to have less double code

    protected void handleEmptyDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException ex = null;

        try {
            final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            ex = new TechnicalException(ComponentType.PROTOCOL_IEC61850, UNEXPECTED_EXCEPTION, e);
        }

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, ex, null, retryCount);

        responseMessageSender.send(responseMessage);
    }

    protected void handleGetStatusDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        DeviceStatus status = null;

        try {
            final GetStatusDeviceResponse response = (GetStatusDeviceResponse) deviceResponse;
            status = response.getDeviceStatus();
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                    "Unexpected exception while retrieving response message", e);
        }

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, osgpException, status, retryCount);

        responseMessageSender.send(responseMessage);
    }

    protected void handleGetConfigurationDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        Configuration configuration = null;

        try {
            final GetConfigurationDeviceResponse response = (GetConfigurationDeviceResponse) deviceResponse;

            configuration = response.getConfiguration();
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                    "Unexpected exception while retrieving response message", e);
        }

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, osgpException, configuration, retryCount);

        responseMessageSender.send(responseMessage);
    }

    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {
        LOGGER.error("Error while processing message", e);
        final OsgpException ex = new TechnicalException(ComponentType.PROTOCOL_IEC61850, UNEXPECTED_EXCEPTION, e);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage(domain, domainVersion,
                messageType, correlationUid, organisationIdentification, deviceIdentification,
                ResponseMessageResultType.NOT_OK, ex, null, retryCount);

        this.responseMessageSender.send(protocolResponseMessage);
    }

    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String domain, final String domainVersion, final String messageType) {
        LOGGER.error("Error while processing message", e);
        final OsgpException ex = new TechnicalException(ComponentType.PROTOCOL_IEC61850, UNEXPECTED_EXCEPTION, e);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage(domain, domainVersion,
                messageType, correlationUid, organisationIdentification, deviceIdentification,
                ResponseMessageResultType.NOT_OK, ex, null);

        this.responseMessageSender.send(protocolResponseMessage);
    }

    public void handleUnExpectedError(final DeviceResponse deviceResponse, final Throwable t,
            final Serializable messageData, final String domain, final String domainVersion, final String messageType,
            final boolean isScheduled, final int retryCount) {

        final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
        final OsgpException ex = new TechnicalException(ComponentType.PROTOCOL_IEC61850, t.getMessage());

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, ex, messageData, isScheduled, retryCount);

        this.responseMessageSender.send(responseMessage);
    }

    protected void handleExpectedError(final OsgpException e, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final String domain,
            final String domainVersion, final String messageType) {
        LOGGER.error("Expected error while processing message", e);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage(domain, domainVersion,
                messageType, correlationUid, organisationIdentification, deviceIdentification,
                ResponseMessageResultType.NOT_OK, e, null);

        this.responseMessageSender.send(protocolResponseMessage);
    }
}
