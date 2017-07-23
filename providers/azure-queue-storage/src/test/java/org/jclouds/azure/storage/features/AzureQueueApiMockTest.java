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
package org.jclouds.azure.storage.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azure.storage.domain.internals.QueueMessage;
import org.jclouds.azure.storage.domain.internals.QueueResponse.CreateQueueResponse;
import org.jclouds.azure.storage.domain.internals.QueueResponse.DeleteQueueResponse;
import org.jclouds.azure.storage.domain.internals.QueueResponse.ListQueueResponse;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.azure.storage.features.AzureQueueTestUtils.*;


@Test(groups = "unit", testName = "AzureQueueApiMockTest")
public class AzureQueueApiMockTest {
   private List<QueueMessage> response;

   public void testCreate() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setResponseCode(201));
      try {
         QueueApi api = api(server.getUrl("/").toString(), "azure-queue-storage").getQueueApi();
         CreateQueueResponse response = api.create("1");
         assertThat(response.isSuccess()).isTrue();
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      try {
         QueueApi api = api(server.getUrl("/").toString(), "azure-queue-storage").getQueueApi();
         DeleteQueueResponse response = api.delete("1");
         assertThat(response.isSuccess()).isTrue();
      } finally {
         server.shutdown();
      }
   }

   public void testList() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/list_queue_response.xml")));

      try {
         QueueApi api = api(server.getUrl("/").toString(), "azure-queue-storage").getQueueApi();
         ListQueueResponse response = api.list();

         assertThat(response.getQueues().size()).isEqualTo(3);
         assertThat(response.getQueues().get(0).getName()).isEqualTo("myqueue");
         assertThat(response.getQueues().get(0).getUrl()).isEqualTo("https://jcloudsazure.queue.core.windows.net/myqueue");
      } finally {
         server.shutdown();
      }
   }

   /*public void testGetService() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/get_queue_services_response.xml")));

      try{
         QueueApi api = api(server.getUrl("/").toString(), "azure-queue-storage").getQueueApi();
         GetQueueServicesResponse response = api.getService();

      } finally {
         server.shutdown();
      }
   }*/
}