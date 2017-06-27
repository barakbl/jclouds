package org.jclouds.azure.storage.parser;

import com.google.common.base.Function;
import org.jclouds.azure.storage.domain.CreateQueueResponse;
import org.jclouds.http.HttpResponse;

import javax.inject.Singleton;

@Singleton
public class ParseCreateQueueResponse implements Function<HttpResponse, CreateQueueResponse> {
    @Override
    public CreateQueueResponse apply(HttpResponse httpResponse) {
        CreateQueueResponse response = new CreateQueueResponse();
        response.setSuccess(httpResponse.getStatusCode() == 201);
        return response;
    }
}
