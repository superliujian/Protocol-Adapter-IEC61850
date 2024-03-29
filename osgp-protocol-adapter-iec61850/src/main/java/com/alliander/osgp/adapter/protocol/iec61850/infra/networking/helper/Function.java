/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

/**
 * An interface, containing a function that can be applied. <R> is the given
 * return type. Can be {@link Void}
 */
public interface Function<R> {

    /**
     * The function, containing the bussiness logic of the Function
     */
    R apply() throws Exception;

}
