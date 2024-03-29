/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.db.api.iec61850valueobjects;

import com.alliander.osgp.dto.valueobjects.DomainType;

public enum RelayType {
    LIGHT {
        @Override
        public DomainType domainType() {
            return DomainType.PUBLIC_LIGHTING;
        }
    },
    TARIFF {
        @Override
        public DomainType domainType() {
            return DomainType.TARIFF_SWITCHING;
        }
    },
    TARIFF_REVERSED {
        @Override
        public DomainType domainType() {
            return DomainType.TARIFF_SWITCHING;
        }
    };

    public abstract DomainType domainType();
}
