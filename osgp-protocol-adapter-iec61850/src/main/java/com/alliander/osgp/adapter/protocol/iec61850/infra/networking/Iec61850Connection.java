/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ServerModel;

public class Iec61850Connection {

    private ClientAssociation clientAssociation;

    private ServerModel serverModel;

    public Iec61850Connection(final ClientAssociation clientAssociation, final ServerModel serverModel) {
        this.clientAssociation = clientAssociation;
        this.serverModel = serverModel;
    }

    public ClientAssociation getClientAssociation() {
        return this.clientAssociation;
    }

    public ServerModel getServerModel() {
        return this.serverModel;
    }
}