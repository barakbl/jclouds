/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azure.storage;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import java.util.Date;
import java.util.Properties;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

public class AzureStorageQueueApiMetadata extends BaseHttpApiMetadata {

    @Override
    public ApiMetadata.Builder<?> toBuilder() {
        return null;
    }

    public AzureStorageQueueApiMetadata() {
        this(new Builder());
    }

    protected AzureStorageQueueApiMetadata(Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        Properties properties = BaseHttpApiMetadata.defaultProperties();
        properties.setProperty("x-ms-version", "2016-05-31");
        return properties;
    }

    public static class Builder extends BaseHttpApiMetadata.Builder<AzureStorageQueueApi, Builder> {

        protected Builder() {
            super(AzureStorageQueueApi.class);
            id("azure-queue-storage")
                    .name("Azure Queue Storage")
                    .identityName("Account")
                    .credentialName("Key")
//                    .documentation(URI.create("https://www.backblaze.com/b2/docs/"))
                    .defaultProperties(AzureStorageQueueApiMetadata.defaultProperties())
                    .defaultEndpoint("https://${jclouds.identity}.queue.core.windows.net/");
//                    .defaultModules(ImmutableSet.<Class<? extends Module>>of(
//                            B2HttpApiModule.class,
//                            B2BlobStoreContextModule.class));
        }

        @Override
        public AzureStorageQueueApiMetadata build() {
            return new AzureStorageQueueApiMetadata(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Builder fromApiMetadata(ApiMetadata in) {
            return this;
        }
    }
}
