/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
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

import java.io.Closeable;

public class ResponseBuffers implements Closeable {
    private final ReplyHeader replyHeader;
    private final ByteBuf bodyByteBuffer;
    private final long elapsedNanoseconds;
    private volatile boolean isClosed;

    public ResponseBuffers(final ReplyHeader replyHeader, final ByteBuf bodyByteBuffer, final long elapsedNanoseconds) {
        this.replyHeader = replyHeader;
        this.bodyByteBuffer = bodyByteBuffer;
        this.elapsedNanoseconds = elapsedNanoseconds;
    }

    public ReplyHeader getReplyHeader() {
        return replyHeader;
    }

    /**
     * Returns a read-only buffer containing the response body.  Care should be taken to not use the returned buffer after this instance has
     * been closed.
     *
     * @return a read-only buffer containing the response body
     */
    public ByteBuf getBodyByteBuffer() {
        return bodyByteBuffer.asReadOnly();
    }

    public long getElapsedNanoseconds() {
        return elapsedNanoseconds;
    }

    @Override
    public void close() {
        if (!isClosed) {
            if (bodyByteBuffer != null) {
                bodyByteBuffer.close();
            }
            isClosed = true;
        }
    }
}
