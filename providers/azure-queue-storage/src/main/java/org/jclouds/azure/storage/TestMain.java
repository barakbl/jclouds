package org.jclouds.azure.storage;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.azure.storage.config.AzureStorageQueueModule;

public class TestMain {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AzureStorageQueueModule());
        AzureStorageQueueApi api = injector.getInstance(AzureStorageQueueApi.class);
        api.getQueueApi().create("aaa");

        // 1. How can I assign the credential (account, token, etc.) to guice. like spring, we can use .properties or set in Config class
        // 2. I got a lot of error here, it seems I miss tons settings.
        /*
Exception in thread "main" com.google.inject.CreationException: Guice creation errors:

1) No implementation for com.google.common.base.Supplier<org.jclouds.domain.Credentials> annotated with @org.jclouds.location.Provider() was bound.
  while locating com.google.common.base.Supplier<org.jclouds.domain.Credentials> annotated with @org.jclouds.location.Provider()
    for parameter 1 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

2) No implementation for com.google.common.util.concurrent.TimeLimiter was bound.
  while locating com.google.common.util.concurrent.TimeLimiter
    for parameter 3 at org.jclouds.rest.internal.InvokeHttpMethod.<init>(InvokeHttpMethod.java:59)
  at org.jclouds.rest.config.HttpApiModule.configure(HttpApiModule.java:54)

3) No implementation for java.lang.Boolean annotated with @com.google.inject.name.Named(value=jclouds.connection-close-header) was bound.
  while locating java.lang.Boolean annotated with @com.google.inject.name.Named(value=jclouds.connection-close-header)
    for parameter 10 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

4) No implementation for java.lang.Boolean annotated with @com.google.inject.name.Named(value=jclouds.strip-expect-header) was bound.
  while locating java.lang.Boolean annotated with @com.google.inject.name.Named(value=jclouds.strip-expect-header)
    for parameter 9 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

5) No implementation for java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.connection-timeout) was bound.
  while locating java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.connection-timeout)
    for parameter 0 at org.jclouds.http.HttpUtils.<init>(HttpUtils.java:91)
  while locating org.jclouds.http.HttpUtils
    for parameter 4 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

6) No implementation for java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.max-connections-per-context) was bound.
  while locating java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.max-connections-per-context)
    for parameter 2 at org.jclouds.http.HttpUtils.<init>(HttpUtils.java:91)
  while locating org.jclouds.http.HttpUtils
    for parameter 4 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

7) No implementation for java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.max-connections-per-host) was bound.
  while locating java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.max-connections-per-host)
    for parameter 3 at org.jclouds.http.HttpUtils.<init>(HttpUtils.java:91)
  while locating org.jclouds.http.HttpUtils
    for parameter 4 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

8) No implementation for java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.so-timeout) was bound.
  while locating java.lang.Integer annotated with @com.google.inject.name.Named(value=jclouds.so-timeout)
    for parameter 1 at org.jclouds.http.HttpUtils.<init>(HttpUtils.java:91)
  while locating org.jclouds.http.HttpUtils
    for parameter 4 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

9) No implementation for java.lang.String annotated with @org.jclouds.location.Provider() was bound.
  while locating java.lang.String annotated with @org.jclouds.location.Provider()
    for parameter 0 at org.jclouds.location.suppliers.all.JustProvider.<init>(JustProvider.java:43)
  while locating org.jclouds.location.suppliers.all.JustProvider
    for parameter 0 at org.jclouds.location.suppliers.all.RegionToProviderOrJustProvider.<init>(RegionToProviderOrJustProvider.java:46)
  while locating org.jclouds.location.suppliers.all.RegionToProviderOrJustProvider
    for parameter 0 at org.jclouds.location.suppliers.all.ZoneToRegionToProviderOrJustProvider.<init>(ZoneToRegionToProviderOrJustProvider.java:61)
  at org.jclouds.location.config.LocationModule.memoizedLocationsSupplier(LocationModule.java:109)

10) No implementation for java.lang.String annotated with @org.jclouds.location.Provider() was bound.
  while locating java.lang.String annotated with @org.jclouds.location.Provider()
    for parameter 1 at org.jclouds.location.suppliers.fromconfig.RegionIdToZoneIdsFromConfiguration.<init>(RegionIdToZoneIdsFromConfiguration.java:59)
  at org.jclouds.location.config.LocationModule.regionIdToZoneIdsSupplier(LocationModule.java:178)

11) No implementation for java.lang.String annotated with @org.jclouds.location.Provider() was bound.
  while locating java.lang.String annotated with @org.jclouds.location.Provider()
    for parameter 1 at org.jclouds.location.suppliers.fromconfig.RegionIdsFromConfiguration.<init>(RegionIdsFromConfiguration.java:38)
  while locating org.jclouds.location.suppliers.fromconfig.RegionIdsFromConfiguration
    for parameter 0 at org.jclouds.location.predicates.fromconfig.AnyOrConfiguredRegionId.<init>(AnyOrConfiguredRegionId.java:40)
  at org.jclouds.location.config.LocationModule.regionIdsSupplier(LocationModule.java:118)

12) No implementation for java.lang.String annotated with @org.jclouds.location.Provider() was bound.
  while locating java.lang.String annotated with @org.jclouds.location.Provider()
    for parameter 1 at org.jclouds.location.suppliers.fromconfig.RegionIdsFromConfiguration.<init>(RegionIdsFromConfiguration.java:38)
  at org.jclouds.location.config.LocationModule.regionIdsSupplier(LocationModule.java:118)

13) No implementation for java.lang.String annotated with @org.jclouds.location.Provider() was bound.
  while locating java.lang.String annotated with @org.jclouds.location.Provider()
    for parameter 1 at org.jclouds.location.suppliers.fromconfig.ZoneIdsFromConfiguration.<init>(ZoneIdsFromConfiguration.java:38)
  while locating org.jclouds.location.suppliers.fromconfig.ZoneIdsFromConfiguration
    for parameter 0 at org.jclouds.location.predicates.fromconfig.AnyOrConfiguredZoneId.<init>(AnyOrConfiguredZoneId.java:40)
  at org.jclouds.location.config.LocationModule.zoneIdsSupplier(LocationModule.java:128)

14) No implementation for java.lang.String annotated with @org.jclouds.location.Provider() was bound.
  while locating java.lang.String annotated with @org.jclouds.location.Provider()
    for parameter 1 at org.jclouds.location.suppliers.fromconfig.ZoneIdsFromConfiguration.<init>(ZoneIdsFromConfiguration.java:38)
  at org.jclouds.location.config.LocationModule.zoneIdsSupplier(LocationModule.java:128)

15) No implementation for java.lang.String annotated with @org.jclouds.rest.annotations.ApiVersion() was bound.
  while locating java.lang.String annotated with @org.jclouds.rest.annotations.ApiVersion()
    for parameter 0 at org.jclouds.rest.functions.PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion.<init>(PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion.java:68)
  at org.jclouds.location.config.LocationModule.configure(LocationModule.java:72)

16) No implementation for java.lang.String annotated with @org.jclouds.rest.annotations.ApiVersion() was bound.
  while locating java.lang.String annotated with @org.jclouds.rest.annotations.ApiVersion()
    for parameter 2 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

17) No implementation for java.lang.String annotated with @org.jclouds.rest.annotations.BuildVersion() was bound.
  while locating java.lang.String annotated with @org.jclouds.rest.annotations.BuildVersion()
    for parameter 3 at org.jclouds.rest.internal.RestAnnotationProcessor.<init>(RestAnnotationProcessor.java:137)
  at org.jclouds.rest.config.RestModule.configure(RestModule.java:64)

18) No implementation for java.util.Set<java.lang.String> annotated with @org.jclouds.location.Iso3166() was bound.
  while locating java.util.Set<java.lang.String> annotated with @org.jclouds.location.Iso3166()
    for parameter 2 at org.jclouds.location.suppliers.all.JustProvider.<init>(JustProvider.java:43)
  while locating org.jclouds.location.suppliers.all.JustProvider
    for parameter 0 at org.jclouds.location.suppliers.all.RegionToProviderOrJustProvider.<init>(RegionToProviderOrJustProvider.java:46)
  while locating org.jclouds.location.suppliers.all.RegionToProviderOrJustProvider
    for parameter 0 at org.jclouds.location.suppliers.all.ZoneToRegionToProviderOrJustProvider.<init>(ZoneToRegionToProviderOrJustProvider.java:61)
  at org.jclouds.location.config.LocationModule.memoizedLocationsSupplier(LocationModule.java:109)

19) No implementation for org.jclouds.http.HttpCommandExecutorService was bound.
  while locating org.jclouds.http.HttpCommandExecutorService
    for parameter 1 at org.jclouds.rest.internal.InvokeHttpMethod.<init>(InvokeHttpMethod.java:59)
  at org.jclouds.rest.config.HttpApiModule.configure(HttpApiModule.java:54)

20) No implementation for org.jclouds.providers.ProviderMetadata was bound.
  while locating org.jclouds.providers.ProviderMetadata
    for parameter 0 at org.jclouds.location.suppliers.fromconfig.ProviderURIFromProviderMetadata.<init>(ProviderURIFromProviderMetadata.java:31)
  at org.jclouds.location.config.LocationModule.provideProvider(LocationModule.java:90)

21) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.implicitLocationSupplier(LocationModule.java:98)

22) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.implicitRegionIdSupplier(LocationModule.java:167)

23) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.isoCodesSupplier(LocationModule.java:81)

24) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.memoizedLocationsSupplier(LocationModule.java:109)

25) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.provideProvider(LocationModule.java:90)

26) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.regionIdToURISupplier(LocationModule.java:158)

27) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.regionIdToZoneIdsSupplier(LocationModule.java:178)

28) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.regionIdsSupplier(LocationModule.java:118)

29) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.zoneIdToURISupplier(LocationModule.java:188)

30) No implementation for java.lang.Long annotated with @com.google.inject.name.Named(value=jclouds.session-interval) was bound.
  at org.jclouds.location.config.LocationModule.zoneIdsSupplier(LocationModule.java:128)

30 errors
	at com.google.inject.internal.Errors.throwCreationExceptionIfErrorsExist(Errors.java:435)
	at com.google.inject.internal.InternalInjectorCreator.initializeStatically(InternalInjectorCreator.java:154)
	at com.google.inject.internal.InternalInjectorCreator.build(InternalInjectorCreator.java:106)
	at com.google.inject.Guice.createInjector(Guice.java:95)
	at com.google.inject.Guice.createInjector(Guice.java:72)
	at com.google.inject.Guice.createInjector(Guice.java:62)
	at org.jclouds.azure.storage.TestMain.main(TestMain.java:10)
        */
    }

}
