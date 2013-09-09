/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.connection;

import org.bson.ByteBuf;
import org.mongodb.annotations.NotThreadSafe;

import java.util.List;

/**
 * A connection to a MongoDB server with blocking operations.
 * <p>
 * This class is not completely thread safe.  At most one thread can have an active call to sendMessage, and one thread an active call
 * to receiveMessage.
 * </p>
 *
 * @since 3.0
 */
@NotThreadSafe
interface InternalConnection {

    /**
     * Gets the server address of this connection
     */
    ServerAddress getServerAddress();

    /**
     * Gets the id of the connection.  If possible, this id will correlate with the connection id that the server puts in its log messages.
     * @return the id
     */
    String getId();

    /**
     * Send a message to the server. The connection may not make any attempt to validate the integrity of the message.
     *
     * @param byteBuffers the list of byte buffers to send.
     */
    void sendMessage(final List<ByteBuf> byteBuffers);

    /**
     * Receive a response to a sent message from the server.

     * @return the response
     */
    ResponseBuffers receiveMessage();

    /**
     * Asynchronously send a message to the server. The connection may not make any attempt to validate the integrity of the message.
     *
     * @param byteBuffers the list of byte buffers to send
     * @param callback the callback to invoke on completion
     */
    void sendMessageAsync(List<ByteBuf> byteBuffers, SingleResultCallback<Void> callback);

    /**
     * Asynchronously receive a response to a sent message from the server.
     *
     * @param callback the callback to invoke on completion
     */
    void receiveMessageAsync(SingleResultCallback<ResponseBuffers> callback);

    /**
     * Closes the connection.
     */
    void close();

    /**
     * Returns the closed state of the connection
     *
     * @return true if connection is closed
     */
    boolean isClosed();
}
