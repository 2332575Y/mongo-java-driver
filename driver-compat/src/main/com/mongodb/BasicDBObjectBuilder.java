/*
 * Copyright (c) 2008 - 2012 10gen, Inc. <http://10gen.com>
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * utility for building complex objects example: BasicDBObjectBuilder.start().add( "name" , "eliot" ).add( "number" , 17
 * ).get()
 */
@SuppressWarnings({ "rawtypes" })
public class BasicDBObjectBuilder {

    /**
     * creates an empty object
     */
    public BasicDBObjectBuilder() {
        _stack = new LinkedList<DBObject>();
        _stack.add(new BasicDBObject());
    }

    /**
     * Creates an empty object
     *
     * @return The new empty builder
     */
    public static BasicDBObjectBuilder start() {
        return new BasicDBObjectBuilder();
    }

    /**
     * creates an object with the given key/value
     *
     * @param k   The field name
     * @param val The value
     */
    public static BasicDBObjectBuilder start(final String k, final Object val) {
        return (new BasicDBObjectBuilder()).add(k, val);
    }

    /**
     * Creates an object builder from an existing map.
     *
     * @param m map to use
     * @return the new builder
     */
    @SuppressWarnings("unchecked")
    public static BasicDBObjectBuilder start(final Map m) {
        final BasicDBObjectBuilder b = new BasicDBObjectBuilder();
        final Iterator<Map.Entry> i = m.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry entry = i.next();
            b.add(entry.getKey().toString(), entry.getValue());
        }
        return b;
    }

    /**
     * appends the key/value to the active object
     *
     * @param key
     * @param val
     * @return returns itself so you can chain
     */
    public BasicDBObjectBuilder append(final String key, final Object val) {
        _cur().put(key, val);
        return this;
    }


    /**
     * same as appends
     *
     * @param key
     * @param val
     * @return returns itself so you can chain
     * @see #append(String, Object)
     */
    public BasicDBObjectBuilder add(final String key, final Object val) {
        return append(key, val);
    }

    /**
     * creates an new empty object and inserts it into the current object with the given key. The new child object
     * becomes the active one.
     *
     * @param key
     * @return returns itself so you can chain
     */
    public BasicDBObjectBuilder push(final String key) {
        final BasicDBObject o = new BasicDBObject();
        _cur().put(key, o);
        _stack.addLast(o);
        return this;
    }

    /**
     * pops the active object, which means that the parent object becomes active
     *
     * @return returns itself so you can chain
     */
    public BasicDBObjectBuilder pop() {
        if (_stack.size() <= 1) {
            throw new IllegalArgumentException("can't pop last element");
        }
        _stack.removeLast();
        return this;
    }

    /**
     * gets the base object
     *
     * @return The base object
     */
    public DBObject get() {
        return _stack.getFirst();
    }

    /**
     * returns true if no key/value was inserted into base object
     *
     * @return True if empty
     */
    public boolean isEmpty() {
        return ((BasicDBObject) _stack.getFirst()).size() == 0;
    }

    private DBObject _cur() {
        return _stack.getLast();
    }

    private final LinkedList<DBObject> _stack;

}
