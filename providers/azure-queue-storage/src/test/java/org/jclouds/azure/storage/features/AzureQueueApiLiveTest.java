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


import org.jclouds.azure.storage.domain.internals.Queue;
import org.jclouds.azure.storage.domain.internals.QueueResponse.CreateQueueResponse;
import org.jclouds.azure.storage.domain.internals.QueueResponse.DeleteQueueResponse;
import org.jclouds.azure.storage.domain.internals.QueueResponse.ListQueueResponse;
import org.jclouds.azure.storage.internal.BaseAzureQueueApiLiveTest;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.io.Writer;

import static org.assertj.core.api.Assertions.assertThat;

public final class AzureQueueApiLiveTest extends BaseAzureQueueApiLiveTest{
   private static final String queueName= "myqueue";

   @Test(groups = "live")
   public void testCreate() {
      QueueApi queueApi= api.getQueueApi();
      CreateQueueResponse response = queueApi.create(queueName);
      try{
         assertThat(response.isSuccess()).isEqualTo(true);
      }
      finally {
         queueApi.delete(queueName);
      }
   }

   @Test(groups = "live")
   public void testDelete() {
      QueueApi queueApi= api.getQueueApi();
      queueApi.create(queueName);
      DeleteQueueResponse response = queueApi.delete(queueName);
      assertThat(response.isSuccess()).isEqualTo(true);
   }

   @Test(groups = "live")
   public void testList() {
      QueueApi queueApi= api.getQueueApi();
      //queueApi.create(queueName);
      //try {
         boolean found = false;
         ListQueueResponse queues = queueApi.list();
         for (Queue queue : queues.getQueues()) {
            if (queue.getName().equals(queueName)) {
               found = true;
            }
         }
         assertThat(found).isTrue();
      //} finally {
      //   queueApi.delete(queueName);
      //}
   }

//   @Test(groups = "live")
//   public void testGet() {
//      QueueApi queueApi= api.getQueueApi();
//      queueApi.create(queueName);
//      try {
//         boolean found = false;
//         GetQueueResponse queues = queueApi.get("myqueue", 1);
//         assertThat(found).isTrue();
//      } finally {
//         queueApi.delete("1");
//      }
//   }
}
