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
package org.jclouds.azure.storage.domain.internals;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "QueueMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueueMessage {
   @XmlElement(name = "MessageId")
   private String messageId;

   @XmlElement(name = "InsertionTime")
   private String insertionTime;

   @XmlElement(name = "ExpirationTime")
   private String expirationTime;

   @XmlElement(name = "PopReceipt")
   private String popReceipt;

   @XmlElement(name = "TimeNextVisible")
   private String timeNextVisible;

   @XmlElement(name = "DequeueCount")
   private int dequeueCount;


   @XmlElement(name = "MessageText")
   private String messageText;


   public String getMessageId() {
      return messageId;
   }

   public void setMessageId(String messageId) {
      this.messageId = messageId;
   }

   public String getInsertionTime() {
      return insertionTime;
   }

   public void setInsertionTime(String insertionTime) {
      this.insertionTime = insertionTime;
   }

   public String getExpirationTime() {
      return expirationTime;
   }

   public void setExpirationTime(String expirationTime) {
      this.expirationTime = expirationTime;
   }

   public String getPopReceipt() {
      return popReceipt;
   }

   public void setPopReceipt(String popReceipt) {
      this.popReceipt = popReceipt;
   }

   public String getTimeNextVisible() {
      return timeNextVisible;
   }

   public void setTimeNextVisible(String timeNextVisible) {
      this.timeNextVisible = timeNextVisible;
   }

   public int getDequeueCount() {
      return dequeueCount;
   }

   public void setDequeueCount(int dequeueCount) {
      this.dequeueCount = dequeueCount;
   }

   public String getMessageText() {
      return messageText;
   }

   public void setMessageText(String messageText) {
      this.messageText = messageText;
   }
}
