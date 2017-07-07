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
package org.jclouds.b2.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.azure.storage.AzureStorageQueueApi;
import org.jclouds.azure.storage.features.QueueApi;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "QueueApiMockTest")
public final class QueueApiMockTest {
   public void testGetUploadUrl() throws Exception {
      MockWebServer server = createMockWebServer();
      // TODO: enqueue response

      try {
         QueueApi api = api(server.getUrl("/").toString(), "azure-queue-storage").getQueueApi();

         // TODO: run request
      } finally {
         server.shutdown();
      }
   }

   static MockWebServer createMockWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      URL url = server.getUrl("");
      return server;
   }

   static AzureStorageQueueApi api(String uri, String provider, Properties overrides) {
       Set<Module> modules = ImmutableSet.<Module> of(
             new ExecutorServiceModule(MoreExecutors.sameThreadExecutor()));

      return ContextBuilder.newBuilder(provider)
            .credentials("ACCOUNT_ID", "APPLICATION_KEY")
            .endpoint(uri)
            .overrides(overrides)
            .modules(modules)
            .buildApi(AzureStorageQueueApi.class);
   }

   static AzureStorageQueueApi api(String uri, String provider) {
      return api(uri, provider, new Properties());
   }
}
