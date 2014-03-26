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

package org.mongodb.connection.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.mongodb.connection.SSLSettings;
import org.mongodb.connection.ServerAddress;
import org.mongodb.connection.SocketSettings;
import org.mongodb.connection.Stream;
import org.mongodb.connection.StreamFactory;

import static org.mongodb.assertions.Assertions.notNull;

/**
 * A StreamFactory for Streams based on Netty 4.0
 *
 * @since 3.0
 */
public class NettyStreamFactory implements StreamFactory {
    private final SocketSettings settings;
    private final SSLSettings sslSettings;
    private final EventLoopGroup eventLoopGroup;
    private final ByteBufAllocator allocator;

    /**
     * Construct a new instance of the factory.
     *
     * @param settings the socket settings
     * @param sslSettings the SSL settings
     * @param eventLoopGroup the event loop group that all channels created by this factory will be a part of
     * @param allocator the allocator to use for ByteBuf instances
     */
    public NettyStreamFactory(final SocketSettings settings, final SSLSettings sslSettings, final NioEventLoopGroup eventLoopGroup,
                              final ByteBufAllocator allocator) {
        this.settings = notNull("settings", settings);
        this.sslSettings = notNull("sslSettings", sslSettings);
        this.eventLoopGroup = notNull("eventLoopGroup", eventLoopGroup);
        this.allocator = notNull("allocator", allocator);
    }

    /**
     * Construct a new instance of the factory with a default allocator and event loop group.
     *
     * @param settings the socket settings
     * @param sslSettings the SSL settings
     */
    public NettyStreamFactory(final SocketSettings settings, final SSLSettings sslSettings) {
        this(settings, sslSettings, new NioEventLoopGroup(), PooledByteBufAllocator.DEFAULT);
    }

    @Override
    public Stream create(final ServerAddress serverAddress) {
        return new NettyStream(serverAddress, settings, sslSettings, eventLoopGroup, allocator);
    }

}
