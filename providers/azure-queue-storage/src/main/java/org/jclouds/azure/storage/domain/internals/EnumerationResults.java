package org.jclouds.azure.storage.domain.internals;

import javax.xml.bind.annotation.*;

public abstract class EnumerationResults {

   public static final String ROOT_ELEMENT = "EnumerationResults";


   private String serviceEndpoint;

   private String prefix;

   private String marker;

   private int maxResults;

   public String getServiceEndpoint() {
      return serviceEndpoint;
   }

   @XmlAttribute( name = "ServiceEndpoint")
   public void setServiceEndpoint(String serviceEndpoint) {
      this.serviceEndpoint = serviceEndpoint;
   }

   public String getPrefix() {
      return prefix;
   }

   @XmlElement
   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public String getMarker() {
      return marker;
   }

   @XmlElement
   public void setMarker(String marker) {
      this.marker = marker;
   }

   public int getMaxResults() {
      return maxResults;
   }

   @XmlElement
   public void setMaxResults(int maxResults) {
      this.maxResults = maxResults;
   }
}
