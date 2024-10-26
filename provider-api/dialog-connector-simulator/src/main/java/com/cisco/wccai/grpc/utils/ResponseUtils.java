package com.cisco.wccai.grpc.utils;

import com.cisco.wcc.ccai.v1.CcaiApi;
import com.cisco.wccai.grpc.model.Response;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ResponseUtils {
    private static final Random RANDOM = new Random();

    private ResponseUtils() {
    }

    public static void sendResponses(Response response, StreamObserver<CcaiApi.StreamingAnalyzeContentResponse> responseObserver) {
        if (response == null || response.getResponses() == null || response.getResponses().isEmpty()) {
            return;
        }
        responseObserver.onNext(response.getResponses().get(0));
        for (int i = 1; i < response.getResponses().size(); i++) {
            int delayMillis = RANDOM.nextInt(2000);
            Uninterruptibles.sleepUninterruptibly(delayMillis, TimeUnit.MILLISECONDS);
            CcaiApi.StreamingAnalyzeContentResponse nextResponse = response.getResponses().get(i);
            responseObserver.onNext(nextResponse);
        }
    }
}
