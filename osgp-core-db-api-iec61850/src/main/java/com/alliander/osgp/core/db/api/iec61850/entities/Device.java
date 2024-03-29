/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.db.api.iec61850.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

/**
 * Stripped down version of the platform Device class
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Device implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4119222173415540822L;

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    /**
     * Creation time of this entity. This field is set by { @see
     * this.prePersist() }.
     */
    @Column(nullable = false)
    protected Date creationTime = new Date();

    /**
     * Modification time of this entity. This field is set by { @see
     * this.preUpdate() }.
     */
    @Column(nullable = false)
    protected Date modificationTime = new Date();

    /**
     * Version of this entity.
     */
    @Version
    private Long version = -1L;

    /**
     * Device identification of a device. This is the main value used to find a
     * device.
     */
    @Column(unique = true, nullable = false, length = 40)
    protected String deviceIdentification;

    /**
     * Indicates the type of the device. Example { @see Ssld.SSLD_TYPE }
     */
    @Column()
    protected String deviceType;

    public Device() {
        // Default constructor
    }

    public Device(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Device(final String deviceIdentification, final String alias, final String containerCity,
            final String containerPostalCode, final String containerStreet, final String containerNumber,
            final String containerMunicipality, final Float gpsLatitude, final Float gpsLongitude) {
        this.deviceIdentification = deviceIdentification;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Device device = (Device) o;
        return Objects.equals(this.deviceIdentification, device.deviceIdentification);
    }

    public final Date getCreationTime() {
        return (Date) this.creationTime.clone();
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public final Long getId() {
        return this.id;
    }

    public final Date getModificationTime() {
        return (Date) this.modificationTime.clone();
    }

    public final Long getVersion() {
        return this.version;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.deviceIdentification);
    }

    /**
     * Method for actions to be taken before inserting.
     */
    @PrePersist
    private void prePersist() {
        final Date now = new Date();
        this.creationTime = now;
        this.modificationTime = now;
    }

    /**
     * Method for actions to be taken before updating.
     */
    @PreUpdate
    private void preUpdate() {
        this.modificationTime = new Date();
    }

    public void setVersion(final Long newVersion) {
        this.version = newVersion;
    }

    /**
     * This setter is only needed for testing. Don't use this in production
     * code.
     *
     * @param id
     *            The id.
     */
    public void setId(final Long id) {
        this.id = id;
    }
}
