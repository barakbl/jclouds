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
package org.jclouds.blobstore.cli;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.inject.Module;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobBuilder.PayloadBlobBuilder;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.Location;
import org.jclouds.io.Payload;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.util.Closeables2;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

// TODO: map exceptions to errno values like jclouds-cli
// TODO: blobstore-cli uses subcommands, e.g., "container list", instead of
// jclouds-cli tokens, e.g., "container-list".

public final class BlobStoreCli {
   @Option(name = "--properties", usage = "properties")
   public File propertiesFile = null;

   @Argument(handler = SubCommandHandler.class, required = true, metaVar = "resource", usage = "resource type")
   @SubCommands({
      @SubCommand(name = "blob", impl = BlobCommand.class),
      @SubCommand(name = "container", impl = ContainerCommand.class)
   })
   BlobStoreCommand value;

   public interface BlobStoreCommand {
      void run(BlobStore blobStore) throws IOException;
   }

   public static final class BlobCommand implements BlobStoreCommand {
      @Argument(handler = SubCommandHandler.class, required = true, metaVar = "action", usage = "blob action")
      @SubCommands({
         @SubCommand(name = "get", impl = BlobGetCommand.class),
         @SubCommand(name = "list", impl = BlobListCommand.class),
         @SubCommand(name = "put", impl = BlobPutCommand.class),
         @SubCommand(name = "remove", impl = BlobRemoveCommand.class)
      })
      BlobStoreCommand value;
      @Override
      public void run(BlobStore blobStore) throws IOException {
         value.run(blobStore);
      }
   }

   public static final class ContainerCommand implements BlobStoreCommand {
      @Argument(handler = SubCommandHandler.class, required = true, metaVar = "action", usage = "container action")
      @SubCommands({
         @SubCommand(name = "clear", impl = ContainerClearCommand.class),
         @SubCommand(name = "create", impl = ContainerCreateCommand.class),
         @SubCommand(name = "delete", impl = ContainerDeleteCommand.class),
         @SubCommand(name = "list", impl = ContainerListCommand.class),
         @SubCommand(name = "location-list", impl = ContainerLocationListCommand.class)
      })
      BlobStoreCommand value;
      @Override
      public void run(BlobStore blobStore) throws IOException {
         value.run(blobStore);
      }
   }

   public BlobStoreCli(String[] args) throws CmdLineException {
      CmdLineParser parser = new CmdLineParser(this,
              ParserProperties.defaults().withUsageWidth(80));
      parser.parseArgument(args);
   }

   public static void main(String[] args) throws IOException {
      BlobStoreCli cli;
      try {
         cli = new BlobStoreCli(args);
      } catch (CmdLineException cle) {
         PrintStream out = System.err;
         out.println("Unable to parse command line arguments: " +
               cle.getMessage());
         out.println("Valid options include:");
         cle.getParser().printUsage(out);
         System.exit(1);
         return;
      }

      Properties properties = new Properties(System.getProperties());
      if (cli.propertiesFile != null) {
         InputStream is = new BufferedInputStream(new FileInputStream(cli.propertiesFile));
         try {
            properties.load(is);
         } finally {
            Closeables2.closeQuietly(is);
         }
         System.setProperties(properties);
      }

      BlobStoreContext blobStoreContext = getBlobStoreContext(properties);
      try {
         cli.value.run(blobStoreContext.getBlobStore());
      } finally {
         blobStoreContext.close();
      }
   }

   private static BlobStoreContext getBlobStoreContext(Properties properties) {
      String provider = properties.getProperty(Constants.PROPERTY_PROVIDER);
      String identity = properties.getProperty(Constants.PROPERTY_IDENTITY);
      String credential = properties.getProperty(Constants.PROPERTY_CREDENTIAL);
      String endpoint = properties.getProperty(Constants.PROPERTY_ENDPOINT);
      if (provider == null || identity == null || credential == null) {
         throw new IllegalArgumentException("must provide " +
               Constants.PROPERTY_PROVIDER + ", " +
               Constants.PROPERTY_IDENTITY + ", and " +
               Constants.PROPERTY_CREDENTIAL);
      }

      ContextBuilder contextBuilder = ContextBuilder
            .newBuilder(provider)
            .credentials(identity, credential)
            .modules(ImmutableList.<Module>of(new SLF4JLoggingModule()))
            .overrides(properties);
      if (endpoint != null) {
         contextBuilder = contextBuilder.endpoint(endpoint);
      }
      return contextBuilder.build(BlobStoreContext.class);
   }

   public static final class BlobGetCommand implements BlobStoreCommand {
      @Argument(metaVar = "container-name", usage = "container name",
              index = 0, required = true)
      public String containerName;

      @Argument(metaVar = "remote-name", usage = "remote blob name",
              index = 1, required = true)
      public String remoteName;

      @Argument(metaVar = "local-name", usage = "local file name (optional)",
              index = 2)
      public String localName = null;

      @Override
      public void run(BlobStore blobStore) throws IOException {
         if (localName == null) {
            localName = remoteName;
         }

         Blob blob = blobStore.getBlob(containerName, remoteName);
         if (blob == null) {
            throw new KeyNotFoundException(containerName, remoteName,
                  "Blob does not exist");
         }
         Payload payload = blob.getPayload();
         InputStream is = payload.openStream();
         try {
            if (localName.equals("-")) {
               ByteStreams.copy(is, System.out);
            } else {
               Files.asByteSink(new File(localName)).writeFrom(is);
            }
         } finally {
            Closeables2.closeQuietly(is);
            Closeables2.closeQuietly(payload);
         }
      }
   }

   public static final class BlobListCommand implements BlobStoreCommand {
      @Argument(metaVar = "container-name", usage = "Container name",
              index = 0, required = true)
      public String containerName;

      @Argument(metaVar = "prefix", usage = "List blobs with only this prefix",
              index = 1, required = false)
      public String prefix;

      @Option(name = "--detailed", usage = "Display details")
      public boolean details = false;

      @Option(name = "--recursive", usage = "List blobs recursively")
      public boolean recursive = false;

      @Override
      public void run(BlobStore blobStore) {
         ListContainerOptions options = new ListContainerOptions()
               .delimiter("/")
               .prefix(prefix);
         if (recursive) {
            options.recursive();
         }

         while (true) {
            PageSet<? extends StorageMetadata> blobs = blobStore.list(
                  containerName, options);
            for (StorageMetadata sm : blobs) {
               if (details) {
                  // TODO: wonky format
                  // [type=BLOB, id=null, name=foo, location={scope=PROVIDER, id=s3, description=https://storage.googleapis.com}, uri=https://storage.googleapis.com/gaulbackup2/foo, userMetadata={mode=33204, uid=1000, gid=1000, mtime=1402377504}]
                  System.out.println(blobStore.blobMetadata(
                        containerName, sm.getName()));
               } else {
                  System.out.println(sm.getName());
               }
            }

            String marker = blobs.getNextMarker();
            if (marker == null) {
               break;
            }
            options.afterMarker(marker);
         }
      }
   }

   public static final class BlobPutCommand implements BlobStoreCommand {
      @Argument(metaVar = "container-name", usage = "container name",
              index = 0, required = true)
      public String containerName;

      @Argument(metaVar = "local-name", usage = "local file name",
              index = 1, required = true)
      public String localName;

      @Argument(metaVar = "remote-name",
              usage = "remote blob name (optional)", index = 2,
              required = false)
      public String remoteName = null;

      @Option(name = "--multipart-upload", usage = "multipart upload")
      public boolean multipartUpload = false;

      @Override
      public void run(BlobStore blobStore) throws IOException {
         if (remoteName == null) {
            remoteName = localName;
         }

         BlobBuilder blobBuilder = blobStore.blobBuilder(remoteName);
         PayloadBlobBuilder payloadBuilder;
         if (localName.equals("-")) {
            payloadBuilder = blobBuilder.payload(System.in);
         } else {
            ByteSource byteSource = Files.asByteSource(new File(localName));
            payloadBuilder = blobBuilder.payload(byteSource)
               .contentLength(byteSource.size());
            if (!multipartUpload) {
               payloadBuilder = payloadBuilder.contentMD5(
                  byteSource.hash(Hashing.md5()));
            }
         }
         Blob blob = payloadBuilder.build();
         PutOptions putOptions = new PutOptions(multipartUpload);
         blobStore.putBlob(containerName, blob, putOptions);
      }
   }

   public static final class BlobRemoveCommand implements BlobStoreCommand {
      @Argument(metaVar = "container-name", usage = "Container name",
              index = 0, required = true)
      public String containerName;

      @Argument(metaVar = "remote-name", usage = "remote blob name",
              index = 1, required = true)
      public String blobName;

      @Override
      public void run(BlobStore blobStore) {
         blobStore.removeBlob(containerName, blobName);
      }
   }

   public static final class ContainerClearCommand implements BlobStoreCommand {
      @Argument(metaVar = "container-name", usage = "Container name",
              required = true)
      public String containerName;

      @Override
      public void run(BlobStore blobStore) {
         blobStore.clearContainer(containerName);
      }
   }

   public static final class ContainerCreateCommand implements BlobStoreCommand {
      @Argument(metaVar = "container-name", usage = "Container name",
              required = true)
      public String containerName;

      @Option(name = "--location", usage = "Container location")
      public String locationString;

      @Override
      public void run(BlobStore blobStore) {
         Location location = null;
         if (!Strings.isNullOrEmpty(locationString)) {
            for (Location loc : blobStore.listAssignableLocations()) {
               if (loc.getId().equalsIgnoreCase(locationString)) {
                  location = loc;
                  break;
               }
            }
            if (location == null) {
               throw new IllegalArgumentException("unknown location: " + locationString);
            }
         }

         boolean created = blobStore.createContainerInLocation(location, containerName);
         if (!created) {
            System.err.println("container not created; does it already exist?");
         }
      }
   }

   public static final class ContainerDeleteCommand implements BlobStoreCommand {
      @Argument(metaVar = "container-name", usage = "Container name",
              required = true)
      public String containerName;

      @Override
      public void run(BlobStore blobStore) {
         blobStore.deleteContainer(containerName);
      }
   }

   public static final class ContainerListCommand implements BlobStoreCommand {
      @Override
      public void run(BlobStore blobStore) {
         List<String> names = Lists.newArrayList();
         for (StorageMetadata container : blobStore.list()) {
            names.add(container.getName());
         }
         Collections.sort(names);
         for (String name : names) {
            System.out.println(name);
         }
      }
   }

   public static final class ContainerLocationListCommand implements BlobStoreCommand {
      @Override
      public void run(BlobStore blobStore) {
         for (Location location : blobStore.listAssignableLocations()) {
            System.out.println(location.getId());
         }
      }
   }
}
