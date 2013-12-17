/**
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
package org.jclouds.blobstore.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.http.annotation.Immutable;
import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.rest.HttpClient;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Simple BlobStore benchmark.
 *
 * @author Andrew Gaul
 * @author Rajiv Desai
 */
public final class BlobStoreBench {
   /** BlobStore backend. */
   private final BlobStoreContext blobStoreContext;

   /** Enum representing the test type. */
   private static enum TestType {
      /** Test reads. */
      READ,
      /** Test signed reads. */
      SIGNED_READ,
      /** Test writes. */
      WRITE,
      /** Test signed writes. */
      SIGNED_WRITE,
      /** Test delete. */
      DELETE,
      /** Test signed delete. */
      SIGNED_DELETE
   }

   /**
    * Sharding strategy defines whether to write to a single container or to
    * shard across multiple contianers.
    */
   private static enum ShardingStrategy {
      /** Write to a single container. */
      FLAT,
      /**
       * Shard writes across multiple-containers in a round-robin scheme. If the
       * MULTI-CONTAINER sharding strategy is being used, we need to create
       * multiple containers. How many containers to create is decided by the
       * "numShards" config option. If the container name defined by --container
       * was "foo", we'll create contianers "foo-0", "foo-1", "foo-2" and so on.
       * Note that the container "foo" is already present.
       */
      MULTI_CONTAINER
   }

   @Argument(required = true, index = 0, usage = "Test type")
   private TestType testType;

   @Option(name = "--sharding-strategy", usage = "Sharding strategy")
   private ShardingStrategy shardingStrategy = ShardingStrategy.FLAT;

   @Option(name = "--num-shards", usage = "Number of shards")
   private long numShards = 0;

   /** Show progress. */
   @Option(name = "--show-progress",
           usage = "Show progress every N operations")
   private int showProgress = -1;

   /** Batched transfers. */
   @Option(name = "--batched-transfer", usage = "Issue transfers in batches")
   private boolean batchedTransfer;

   /** Number of operations to issue. */
   @Option(name = "--max-operations", usage = "Number of operations")
   private long maxOperations = Long.MAX_VALUE;

   /** Maximum runtime. */
   @Option(name = "--max-runtime", usage = "Maximum runtime (in seconds)")
   private long maxRuntime = Long.MAX_VALUE;

   /** Size of blobs. */
   @Option(name = "--blob-size", usage = "Size of blobs")
   private int blobSize = 64 * 1024;

   /** Number of parallel requests. */
   @Option(name = "--num-parallel-requests",
           usage = "Number of parallel requests")
   private int numThreads = 10;

   /**
    * Number of retries. Setting this to non-zero obscures failure count and
    * latency.
    */
   @Option(name = "--num-retries",
           usage = "Number of times to retry a request")
   private int numRetries = 0;

   /**
    * Container name to write/read blobs from.
    */
   @Option(name = "--container", usage = "Name of the container in which to "
         + "read/write blobs")
   private String containerName;

   /**
    * For multi-container sharding config, the region where the containers get
    * created.
    */
   @Option(name = "--region", usage = "Name of the region where containers " +
   		"are created. Only used during multi-container sharding.")
   private String region = "";

   private Location location = null;

   /** ExecutorService to issue asynchronous requests on. */
   private final ListeningExecutorService executorService;

   /** Blobstore. */
   private final BlobStore blobStore;

   /** Signer. */
   private final BlobRequestSigner signer;

   /** HTTP client. */
   private final HttpClient httpClient;

   /** Data to be transferred. */
   private byte[] input;

   private static final PrintStream out = System.out;

   private BlobStoreBench(final String[] args) throws CmdLineException {
      CmdLineParser parser = new CmdLineParser(this);
      parser.setUsageWidth(80);
      parser.parseArgument(args);

      if (maxOperations == Long.MAX_VALUE && maxRuntime == Long.MAX_VALUE) {
         throw new CmdLineException(parser,
               "Must specify --max-operations or --max-runtime");
      }

      // jclouds retries obscure the failure count and increase latency.
      System.setProperty(Constants.PROPERTY_MAX_RETRIES,
            String.valueOf(numRetries));

      blobStoreContext = getBlobStoreContext();
      blobStore = blobStoreContext.getBlobStore();
      signer = blobStoreContext.getSigner();
      httpClient = blobStoreContext.utils().http();
      if (containerName == null) {
         throw new CmdLineException(parser,
               "Must specify the container to use using --container");
      }

      final String provider = System.getProperty(Constants.PROPERTY_PROVIDER);
      // Create the container for transient and filesystem blobstores.
      if (provider.equals("transient") ||
          provider.equals("filesystem")) {
         blobStore.createContainerInLocation(null, containerName);
         out.printf("Created container %s%n", containerName);
      }
      executorService = MoreExecutors.listeningDecorator(Executors
            .newFixedThreadPool(numThreads));
      if (!region.isEmpty()) {
         location = Iterables.tryFind(blobStore.listAssignableLocations(),
               LocationPredicates.idEquals(region)).orNull();
         if (location == null) {
            out.printf("Cannot find region %s. Using default.%n", region);
            region = null;
         }
      }

      input = new byte[blobSize];
      new Random().nextBytes(input);
    }

   private Properties getProperties() {
      Properties properties = new Properties();

      // Number of IO and user threads
      String jcloudsIOThreads = System
            .getProperty(Constants.PROPERTY_IO_WORKER_THREADS);
      String jcloudsUserThreads = System
            .getProperty(Constants.PROPERTY_USER_THREADS);

      properties.setProperty(Constants.PROPERTY_IO_WORKER_THREADS,
            jcloudsIOThreads);
      properties.setProperty(Constants.PROPERTY_USER_THREADS,
            jcloudsUserThreads);

      // Set retry values.
      properties.setProperty(Constants.PROPERTY_MAX_RETRIES,
            String.valueOf(numRetries));
      properties.setProperty(Constants.PROPERTY_RETRY_DELAY_START, "50");

      return properties;
   }

   /**
    * Create a BlobStoreContext object using the config options provided.
    */
   private BlobStoreContext getBlobStoreContext() {
      final String provider = System.getProperty(Constants.PROPERTY_PROVIDER);
      ContextBuilder builder = ContextBuilder
            .newBuilder(provider)
            .credentials(System.getProperty(Constants.PROPERTY_IDENTITY),
                  System.getenv(Constants.PROPERTY_CREDENTIAL))
            .overrides(getProperties());
      final String endPoint = System.getProperty(Constants.PROPERTY_ENDPOINT);
      if (endPoint != null) {
         builder.endpoint(endPoint);
      }

      return builder.build(BlobStoreContext.class);
   }

   /**
    * During MULTI-CONTAINER sharding, create the containers to put blobs into.
    */
   private void createContainers() throws Exception {
      List<ListenableFuture<Void>> futures = Lists.newArrayList();

      for (int i = 0; i < numShards; i++) {
         final String name = containerName + "-" + i;
         ListenableFuture<Void> future = executorService
               .submit(new Callable<Void>() {
                  @Override
                  public Void call() {
                     blobStore.createContainerInLocation(location, name);
                     return null;
                  }
               });
         futures.add(future);
      }

      try {
         Futures.allAsList(futures).get();
      } finally {
         for (ListenableFuture<Void> future : futures) {
            future.cancel(/* mayInterruptIfRunning= */true);
         }
      }
   }

   /**
    * During MULTI-CONTAINER sharding, get the container for a given blob id.
    *
    * @param blodId
    *           The id of the blob for which to find the container name.
    * @return Container name for the given blob.
    */
   private String getContainerForVersion(final long blobId) {
      if (shardingStrategy != ShardingStrategy.MULTI_CONTAINER) {
         return containerName;
      }

      long containerNum = (blobId - 1) % numShards;
      return containerName + "-" + containerNum;
   }

   private Callable<Blob> createReadOperation(final String container,
         final String blobName) {
      return new Callable<Blob>() {
         @Override
         public Blob call() throws IOException {
            Blob blob = blobStore.getBlob(container, blobName);
            try {
               Payload payload = blob.getPayload();
               ByteStreams.copy(payload, ByteStreams.nullOutputStream());
            } finally {
            }
            return blob;
         }
      };
   }

   private Callable<HttpResponse> createSignedReadOperation(
         final String container, final String blobName) {
      final HttpRequest request = signer.signGetBlob(container, blobName);
      return new Callable<HttpResponse>() {
         @Override
         public HttpResponse call() throws IOException {
            HttpResponse httpResponse = httpClient.invoke(request);
            Payload payload = httpResponse.getPayload();
            ByteStreams.copy(payload, ByteStreams.nullOutputStream());
            return httpResponse;
         }
      };
    }

   private Callable<String> createWriteOperation(final String blobName,
         final long blobId) {
      final Blob blob = blobStore
                .blobBuilder(blobName)
                .payload(input)
                .build();
      return new Callable<String>() {
         @Override
         public String call() {
            return blobStore.putBlob(getContainerForVersion(blobId), blob);
         }
      };
   }

   private Callable<HttpResponse> createSignedWriteOperation(
         final String blobName, final long blobId) throws IOException {
      Blob blob = blobStore.blobBuilder(blobName).forSigning().payload(input)
            .calculateMD5().contentType(MediaType.OCTET_STREAM.toString())
            .build();
      final HttpRequest request = signer.signPutBlob(
            getContainerForVersion(blobId), blob);
      return new Callable<HttpResponse>() {
         @Override
         public HttpResponse call() {
            return httpClient.invoke(request);
         }
      };
   }

   private Callable<Void> createDeleteOperation(final String container,
         final String blobName) {
      return new Callable<Void>() {
         @Override
         public Void call() {
            blobStore.removeBlob(container, blobName);
            return null;
         }
      };
   }

   private Callable<HttpResponse> createSignedDeleteOperation(
         final String container, final String blobName) {
      final HttpRequest request = signer.signRemoveBlob(container, blobName);
      return new Callable<HttpResponse>() {
         @Override
         public HttpResponse call() {
            return httpClient.invoke(request);
         }
      };
   }

   private void listBlobsFromContainer(final String container,
         final List<ContainerBlobTuple> blobsToContainers) {
      ListContainerOptions options = new ListContainerOptions();
      outer: while (true) {
         PageSet<? extends StorageMetadata> pageSet = blobStore.list(container,
               options);
         for (StorageMetadata sm : pageSet) {
            blobsToContainers.add(new ContainerBlobTuple(container, sm
                  .getName()));
            if (blobsToContainers.size() == maxOperations) {
               break outer;
            }
         }
         String marker = pageSet.getNextMarker();
         if (marker == null) {
            break;
         }
         options = options.afterMarker(marker);
      }
   }

   private List<ContainerBlobTuple> listBlobsFromAllContainers() {
      List<ContainerBlobTuple> blobsToContainers = Lists.newArrayList();
        // Get blobs from the "parent" container.
      listBlobsFromContainer(containerName, blobsToContainers);
        // Get blobs from the sharded containers.
      for (int i = 0; i < numShards && blobsToContainers.size() < maxOperations;
            i++) {
         String container = containerName + "-" + i;
         listBlobsFromContainer(container, blobsToContainers);
      }
        return blobsToContainers;
   }

   private void run() throws Exception {
      final Semaphore semaphore = new Semaphore(numThreads);
      final SynchronizedDescriptiveStatistics successStats =
            new SynchronizedDescriptiveStatistics();
      final SynchronizedDescriptiveStatistics failedStats =
            new SynchronizedDescriptiveStatistics();

      // Create containers when sharding strategy is MULTI-CONTAINER.
      // Otherwise verify that the container exists.
      if (shardingStrategy == ShardingStrategy.MULTI_CONTAINER) {
         createContainers();
      } else if (!blobStore.containerExists(containerName)) {
         throw new Exception("Container does not exist.");
      }

      // Get blob names list for read/delete.
      List<ContainerBlobTuple> blobsToContainers = Lists.newArrayList();
      switch (testType) {
      case READ:
      case SIGNED_READ:
      case DELETE:
      case SIGNED_DELETE:
         blobsToContainers = listBlobsFromAllContainers();
         if (blobsToContainers.isEmpty()) {
            throw new Exception("Container does not contain any blobs");
         }
         break;
      default:
         // do nothing;
      }

      // Check enough blobs to read/delete. Otherwise issue a warning message.
      if ((testType == TestType.READ || testType == TestType.SIGNED_READ
            || testType == TestType.DELETE || testType == TestType.SIGNED_DELETE)
            && maxOperations > blobsToContainers.size()) {
         out.printf("Warning: Container has fewer blobs [%d] than "
               + "maxOperations [%d], resetting maxOperations%n",
               blobsToContainers.size(), maxOperations);
         maxOperations = blobsToContainers.size();
      }

      Stopwatch overallStopwatch = Stopwatch.createStarted();
      for (long numOperations = 1; numOperations <= maxOperations;
            ++numOperations) {
         Callable<?> operation;

         ContainerBlobTuple tuple = null;
         if (!blobsToContainers.isEmpty()) {
            tuple = blobsToContainers.get((int) numOperations - 1);
         }

         switch (testType) {
         case READ:
            operation = createReadOperation(tuple.containerName,
                  tuple.blobName);
            break;
         case SIGNED_READ:
            operation = createSignedReadOperation(tuple.containerName,
                  tuple.blobName);
            break;
         case WRITE:
            operation = createWriteOperation(newBlobName(numOperations),
                  numOperations);
            break;
         case SIGNED_WRITE:
            operation = createSignedWriteOperation(newBlobName(numOperations),
                  numOperations);
            break;
         case DELETE:
            operation = createDeleteOperation(tuple.containerName,
                  tuple.blobName);
            break;
         case SIGNED_DELETE:
            operation = createSignedDeleteOperation(tuple.containerName,
                  tuple.blobName);
            break;
         default:
            throw new IllegalArgumentException("Unhandled enum: " + testType);
         }

         semaphore.acquireUninterruptibly();
         final Stopwatch operationStopwatch = Stopwatch.createStarted();
         ListenableFuture<?> future = executorService.submit(operation);
         Futures.addCallback(future, new FutureCallback<Object>() {
            @Override
            public void onSuccess(final Object result) {
               successStats.addValue(operationStopwatch
                     .elapsed(TimeUnit.MILLISECONDS));
               semaphore.release();
            }

            @Override
            public void onFailure(final Throwable t) {
               failedStats.addValue(operationStopwatch
                     .elapsed(TimeUnit.MILLISECONDS));
               semaphore.release();
            }
         });

         if (batchedTransfer && (numOperations % numThreads) == 0) {
            // wait for all workers to complete
            semaphore.acquireUninterruptibly(numThreads);
            semaphore.release(numThreads);
         }

         if (showProgress != -1 && numOperations % showProgress == 0) {
            out.printf("Operation %d in progress%n", numOperations);
         }

         // checking here ensures we issue at least one operation
         if (overallStopwatch.elapsed(TimeUnit.SECONDS) >= maxRuntime) {
            break;
         }
      }

      // wait for all workers to complete
      semaphore.acquireUninterruptibly(numThreads);

      if (showProgress != -1) {
         out.printf("All operations completed%n%n");
      }

      long elapsedSeconds = overallStopwatch.elapsed(TimeUnit.SECONDS);

      out.printf("Number of operations: %d%n",
            successStats.getN() + failedStats.getN())
            .printf("Number of failures: %d%n", failedStats.getN())
            .printf("Blob size: %d%n", blobSize)
            .printf("Number of parallel requests: %d%n", numThreads)
            .printf("Sharding strategy: %s%n", shardingStrategy)
            .printf("Region: %s%n", region)
            .printf("Number of shards: %d%n", numShards)
            .printf("Elapsed time (s): %d%n", elapsedSeconds)
            .printf("Bandwidth (KB/s): %.1f%n",
                  successStats.getN() * blobSize / 1024.0 / elapsedSeconds)
            .printf("Throughput (Obj/s): %.1f%n",
                  (double) successStats.getN() / elapsedSeconds);

      emitLatencyStatistics("Successful transfer latency:", successStats);
      emitLatencyStatistics("Failed transfer latency:", failedStats);
   }

   private static void emitLatencyStatistics(final String header,
         final SynchronizedDescriptiveStatistics stats) {
      out.println();
      out.println(header);
      out.printf("Minimum (ms): %.1f%n", stats.getMin())
            .printf("50%% (ms): %.1f%n", stats.getPercentile(50.0))
            .printf("95%% (ms): %.1f%n", stats.getPercentile(95.0))
            .printf("99%% (ms): %.1f%n", stats.getPercentile(99.0))
            .printf("99.9%% (ms): %.1f%n", stats.getPercentile(99.9))
            .printf("Maximum (ms): %.1f%n", stats.getMax())
            .printf("Mean (ms): %.1f%n", stats.getMean())
            .printf("Standard deviation: %.1f%n", stats.getStandardDeviation());
   }

   /** Close BlobStoreBench. */
   private void close() {
      blobStoreContext.close();
      executorService.shutdownNow();
   }

   private String newBlobName(final long blobId) {
      StringBuilder blobName = new StringBuilder();
      blobName.append(UUID.randomUUID()).append(blobId);
      return blobName.toString();
   }


   /**
    * Entry point for BlobStoreBench.
    *
    * @param args
    *           command line args
    * @throws Exception
    *            on error
    */
   public static void main(final String[] args) throws Exception {
      BlobStoreBench blobStoreBench;
      try {
         blobStoreBench = new BlobStoreBench(args);
      } catch (CmdLineException cle) {
         CmdLineParser parser = cle.getParser();
         out.println(cle.getMessage());
         out.println("Usage ::");
         parser.printUsage(out);
         return;
      }

      try {
         blobStoreBench.run();
      } finally {
         blobStoreBench.close();
      }
   }

   /** Stores the mapping of container name to a blob name. */
   @Immutable
   static final class ContainerBlobTuple {
      private final String containerName;

      private final String blobName;

      private ContainerBlobTuple(final String containerName,
            final String blobName) {
         Preconditions.checkNotNull(containerName);
         Preconditions.checkNotNull(blobName);
         this.containerName = containerName;
         this.blobName = blobName;
      }
   }
}
