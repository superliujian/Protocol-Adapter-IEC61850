/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.util.HashMap;

/**
 * Factory class which can create {@link Iec61850Client} instances and caches
 * created instances.
 */
public class Iec61850ClientFactory {

    private final HashMap<String, Iec61850Client> cache = new HashMap<>();
}
