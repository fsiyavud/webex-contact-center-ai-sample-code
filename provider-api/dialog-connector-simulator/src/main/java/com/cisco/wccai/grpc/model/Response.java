package com.cisco.wccai.grpc.model;


import com.cisco.wcc.ccai.v1.CcaiApi;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Response {
    private List<CcaiApi.StreamingAnalyzeContentResponse> responses;
}
