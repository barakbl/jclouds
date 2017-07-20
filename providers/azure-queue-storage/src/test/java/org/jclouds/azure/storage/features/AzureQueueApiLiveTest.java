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


import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import org.jclouds.azure.storage.AzureStorageQueueApi;
import org.jclouds.azure.storage.domain.CreateQueueResponse;
import org.jclouds.azure.storage.domain.DeleteQueueResponse;
import org.jclouds.azure.storage.domain.ListQueueResponse;
import org.jclouds.azure.storage.internal.BaseAzureQueueApiLiveTest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public final class AzureQueueApiLiveTest extends BaseAzureQueueApiLiveTest{
   private static final String queueName= "myqueue";

   @Test(groups = "live")
   public void testCreate() {
      QueueApi queueApi= api.getQueueApi();
      CreateQueueResponse response = queueApi.create(queueName);
      try{
         assertThat(response).isEqualTo(201);
      }finally {
         queueApi.delete();
      }
   }

   @Test(groups = "live")
   public void testDelete() {
      QueueApi queueApi= api.getQueueApi();
      queueApi.create(queueName);
      DeleteQueueResponse response = queueApi.delete();
      assertThat(response).isEqualTo(204);
   }

   @Test(groups = "live")
   public void testList() {
      QueueApi queueApi= api.getQueueApi();
      queueApi.create(queueName);
      try {
         boolean found = false;
         ListQueueResponse queues = queueApi.list();
//         for (ListQueueResponse.Queue queue : queues..getQueues()){
//            if (queue.getName().equals(queueName)) {
//               found = true;
//            }
//         }
         assertThat(found).isTrue();
      } finally {
         queueApi.delete();
      }
   }
}
