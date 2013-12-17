#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#jclouds provider/api.
PROVIDER=transient

# Number of jclouds IO Threads
IO_THREADS=10

# Number of jclouds user threads
USER_THREADS=10

# Blobstore credentials
IDENTITY="myidentity"
CREDENTIAL="secretCreds"

# Container used for the test. For "transient" and "filesystem" providers,
# the container gets created at runtime. For others, it is expected to
# be present.
CONTAINER=jclouds-container
 
# Start BlobStoreBench. Note that for some providers, you might have to add
# additional params below.
# For e.g.
#   -Djclouds.keystone.credential-type=apiAccessKeyCredentials \
#   -Djclouds.endpoint="http://some-endpoint.blobstore.com" \
#   -Djclouds.strip-expect-header=true \
java -jar \
    -XX:+UseParNewGC -XX:+UseConcMarkSweepGC \
    -XX:-HeapDumpOnOutOfMemoryError \
    -XX:+AlwaysPreTouch \
    -Djclouds.provider=${PROVIDER} \
    -Djclouds.io-worker-threads=${IO_THREADS} \
    -Djclouds.user-threads=${USER_THREADS} \
    -Djclouds.identity=${IDENTITY} \
    -Djclouds.credential=${CREDENTIAL} \
    -Djclouds.filesystem.basedir=/tmp/blobstore \
    -Djclouds.trust-all-certs=true \
    target/jclouds-tools-1.8.0-SNAPSHOT-jar-with-dependencies.jar \
        WRITE \
        --container ${CONTAINER} \
        --num-retries 5 \
        --max-operations 10
