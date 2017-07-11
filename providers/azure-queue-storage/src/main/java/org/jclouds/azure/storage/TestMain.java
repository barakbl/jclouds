package org.jclouds.azure.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.azure.storage.features.QueueApi;
import org.jclouds.concurrent.config.ExecutorServiceModule;

import java.util.Set;

public class TestMain {

   public static void main(String[] args) {
      Set<Module> modules = ImmutableSet.<Module>of(
              new ExecutorServiceModule(MoreExecutors.sameThreadExecutor()));

      AzureStorageQueueApi api = ContextBuilder.newBuilder("azure-queue-storage")
              .credentials(args[0], args[1])
              .endpoint("https://jcloudsazure.queue.core.windows.net/")
              .modules(modules)
              .buildApi(AzureStorageQueueApi.class);

        /*
        Exception in thread "main" java.util.ServiceConfigurationError: org.jclouds.providers.ProviderMetadata: Provider org.jclouds.azure.storage.AzureStorageQueueProviderMetadata could not be instantiated
	at java.util.ServiceLoader.fail(ServiceLoader.java:232)
	at java.util.ServiceLoader.access$100(ServiceLoader.java:185)
	at java.util.ServiceLoader$LazyIterator.nextService(ServiceLoader.java:384)
	at java.util.ServiceLoader$LazyIterator.next(ServiceLoader.java:404)
	at java.util.ServiceLoader$1.next(ServiceLoader.java:480)
	at com.google.common.collect.ImmutableCollection$Builder.addAll(ImmutableCollection.java:281)
	at com.google.common.collect.ImmutableCollection$ArrayBasedBuilder.addAll(ImmutableCollection.java:360)
	at com.google.common.collect.ImmutableSet$Builder.addAll(ImmutableSet.java:508)
	at org.jclouds.providers.Providers.all(Providers.java:83)
	at org.jclouds.providers.Providers.withId(Providers.java:99)
	at org.jclouds.ContextBuilder.newBuilder(ContextBuilder.java:167)
	at org.jclouds.azure.storage.TestMain.main(TestMain.java:21)
Caused by: java.lang.ExceptionInInitializerError
	at org.jclouds.apis.internal.BaseApiMetadata.defaultProperties(BaseApiMetadata.java:82)
	at org.jclouds.apis.internal.BaseApiMetadata$Builder.<init>(BaseApiMetadata.java:108)
	at org.jclouds.rest.internal.BaseHttpApiMetadata$Builder.<init>(BaseHttpApiMetadata.java:77)
	at org.jclouds.azure.storage.AzureStorageQueueApiMetadata$Builder.<init>(AzureStorageQueueApiMetadata.java:53)
	at org.jclouds.azure.storage.AzureStorageQueueApiMetadata.<init>(AzureStorageQueueApiMetadata.java:37)
	at org.jclouds.azure.storage.AzureStorageQueueProviderMetadata$Builder.<init>(AzureStorageQueueProviderMetadata.java:49)
	at org.jclouds.azure.storage.AzureStorageQueueProviderMetadata.<init>(AzureStorageQueueProviderMetadata.java:33)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
	at java.lang.Class.newInstance(Class.java:442)
	at java.util.ServiceLoader$LazyIterator.nextService(ServiceLoader.java:380)
	... 9 more
Caused by: java.lang.NullPointerException: META-INF/maven/org.apache.jclouds/jclouds-core/pom.properties
	at com.google.common.base.Preconditions.checkNotNull(Preconditions.java:229)
	at org.jclouds.JcloudsVersion.readVersionPropertyFromClasspath(JcloudsVersion.java:87)
	at org.jclouds.JcloudsVersion.<init>(JcloudsVersion.java:82)
	at org.jclouds.JcloudsVersion.<init>(JcloudsVersion.java:77)
	at org.jclouds.JcloudsVersion.<clinit>(JcloudsVersion.java:48)
	... 22 more
         */

      QueueApi queue = api.getQueueApi();

      queue.create("123");
   }

}
