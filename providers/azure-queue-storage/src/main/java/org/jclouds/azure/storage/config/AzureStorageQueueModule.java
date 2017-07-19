package org.jclouds.azure.storage.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Provides;
import org.jclouds.azure.storage.AzureStorageQueueApi;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import javax.inject.Named;
import java.util.concurrent.TimeUnit;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

@ConfiguresHttpApi
public class AzureStorageQueueModule extends HttpApiModule<AzureStorageQueueApi> {
   @Override
   protected void configure() {
      bind(GsonModule.DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
      super.configure();
   }

   @Provides
   @TimeStamp
   protected final String guiceProvideTimeStamp(@TimeStamp Supplier<String> cache) {
      return provideTimeStamp(cache);
   }

   protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
      return cache.get();
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @TimeStamp
   protected Supplier<String> provideTimeStampCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
                                                    final DateService dateService) {
      return Suppliers.memoizeWithExpiration(new Supplier<String>() {
         @Override
         public String get() {
            return dateService.rfc822DateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }

}
