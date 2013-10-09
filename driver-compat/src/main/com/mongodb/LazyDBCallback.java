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

import org.bson.LazyBSONCallback;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.List;

public class LazyDBCallback extends LazyBSONCallback implements DBCallback {

    //not private, because required by LazyWriteableDBCallback.
    final DB db;

    public LazyDBCallback(final DBCollection collection) {
        this.db = collection == null ? null : collection.getDB();
    }

    @Override
    public Object createObject(final byte[] bytes, final int offset) {
        LazyDBObject document = new LazyDBObject(bytes, offset, this);
        Iterator<String> iterator = document.keySet().iterator();
        if (iterator.hasNext() && iterator.next().equals("$ref") && iterator.hasNext() && iterator.next().equals("$id")) {
            return new DBRef(db, document);
        }
        return document;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List createArray(final byte[] bytes, final int offset) {
        return new LazyDBList(bytes, offset, this);
    }

    @Override
    public Object createDBRef(final String ns, final ObjectId id) {
        return new DBRef(db, ns, id);
    }
}
