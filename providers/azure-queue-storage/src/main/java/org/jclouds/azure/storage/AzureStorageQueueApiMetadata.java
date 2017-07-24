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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.azure.storage.config.AzureStorageQueueModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import java.net.URI;
import java.util.Properties;


public class AzureStorageQueueApiMetadata extends BaseHttpApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
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
                 .documentation(URI.create("https://docs.microsoft.com/en-us/rest/api/storageservices/queue-service-rest-api"))
                 .defaultProperties(AzureStorageQueueApiMetadata.defaultProperties())
                 .defaultEndpoint("https://${jclouds.identity}.queue.core.windows.net/")
                 .defaultModules(ImmutableSet.<Class<? extends Module>>of(AzureStorageQueueModule.class))
                 .version("2016-05-31");
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
