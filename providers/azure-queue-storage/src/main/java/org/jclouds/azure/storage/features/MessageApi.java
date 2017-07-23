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

import org.jclouds.azure.storage.domain.internals.MessageResponse.GetQueueResponse;
import org.jclouds.azure.storage.domain.internals.MessageResponse.PostQueueResponse;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;

import javax.inject.Named;
import javax.ws.rs.*;
import java.io.Closeable;

public interface MessageApi extends Closeable {

   @Named("azure_storage_queue_get")
   @GET
   @Path("/{queueName}/messages")
   @JAXBResponseParser
   GetQueueResponse get(@PathParam("queueName") String queueName, @QueryParam("numofmessages") int numofmessages);

   @Named("azure_storage_queue_post")
   @POST
   @Path("/{queueName}/messages")
   @Payload("<QueueMessage><MessageText>{message-content}</MessageText></QueueMessage>")
   @JAXBResponseParser
   PostQueueResponse post(@PathParam("queueName") String queueName, @PayloadParam("message-content") String content);
}
