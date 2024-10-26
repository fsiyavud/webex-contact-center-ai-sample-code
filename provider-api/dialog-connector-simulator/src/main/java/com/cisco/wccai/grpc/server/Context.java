package com.cisco.wccai.grpc.server;


import com.cisco.wccai.grpc.model.Response;
import com.cisco.wccai.grpc.model.State;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;


public class Context {

    private static final Map<State, Response> responseMap = new EnumMap<>(State.class);

    private Context() {
    }

    public static void init() throws IOException {

        responseMap.put(State.CALL_START, PrepareResponse.prepareCallStartResponse());
        responseMap.put(State.START_OF_INPUT, PrepareResponse.startOfInputResponse());
        responseMap.put(State.PARTIAL_RECOGNITION, PrepareResponse.preparePartialRecognitionResponse());
        responseMap.put(State.END_OF_INPUT, PrepareResponse.prepareEndOfInputResponse());
        responseMap.put(State.FINAL_RECOGNITION, PrepareResponse.prepareFinalRecognitionResponse());
        responseMap.put(State.VA, PrepareResponse.prepareFinalVAResponse());
        responseMap.put(State.AA, PrepareResponse.prepareAAResponse());
        responseMap.put(State.DTMF, PrepareResponse.prepareDTMFResponse());
        responseMap.put(State.CALL_END, PrepareResponse.prepareCallEndResponse());
        responseMap.put(State.VA_CHUNKED, PrepareResponse.prepareChunkedVAResponse());
    }

    public static Response getResponse(State state){
        return responseMap.get(state);
    }
}
