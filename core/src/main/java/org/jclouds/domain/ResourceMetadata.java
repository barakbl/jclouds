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
package org.jclouds.domain;

import java.net.URI;
import java.util.Map;

import com.google.inject.ImplementedBy;

/**
 * Identifies containers, files, etc.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(ResourceMetadata.class)
public interface ResourceMetadata<T extends Enum<T>> extends Comparable<ResourceMetadata<T>> {

   /**
    * Whether this resource is a container, file, node, queue, etc.
    */
   T getType();

   /**
    * Unique identifier of this resource within its enclosing namespace. In some scenarios, this id
    * is not user assignable. For files, this may be an system generated key, or the full path to
    * the resource. ex. /path/to/file.txt
    * 
    */
   String getId();

   /**
    * Name of this resource. Names are dictated by the user. For files, this may be the filename,
    * ex. file.txt
    * 
    */
   String getName();

   /**
    * Physical location of the resource.
    * 
    * ex. us-west-1
    * 
    */
   String getLocation();

   /**
    * URI used to access this resource
    */
   URI getUri();

   /**
    * Any key-value pairs associated with the resource.
    */
   Map<String, String> getUserMetadata();

}