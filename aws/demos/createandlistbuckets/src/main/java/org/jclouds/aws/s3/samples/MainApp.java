/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3.samples;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.s3.blobstore.S3BlobStoreContextFactory;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;

/**
 * This the Main class of an Application that demonstrates the use of the blobstore.
 * 
 * Usage is: java MainApp \"accesskeyid\" \"secretkey\" \"bucketName\"
 * 
 * @author Carlos Fernandes
 * @author Adrian Cole
 */
public class MainApp {

   public static int PARAMETERS = 3;
   public static String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: \"accesskeyid\" \"secretkey\" \"bucketName\" ";

   @SuppressWarnings("unchecked")
   public static void main(String[] args) {

      if (args.length < PARAMETERS)
         throw new IllegalArgumentException(INVALID_SYNTAX);

      // Args
      String accesskeyid = args[0];
      String secretkey = args[1];
      String containerName = args[2];

      // Init
      BlobStoreContext context = S3BlobStoreContextFactory.createContext(accesskeyid, secretkey);

      try {

         // Create Container
         BlobStore blobStore = context.getBlobStore();
         blobStore.createContainerInLocation(Region.DEFAULT.toString(), containerName);

         Blob blob = blobStore.newBlob("test");
         blob.setPayload("testdata");
         blobStore.putBlob(containerName, blob);

         // List Container
         for (StorageMetadata resourceMd : blobStore.list()) {
            if (resourceMd.getType() == StorageType.CONTAINER
                     || resourceMd.getType() == StorageType.FOLDER) {
               System.out.printf("  %s: %s entries%n", resourceMd.getName(), context
                        .createInputStreamMap(resourceMd.getName()).size());
            }
         }

      } finally {
         // Close connecton
         context.close();
         System.exit(0);
      }

   }
}
