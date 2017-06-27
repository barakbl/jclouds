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
package org.jclouds.azure.storage.domain;


import org.jclouds.azure.storage.domain.internals.EnumerationResults;

import javax.ws.rs.GET;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "QueueMessagesList")
public class GetQueueResponse {

    public static class QueueMessage {
        @XmlElement
        private String messageId;

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        @XmlElement
        private Date insertionTime;

        public Date getInsertionTime() {
            return insertionTime;
        }

        public void setInsertionTime(Date insertionTime) {
            this.insertionTime = insertionTime;
        }

        @XmlElement
        private Date expirationTime;

        public Date getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(Date expirationTime) {
            this.expirationTime = expirationTime;
        }

        @XmlElement
        private String popReceipt;

        public String getPopReceipt() {
            return popReceipt;
        }

        public void setPopReceipt(String popReceipt) {
            this.popReceipt = popReceipt;
        }

        @XmlElement
        private Date timeNextVisible;

        public Date getTimeNextVisible() {
            return timeNextVisible;
        }

        public void setTimeNextVisible(Date timeNextVisible) {
            this.timeNextVisible = timeNextVisible;
        }

        @XmlElement
        private int dequeueCount;

        public int getDequeueCount() {
            return dequeueCount;
        }

        public void setDequeueCount(int dequeueCount) {
            this.dequeueCount = dequeueCount;
        }

        @XmlElement
        private String messageText;

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }
    }

    @XmlElement
    private QueueMessage queueMessage;

    public QueueMessage getQueueMessage() {
        return queueMessage;
    }

    public void setQueueMessage(QueueMessage queueMessage) {
        this.queueMessage = queueMessage;
    }
}