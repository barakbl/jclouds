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

package org.jclouds.http.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.jclouds.io.ETagOutputStream;

// TODO: collapse into ETagOutputStream and rename to HttpRequestOutputStream
final class StreamingOutputStream extends ETagOutputStream {
   private final HttpURLConnection connection;

   StreamingOutputStream(HttpURLConnection connection, OutputStream os) {
      super(os);
      this.connection = connection;
   }

   @Override
   public void close() throws IOException {
      super.close();
      InputStream is = connection.getInputStream();
      try {
         int responseCode = connection.getResponseCode();
         setETag(connection.getHeaderField("ETag"));
         // TODO: if not 2xx, throw HttpResponseException?
      } finally {
         is.close();
         connection.disconnect();
      }
   }
}
