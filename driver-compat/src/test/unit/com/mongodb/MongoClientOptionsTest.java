/*
 * Copyright (c) 2008 - 2014 MongoDB, Inc.
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

package com.mongodb;

import org.junit.Assert;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class MongoClientOptionsTest {

    @Test
    public void testBuilderDefaults() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        MongoClientOptions options = builder.build();
        assertNull(options.getDescription());
        assertEquals(WriteConcern.ACKNOWLEDGED, options.getWriteConcern());
        assertEquals(0, options.getMinConnectionsPerHost());
        assertEquals(100, options.getConnectionsPerHost());
        assertEquals(10000, options.getConnectTimeout());
        assertEquals(ReadPreference.primary(), options.getReadPreference());
        assertEquals(5, options.getThreadsAllowedToBlockForConnectionMultiplier());
        assertFalse(options.isSocketKeepAlive());
        assertFalse(options.isSSLEnabled());
        assertEquals(DefaultDBDecoder.FACTORY, options.getDbDecoderFactory());
        assertEquals(DefaultDBEncoder.FACTORY, options.getDbEncoderFactory());
        assertEquals(0, options.getHeartbeatThreadCount());
    }

    @Test
    public void testIllegalArguments() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        try {
            builder.writeConcern(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }
        try {
            builder.readPreference(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }
        try {
            builder.connectionsPerHost(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }
        try {
            builder.minConnectionsPerHost(-1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }
        try {
            builder.connectTimeout(-1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }
        try {
            builder.threadsAllowedToBlockForConnectionMultiplier(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }

        try {
            builder.dbDecoderFactory(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }

        try {
            builder.dbEncoderFactory(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // NOPMD all good
        }

    }


    @Test
    public void testBuilderBuild() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.description("test");
        builder.readPreference(ReadPreference.secondary());
        builder.writeConcern(WriteConcern.JOURNAL_SAFE);
        builder.minConnectionsPerHost(30);
        builder.connectionsPerHost(500);
        builder.connectTimeout(100);
        builder.maxWaitTime(200);
        builder.maxConnectionIdleTime(300);
        builder.maxConnectionLifeTime(400);
        builder.threadsAllowedToBlockForConnectionMultiplier(1);
        builder.socketKeepAlive(true);
        builder.SSLEnabled(true);
        builder.dbDecoderFactory(LazyDBDecoder.FACTORY);
        builder.heartbeatFrequency(5);
        builder.heartbeatConnectRetryFrequency(10);
        builder.heartbeatConnectTimeout(15);
        builder.heartbeatSocketTimeout(20);
        builder.heartbeatThreadCount(4);
        builder.requiredReplicaSetName("test");

        DBEncoderFactory encoderFactory = new MyDBEncoderFactory();
        builder.dbEncoderFactory(encoderFactory);

        MongoClientOptions options = builder.build();

        assertEquals("test", options.getDescription());
        assertEquals(ReadPreference.secondary(), options.getReadPreference());
        assertEquals(WriteConcern.JOURNAL_SAFE, options.getWriteConcern());
        assertEquals(200, options.getMaxWaitTime());
        assertEquals(300, options.getMaxConnectionIdleTime());
        assertEquals(400, options.getMaxConnectionLifeTime());
        assertEquals(30, options.getMinConnectionsPerHost());
        assertEquals(500, options.getConnectionsPerHost());
        assertEquals(100, options.getConnectTimeout());
        assertEquals(1, options.getThreadsAllowedToBlockForConnectionMultiplier());
        assertTrue(options.isSocketKeepAlive());
        assertTrue(options.isSSLEnabled());
        assertEquals(LazyDBDecoder.FACTORY, options.getDbDecoderFactory());
        assertEquals(encoderFactory, options.getDbEncoderFactory());
        assertEquals(5, options.getHeartbeatFrequency());
        assertEquals(10, options.getHeartbeatConnectRetryFrequency());
        assertEquals(15, options.getHeartbeatConnectTimeout());
        assertEquals(20, options.getHeartbeatSocketTimeout());
        assertEquals(4, options.getHeartbeatThreadCount());
        assertEquals("test", options.getRequiredReplicaSetName());

        assertEquals(5, options.getServerSettings().getHeartbeatFrequency(MILLISECONDS));
        assertEquals(10, options.getServerSettings().getHeartbeatConnectRetryFrequency(MILLISECONDS));
        assertEquals(4, options.getServerSettings().getHeartbeatThreadCount());
    }

    private static class MyDBEncoderFactory implements DBEncoderFactory {
        @Override
        public DBEncoder create() {
            return new DefaultDBEncoder();
        }
    }
}
