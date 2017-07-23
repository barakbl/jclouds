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
import org.jclouds.azure.storage.domain.internals.MessageResponse.GetQueueResponse;
import org.jclouds.azure.storage.domain.internals.MessageResponse.PostQueueResponse;
import org.jclouds.azure.storage.domain.internals.QueueMessage;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.azure.storage.features.AzureQueueTestUtils.*;


@Test(groups = "unit", testName = "AzureQueueApiMockTest")
public class AzureMessageApiMockTest {
   private List<QueueMessage> response;

   public void testGet() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/get_queue_response.xml")));

      try{
         MessageApi api = api(server.getUrl("/").toString(), "azure-queue-storage").getMessageApi();
         GetQueueResponse response = api.get("myqueue", 2);

         assertThat(response.getQueueMessage().size()).isEqualTo(2);
         assertThat(response.getQueueMessage().get(0).getMessageId()).isEqualTo("0f79669d-e06c-4735-b61d-f7e65099d54c");
         assertThat(response.getQueueMessage().get(0).getInsertionTime()).isEqualTo("Thu, 20 Jul 2017 06:17:12 GMT");
         assertThat(response.getQueueMessage().get(0).getExpirationTime()).isEqualTo("Thu, 27 Jul 2017 06:17:12 GMT");
         assertThat(response.getQueueMessage().get(0).getPopReceipt()).isEqualTo("AgAAAAMAAAAAAAAAUKlM7h8B0wE=");
         assertThat(response.getQueueMessage().get(0).getTimeNextVisible()).isEqualTo("Thu, 20 Jul 2017 06:17:57 GMT");
         assertThat(response.getQueueMessage().get(0).getDequeueCount()).isEqualTo(0);
         assertThat(response.getQueueMessage().get(0).getMessageText()).isEqualTo("YXNkZmdoams=");
      } finally {
         server.shutdown();
      }
   }

   public void testPost() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/post_queue_response.xml")));

      try{
         MessageApi api = api(server.getUrl("/").toString(), "azure-queue-storage").getMessageApi();
         PostQueueResponse response = api.post("myqueue","1234567");
         assertThat(response.getQueueMessage().get(0).getMessageId()).isEqualTo("string-message-id");
         assertThat(response.getQueueMessage().get(0).getInsertionTime()).isEqualTo("insertion-time");
         assertThat(response.getQueueMessage().get(0).getExpirationTime()).isEqualTo("expiration-time");
         assertThat(response.getQueueMessage().get(0).getPopReceipt()).isEqualTo("opaque-string-receipt-data");
         assertThat(response.getQueueMessage().get(0).getTimeNextVisible()).isEqualTo("time-next-visible");
         assertThat(response.getQueueMessage().get(0).getDequeueCount()).isEqualTo(0);
      } finally {
         server.shutdown();
      }
   }
}