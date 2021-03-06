/*
 * Copyright 2019 Nick Rammos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nickrm.jflux.api.exception;

/**
 * Thrown to indicate that a call to the InfluxDB API was incorrect (e.g. syntax error or wrong
 * database name).
 *
 * @since 1.0.0
 */
public final class InfluxClientException extends RuntimeException {

    /**
     * Initializes a new instance setting the message.
     *
     * @param message the message for this exception
     */
    public InfluxClientException(String message) {
        super(message);
    }
}
