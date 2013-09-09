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



package org.mongodb

import spock.lang.Specification

import static org.mongodb.AuthenticationMechanism.MONGODB_CR
import static org.mongodb.AuthenticationMechanism.PLAIN

class MongoCredentialSpecification extends Specification {
    def 'creating a challenge-response credential should populate correct fields'() {
        given:
        AuthenticationMechanism mechanism = MONGODB_CR;
        String userName = 'user';
        String database = 'test';
        char[] password = 'pwd'.toCharArray();

        when:
        MongoCredential credential = MongoCredential.createMongoCRCredential(userName, database, password);

        then:
        mechanism == credential.getMechanism()
        userName == credential.getUserName()
        database == credential.getSource()
        password == credential.getPassword()
        MONGODB_CR == credential.getMechanism()
    }

    def 'should throw IllegalArgumentException when required parameter is not supplied for challenge-response'() {
        when:
        MongoCredential.createMongoCRCredential(null, 'test', 'pwd'.toCharArray());
        then:
        thrown(IllegalArgumentException)

        when:
        MongoCredential.createMongoCRCredential('user', null, 'pwd'.toCharArray());
        then:
        thrown(IllegalArgumentException)

        when:
        MongoCredential.createMongoCRCredential('user', 'test', null);
        then:
        thrown(IllegalArgumentException)
    }

    def 'creating a Plain credential should populate all required fields'() {
        given:
        AuthenticationMechanism mechanism = PLAIN;
        String userName = 'user';
        String source = '$external';
        char[] password = 'pwd'.toCharArray();

        when:
        MongoCredential credential = MongoCredential.createPlainCredential(userName, source, password);

        then:
        mechanism == credential.getMechanism()
        userName == credential.getUserName()
        source == credential.getSource()
        password == credential.getPassword()
        mechanism == credential.getMechanism()
    }

    def 'should throw IllegalArgumentException when a required field is not passed in'() {
        when:
        MongoCredential.createPlainCredential(null, '$external', 'pwd'.toCharArray());
        then:
        thrown(IllegalArgumentException)

        when:
        MongoCredential.createPlainCredential('user', '$external', null);
        then:
        thrown(IllegalArgumentException)

        when:
        MongoCredential.createPlainCredential('user', null, 'pwd'.toCharArray());
        then:
        thrown(IllegalArgumentException)
    }

    def 'creating a GSSAPI Credential should populate the correct fields'() {
        given:
        AuthenticationMechanism mechanism = AuthenticationMechanism.GSSAPI;
        String userName = 'user';

        when:
        MongoCredential credential = MongoCredential.createGSSAPICredential(userName);

        then:
        mechanism == credential.getMechanism()
        userName == credential.getUserName()
        '$external' == credential.getSource()
        null == credential.getPassword()
    }

    def 'creating an X.509 Credential should populate the correct fields'() {
        given:
        AuthenticationMechanism mechanism = AuthenticationMechanism.MONGODB_X509
        String userName = 'user'

        when:
        MongoCredential credential = MongoCredential.createMongoX509Credential(userName)

        then:
        mechanism == credential.getMechanism()
        userName == credential.getUserName()
        '$external' == credential.getSource()
        null == credential.getPassword()
    }

    def 'should get default value of mechanism property when there is no mapping'() {
        when:
        def credential = MongoCredential.createGSSAPICredential('user')

        then:
        credential.getMechanismProperty('unmappedKey', 'mongodb') == 'mongodb'
    }

    def 'should get mapped mechanism properties when there is a mapping'() {
        given:
        String firstKey = 'firstKey'
        String firstValue = 'firstValue'
        String secondKey = 'secondKey'
        Integer secondValue = 2

        when:
        def credential = MongoCredential.createGSSAPICredential('user').withMechanismProperty(firstKey, firstValue)

        then:
        credential.getMechanismProperty(firstKey, 'default') == firstValue

        when:
        credential = credential.withMechanismProperty(secondKey, secondValue)

        then:
        credential.getMechanismProperty(firstKey, 'default') == firstValue
        credential.getMechanismProperty(secondKey, 1) == secondValue
    }

    def 'should preserve other properties when adding a mechanism property'() {
        given:
        def  credential = MongoCredential.createPlainCredential('user', 'source', 'pwd'.toCharArray())

        when:
        def newCredential = credential.withMechanismProperty('foo', 'bar')

        then:
        newCredential.mechanism == credential.mechanism
        newCredential.userName == credential.userName
        newCredential.password == credential.password
        newCredential.source == credential.source
    }

    def 'should throw IllegalArgumentException if username is not provided to a GSSAPI credential'() {
        when:
        MongoCredential.createGSSAPICredential(null);

        then:
        thrown(IllegalArgumentException)
    }

    def 'testObjectOverrides'() {
        given:
        String userName = 'user'
        String database = 'test'
        def password = 'pwd'
        def propertyKey = 'keyOne'
        def propertyValue = 'valueOne'

        when:
        def credentialOne = MongoCredential.createMongoCRCredential(userName, database, password.toCharArray())
        def credentialTwo = credentialOne.withMechanismProperty(propertyKey, propertyValue)

        then:
        MongoCredential.createMongoCRCredential(userName, database, password.toCharArray()) == credentialOne
        credentialOne.withMechanismProperty(propertyKey, propertyValue) == credentialTwo
        credentialOne != credentialTwo

        MongoCredential.createMongoCRCredential(userName, database, password.toCharArray()).hashCode() == credentialOne.hashCode()
        credentialOne.hashCode() != credentialTwo.hashCode()

        !credentialOne.toString().contains(password)
    }
}
