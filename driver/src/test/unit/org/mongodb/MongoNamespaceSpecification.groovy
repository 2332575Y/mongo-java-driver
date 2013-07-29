package org.mongodb

import spock.lang.Specification

class MongoNamespaceSpecification extends Specification {
    def 'null database name should throw IllegalArgumentException'() {
        when:
        new MongoNamespace(null, 'test');

        then:
        thrown(IllegalArgumentException)
    }

    def 'null collection name should throw IllegalArgumentException'() {
        when:
        new MongoNamespace('test', null);

        then:
        thrown(IllegalArgumentException)
    }

    def 'test getters'() {
        when:
        MongoNamespace namespace = new MongoNamespace('db', 'coll');

        then:
        namespace.getDatabaseName() == 'db'
        namespace.getCollectionName() == 'coll'
        namespace.getFullName() == 'db.coll'
    }

    def 'asNamespaceString should return correct string'() {
        expect:
        MongoNamespace.asNamespaceString('db', 'coll') == 'db.coll'
    }

    @SuppressWarnings('ComparisonWithSelf')
    def 'testEqualsAndHashCode'() {
        given:
        MongoNamespace namespace1 = new MongoNamespace('db1', 'coll1');
        MongoNamespace namespace2 = new MongoNamespace('db1', 'coll1');
        MongoNamespace namespace3 = new MongoNamespace('db2', 'coll1');
        MongoNamespace namespace4 = new MongoNamespace('db1', 'coll2');

        expect:
        namespace1 != new Object()
        namespace1 == namespace1;
        namespace1 == namespace2;
        namespace1 != namespace3;
        namespace1 != namespace4;

        namespace1.hashCode() == 97917362
    }
}
