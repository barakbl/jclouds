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
package org.jclouds.aws.s3.blobstore.functions;

import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.internal.TreeSetListBucketResponse;
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
public class ResourceToBucketList implements
         Function<ListContainerResponse<? extends StorageMetadata>, ListBucketResponse> {
   private final BlobToObjectMetadata blob2ObjectMd;

   @Inject
   public ResourceToBucketList(BlobToObjectMetadata blob2ObjectMd) {
      this.blob2ObjectMd = blob2ObjectMd;
   }

   public ListBucketResponse apply(ListContainerResponse<? extends StorageMetadata> list) {

      Iterable<ObjectMetadata> contents = Iterables.transform(Iterables.filter(
               list, new Predicate<StorageMetadata>() {

                  public boolean apply(StorageMetadata input) {
                     return input.getType() == StorageType.BLOB;
                  }

               }), new Function<StorageMetadata, ObjectMetadata>() {

         public MutableObjectMetadata apply(StorageMetadata from) {
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
      return new TreeSetListBucketResponse(null, contents, list.getPath(), list.getMarker(), list
               .getMaxResults(), "/", Iterables.size(contents) == list.getMaxResults(), commonPrefixes);
   }
}