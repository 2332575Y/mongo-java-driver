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

package com.mongodb;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.String.format;

/**
 * Represents a database address
 */
public class DBAddress extends ServerAddress {
    private final String _db;

    /**
     * Creates a new address
     * Accepts as the parameter format:
     * <table border="1">
     * <tr>
     * <td><i>name</i></td>
     * <td>"mydb"</td>
     * </tr>
     * <tr>
     * <td><i>&lt;host&gt;/name</i></td>
     * <td>"127.0.0.1/mydb"</td>
     * </tr>
     * <tr>
     * <td><i>&lt;host&gt;:&lt;port&gt;/name</i></td>
     * <td>"127.0.0.1:8080/mydb"</td>
     * </tr>
     * </table>
     *
     * @param urlFormat the URL-formatted host and port
     * @throws UnknownHostException
     * @see MongoClientURI
     */
    public DBAddress(final String urlFormat) throws UnknownHostException {
        super(_getHostSection(urlFormat));

        _check(urlFormat, "urlFormat");
        _db = _fixName(_getDBSection(urlFormat));

        _check(getHost(), "host");
        _check(_db, "db");
    }

    static String _getHostSection(final String urlFormat) {
        if (urlFormat == null) {
            throw new NullPointerException("urlFormat can't be null");
        }

        int idx = urlFormat.indexOf("/");
        if (idx >= 0) {
            return urlFormat.substring(0, idx);
        }
        return null;
    }

    static String _getDBSection(final String urlFormat) {
        if (urlFormat == null) {
            throw new NullPointerException("urlFormat can't be null");
        }

        int idx = urlFormat.indexOf("/");
        if (idx >= 0) {
            return urlFormat.substring(idx + 1);
        }
        return urlFormat;
    }

    static String _fixName(final String name) {
        return name.replace('.', '-');
    }

    /**
     * @param other        an existing {@code DBAddress} that gives the host and port
     * @param databaseName the database to which to connect
     * @throws UnknownHostException
     */
    public DBAddress(final DBAddress other, final String databaseName) throws UnknownHostException {
        this(other.getHost(), other.getPort(), databaseName);
    }

    /**
     * @param host         host name
     * @param databaseName database name
     * @throws UnknownHostException
     */
    public DBAddress(final String host, final String databaseName) throws UnknownHostException {
        this(host, defaultPort(), databaseName);
    }

    /**
     * @param host         host name
     * @param port         database port
     * @param databaseName database name
     * @throws UnknownHostException
     */
    public DBAddress(final String host, final int port, final String databaseName) throws UnknownHostException {
        super(host, port);
        _db = databaseName.trim();
    }

    /**
     * @param inetAddress  host address
     * @param port         database port
     * @param databaseName database name
     */
    public DBAddress(final InetAddress inetAddress, final int port, final String databaseName) {
        super(inetAddress, port);
        _check(databaseName, "name");
        _db = databaseName.trim();
    }

    static void _check(final String thing, final String name) {
        if (thing == null) {
            throw new NullPointerException(name + " can't be null ");
        }

        String trimmedThing = thing.trim();
        if (trimmedThing.length() == 0) {
            throw new IllegalArgumentException(name + " can't be empty");
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() + _db.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof DBAddress) {
            DBAddress a = (DBAddress) other;
            return a.getPort() == getPort() &&
                   a._db.equals(_db) &&
                   a.getHost().equals(getHost());
        } else if (other instanceof ServerAddress) {
            return other.equals(this);
        }
        return false;
    }

    /**
     * Creates a DBAddress pointing to a different database on the same server.
     *
     * @param name database name
     * @return the DBAddress for the given name with the same host and port as this
     * @throws MongoException
     */
    public DBAddress getSister(final String name) {
        try {
            return new DBAddress(getHost(), getPort(), name);
        } catch (UnknownHostException uh) {
            throw new MongoInternalException(format("UnknownHostException thrown for %s:%s.", getHost(), getPort()), uh);
        }
    }

    /**
     * gets the database name
     *
     * @return the database name
     */
    public String getDBName() {
        return _db;
    }

    /**
     * gets a String representation of address as host:port/databaseName.
     *
     * @return this address
     */
    @Override
    public String toString() {
        return super.toString() + "/" + _db;
    }

}
