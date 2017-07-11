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

<<<<<<< HEAD
=======
import java.util.Properties;

>>>>>>> a1570e15dc340f17ba9a959c34e6272c113690eb
import com.google.auto.service.AutoService;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

@AutoService(ProviderMetadata.class)
public final class AzureStorageQueueProviderMetadata extends BaseProviderMetadata {
    @Override
    public Builder toBuilder() {
        return new Builder().fromProviderMetadata(this);
    }

    public AzureStorageQueueProviderMetadata() {
        this(new Builder());
    }

    protected AzureStorageQueueProviderMetadata(Builder builder) {
        super(builder);
    }

<<<<<<< HEAD
=======
    public static Properties defaultProperties() {
        Properties properties = AzureStorageQueueApiMetadata.defaultProperties();
        return properties;
    }

>>>>>>> a1570e15dc340f17ba9a959c34e6272c113690eb
    public static class Builder extends BaseProviderMetadata.Builder {

        protected Builder() {
            id("azure-queue-storage")
                    .name("Azure Queue Storage")
                    .apiMetadata(new AzureStorageQueueApiMetadata())
                    .endpoint("https://${jclouds.identity}.queue.core.windows.net/")
<<<<<<< HEAD
                    .defaultProperties(AzureStorageQueueApiMetadata.defaultProperties());
//                    .defaultProperties(B2ProviderMetadata.defaultProperties());
=======
                    .defaultProperties(AzureStorageQueueProviderMetadata.defaultProperties());
>>>>>>> a1570e15dc340f17ba9a959c34e6272c113690eb
        }

        @Override
        public AzureStorageQueueProviderMetadata build() {
            return new AzureStorageQueueProviderMetadata(this);
        }

        @Override
        public Builder fromProviderMetadata(ProviderMetadata in) {
            return this;
        }
    }
}
