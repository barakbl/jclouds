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
package org.jclouds.azure.storage.blob.blobstore.functions;

import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ListableBlobProperties;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.jclouds.azure.storage.blob.domain.internal.TreeSetListBlobsResponse;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceToListBlobsResponse implements
         Function<ListContainerResponse<? extends StorageMetadata>, ListBlobsResponse> {
   private final BlobMetadataToBlobProperties blob2ObjectMd;

   @Inject
   public ResourceToListBlobsResponse(BlobMetadataToBlobProperties blob2ObjectMd) {
      this.blob2ObjectMd = blob2ObjectMd;
   }

   public ListBlobsResponse apply(ListContainerResponse<? extends StorageMetadata> list) {

      Iterable<ListableBlobProperties> contents = Iterables.transform(Iterables.filter(list,
               new Predicate<StorageMetadata>() {

                  public boolean apply(StorageMetadata input) {
                     return input.getType() == StorageType.BLOB;
                  }

               }), new Function<StorageMetadata, ListableBlobProperties>() {

         public MutableBlobProperties apply(StorageMetadata from) {
            return blob2ObjectMd.apply((BlobMetadata) from);
         }

      });

      SortedSet<String> commonPrefixes = Sets.newTreeSet(Iterables.transform(Iterables.filter(list,
               new Predicate<StorageMetadata>() {

                  public boolean apply(StorageMetadata input) {
                     return input.getType() == StorageType.RELATIVE_PATH;
                  }

               }), new Function<StorageMetadata, String>() {

         public String apply(StorageMetadata from) {
            return from.getName();
         }

      }));
      return new TreeSetListBlobsResponse(contents, null, list.getPath(), null, list
               .getMaxResults(),list.getMarker(), "/", commonPrefixes);
   }
}