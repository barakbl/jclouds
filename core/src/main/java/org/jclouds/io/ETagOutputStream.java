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
package org.jclouds.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.FilterOutputStream;
import java.io.OutputStream;

import org.jclouds.javax.annotation.Nullable;

/**
 * ETagOutputStream wraps an OutputStream when a client writes to an HTTP
 * server.  After clients close the stream, they can get the ETag from the
 * server.
 */
public class ETagOutputStream extends FilterOutputStream {
   @Nullable
   private String eTag;

   public ETagOutputStream(OutputStream os) {
      super(os);
   }

   /** @return the HTTP response ETag or null */
   @Nullable
   public String getETag() {
      return eTag;
   }

   public void setETag(String eTag) {
      checkState(this.eTag == null);
      this.eTag = checkNotNull(eTag);
   }
}
