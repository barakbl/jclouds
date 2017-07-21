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

/*package org.jclouds.azure.storage.domain;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "StorageServiceProperties")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetQueueServicesResponse {

   @XmlRootElement(name = "Logging")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Logging {
      @XmlElement(name="Version")
      private float version;

      public float getVersion() {
         return version;
      }

      public void setVersion(float version) {
         this.version = version;
      }

      @XmlElement(name="Delete")
      private boolean delete;

      public boolean isDelete() {
         return delete;
      }

      public void setDelete(boolean delete) {
         this.delete = delete;
      }

      @XmlElement(name="Read")
      private boolean read;

      public boolean isRead() {
         return read;
      }

      public void setRead(boolean read) {
         this.read = read;
      }

      @XmlElement(name="Write")
      private boolean write;

      public boolean isWrite() {
         return write;
      }

      public void setWrite(boolean write) {
         this.write = write;
      }

      @XmlRootElement(name = "RetentionPolicy")
      @XmlAccessorType(XmlAccessType.FIELD)
      public static class RetentionPolicy {
         @XmlElement(name="Enable")
         private boolean enable;

         public boolean isEnable() {
            return enable;
         }

         public void setEnable(boolean enable) {
            this.enable = enable;
         }

         @XmlElement(name="Days")
         private int days;

         public int getDays() {
            return days;
         }

         public void setDays(int days) {
            this.days = days;
         }
      }

      @XmlElement(name="RetentionPolicy")
      private RetentionPolicy retentionPolicy;

      public RetentionPolicy getRetentionPolicy() {
         return retentionPolicy;
      }

      public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
         this.retentionPolicy = retentionPolicy;
      }
   }

   @XmlElement (name = "Logging")
   private Logging logging;

   public Logging getLogging() {
      return logging;
   }

   public void setLogging(Logging logging) {
      this.logging = logging;
   }

   @XmlRootElement (name="Metrics")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Metrics {
      @XmlElement (name = "Version")
      private float version;

      public float getVersion() {
         return version;
      }

      public void setVersion(float version) {
         this.version = version;
      }

      @XmlElement (name = "Enable")
      private boolean enable;

      public boolean isEnable() {
         return enable;
      }

      public void setEnable(boolean enable) {
         this.enable = enable;
      }

      @XmlElement (name = "IncludeAPIs")
      private boolean includeAPIs;

      public boolean isIncludeAPIs() {
         return includeAPIs;
      }

      public void setIncludeAPIs(boolean includeAPIs) {
         this.includeAPIs = includeAPIs;
      }

      @XmlRootElement(name="RetentionPolicy")
      @XmlAccessorType(XmlAccessType.FIELD)
      public static class RetentionPolicy {
         @XmlElement (name = "Enable")
         private boolean enable;

         public boolean isEnable() {
            return enable;
         }

         public void setEnable(boolean enable) {
            this.enable = enable;
         }

         @XmlElement (name = "Days")
         private int days;

         public int getDays() {
            return days;
         }

         public void setDays(int days) {
            this.days = days;
         }
      }

      @XmlElement(name="RetentionPolicy")
      private RetentionPolicy retentionPolicy;

      public RetentionPolicy getRetentionPolicy() {
         return retentionPolicy;
      }

      public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
         this.retentionPolicy = retentionPolicy;
      }
   }

   @XmlElement(name="Metrics")
   private Metrics metrics;

   public Metrics getMetrics() {
      return metrics;
   }

   public void setMetrics(Metrics metrics) {
      this.metrics = metrics;
   }
} */