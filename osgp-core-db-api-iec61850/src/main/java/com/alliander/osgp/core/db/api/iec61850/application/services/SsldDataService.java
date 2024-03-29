/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.db.api.iec61850.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.db.api.iec61850.entities.DeviceOutputSetting;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850.repositories.SsldDataRepository;
import com.alliander.osgp.core.db.api.iec61850valueobjects.RelayType;

@Service
@Transactional(value = "iec61850OsgpCoreDbApiTransactionManager", readOnly = true)
public class SsldDataService {

    @Autowired
    private SsldDataRepository ssldDataRepository;

    public Ssld findDevice(final String deviceIdentification) {
        return this.ssldDataRepository.findByDeviceIdentification(deviceIdentification);
    }

    /**
     * Returns the external index, corresponding to the given internal index.
     *
     * Throws an exception if the index is not found.
     */
    public int convertToExternalIndex(final Ssld ssld, final int internalIndex) {

        int output = 0;

        for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
            if (deviceOutputSetting.getInternalId() == internalIndex) {
                output = deviceOutputSetting.getExternalId();
            }
        }

        if (output == 0) {
            throw new IllegalArgumentException("Unknown external id");
        }

        return output;
    }

    /**
     * Returns the internal index, corresponding to the given external index.
     *
     * Throws an exception if the index is not found.
     */
    public int convertToInternalIndex(final Ssld ssld, final int externalIndex) {

        final DeviceOutputSetting deviceOutputSetting = this
                .getDeviceOutputSettingForExternalIndex(ssld, externalIndex);

        if (deviceOutputSetting == null) {
            throw new IllegalArgumentException("Unknown internal id");

        } else {
            return deviceOutputSetting.getInternalId();
        }
    }

    /**
     * Returns a list of all {@link DeviceOutputSetting}s for the given
     * {@link RelayType}
     */
    public List<DeviceOutputSetting> findByRelayType(final Ssld ssld, final RelayType relayType) {

        final List<DeviceOutputSetting> output = new ArrayList<>();

        for (final DeviceOutputSetting d : ssld.getOutputSettings()) {
            if (relayType.equals(d.getRelayType())) {
                output.add(d);
            }
        }

        return output;
    }

    /**
     * Returns the {@link DeviceOutputSetting} for the given external index
     */
    public DeviceOutputSetting getDeviceOutputSettingForExternalIndex(final Ssld ssld, final int index) {
        for (final DeviceOutputSetting d : ssld.getOutputSettings()) {
            if (d.getExternalId() == index) {
                return d;
            }
        }

        return null;
    }

    /**
     * Returns the {@link DeviceOutputSetting} for the given internal index
     */
    public DeviceOutputSetting getDeviceOutputSettingForInternalIndex(final Ssld ssld, final int index) {
        for (final DeviceOutputSetting d : ssld.getOutputSettings()) {
            if (d.getInternalId() == index) {
                return d;
            }
        }

        return null;
    }
}
