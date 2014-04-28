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

package com.mongodb
import org.mongodb.connection.Cluster
import org.mongodb.connection.ClusterDescription
import org.mongodb.connection.ServerDescription
import spock.lang.Specification
import spock.lang.Subject

import static java.util.concurrent.TimeUnit.SECONDS

class ReplicaSetStatusSpecification extends Specification {
    private final ClusterDescription clusterDescription = Mock();
    private final Cluster cluster = Mock();

    @Subject
    private final ReplicaSetStatus replicaSetStatus = new ReplicaSetStatus(cluster);

    def setup() {
        cluster.getDescription(10, SECONDS) >> clusterDescription;
    }

    def 'should return replica set name'() {
        given:
        String setName = 'repl0';
        ServerDescription serverDescription = Mock();

        serverDescription.getSetName() >> setName;
        clusterDescription.getAnyPrimaryOrSecondary() >> [serverDescription]

        expect:
        replicaSetStatus.getName() == setName;
    }

    def 'should return null if no servers'() {
        given:
        clusterDescription.getAnyPrimaryOrSecondary() >> []

        expect:
        replicaSetStatus.getName() == null;
    }

    def 'should return null if master not defined'() {
        given:
        clusterDescription.getPrimaries() >> [];

        expect:
        replicaSetStatus.getMaster() == null;
    }

    def 'should return master'() throws UnknownHostException {
        given:
        ServerDescription serverDescription = Mock();
        serverDescription.getAddress() >> new ServerAddress('localhost').toNew()
        clusterDescription.getPrimaries() >> [serverDescription]

        expect:
        replicaSetStatus.getMaster() != null;
    }

    def 'should test specific server for being master or not'() throws UnknownHostException {
        given:
        ServerDescription primaryDescription = Mock();
        primaryDescription.getAddress() >> new ServerAddress('localhost', 3000).toNew()
        clusterDescription.getPrimaries() >> [primaryDescription]

        expect:
        replicaSetStatus.isMaster(new ServerAddress('localhost', 3000));
        !replicaSetStatus.isMaster(new ServerAddress('localhost', 4000));
    }


    def 'should return max bson object size'() {
        given:
        ServerDescription serverDescription = Mock()
        serverDescription.getMaxDocumentSize() >> 47;
        clusterDescription.getPrimaries() >> [serverDescription];

        expect:
        replicaSetStatus.getMaxBsonObjectSize() == 47;
    }

}
