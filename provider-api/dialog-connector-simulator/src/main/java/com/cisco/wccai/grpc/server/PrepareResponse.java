package com.cisco.wccai.grpc.server;

import com.cisco.wcc.ccai.v1.CcaiApi;
import com.cisco.wcc.ccai.v1.Recognize;
import com.cisco.wcc.ccai.v1.Suggestions;
import com.cisco.wcc.ccai.v1.Virtualagent;
import com.cisco.wccai.grpc.model.Response;
import com.cisco.wccai.grpc.utils.LoadProperties;
import com.cisco.wccai.grpc.utils.Utils;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.UnknownFieldSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The type Prepare response.
 */
public class PrepareResponse {
    private static final Random random = new Random();
    /**
     * The constant EN_US.
     */
    public static final String EN_US = "en-US";
    private static Recognize.StreamingRecognitionResult recognitionResult;
    private static final List<List<String>> listOfLists = Lists.newArrayList();
    private static final Properties properties = LoadProperties.getProperties();
    private static final boolean PROMPT_AUDIO_WAV_HEADER_STRIP = Boolean.parseBoolean(
            properties.getProperty("PROMPT_AUDIO_WAV_HEADER_STRIP", "false"));
    private static final int CHUNK_SIZE = 8192;
    /**
     * Instantiates a new Prepare response.
     */
    PrepareResponse() {

    }

    /**
     * Prepare call start response.
     *
     * @return the response
     * @throws IOException the io exception
     */
    public static Response prepareCallStartResponse() throws IOException {
        Virtualagent.VirtualAgentResult result = Virtualagent.VirtualAgentResult.newBuilder().setResponsePayload("CALL_START event received")
                .addPrompts(Virtualagent.Prompt.newBuilder()
                        .setText("setting prompt from dialog simulator")
                        .setAudioContent(ByteString.readFrom(Utils.getInputStreamForBookAFlight()))
                        .setBargein(Boolean.TRUE)
                        .build())
                .setNlu(Virtualagent.NLU.newBuilder()
                        .addReplyText("Hi ! I'm your virtual agent for ticket booking from dialog simulator. How can I assist you today")
                        .setIntent(Virtualagent.Intent.newBuilder()
                                .setDisplayName("Display name from dialog simulator")
                                .setName("name from dialog simulator")
                                .setMatchConfidence(0.95f)
                                .build()))
                .setInputMode(Virtualagent.InputMode.INPUT_VOICE_DTMF)
                .build();
        CcaiApi.StreamingAnalyzeContentResponse response = CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                .setVaResult(result)
                .build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    /**
     * Start of input response.
     *
     * @return the response
     */
    public static Response startOfInputResponse() {
        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setRecognitionResult(
                                Recognize.StreamingRecognitionResult.newBuilder()
                                        .setResponseEvent(Recognize.OutputEvent.EVENT_START_OF_INPUT)
                                        .build()
                        ).build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    /**
     * Prepare partial recognition response.
     *
     * @return the response
     */
    public static Response preparePartialRecognitionResponse() {

        listOfLists.add(Lists.newArrayList("I want to ", "I want to book", "I want to book tickets"));

        List<Recognize.SpeechRecognitionAlternative> speechAlternativeList = new ArrayList<>(100);

        for (int i = 0; i < listOfLists.get(0).size(); i++) {
            speechAlternativeList.add(Recognize.SpeechRecognitionAlternative.newBuilder()
                    .setTranscript(listOfLists.get(0).get(i))
                    .setConfidence(0.876f)
                    .build());

        }
        if (speechAlternativeList.iterator().hasNext()) {
            recognitionResult = Recognize.StreamingRecognitionResult.newBuilder()
                    .setLanguageCode(EN_US)
                    .setMessageType("interim response from dialog simulator")
                    .setIsFinal(false)
                    .addSpeakerIds(1)
                    .setChannelTag(3)
                    .setResultEndTime(Recognize.Duration.newBuilder()
                            .setSeconds(System.currentTimeMillis() / 1000)
                            .build())
                    .addAlternatives(speechAlternativeList.stream().iterator().next())
                    .build();
        }
        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setRecognitionResult(recognitionResult)
                        .build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    /**
     * Prepare final recognition response.
     *
     * @return the response
     */
    public static Response prepareFinalRecognitionResponse() {
        String[] finalList = {"I want to book tickets from Bengaluru to Kolkata", "when will I get the confirmation over mail"};
        List<Recognize.SpeechRecognitionAlternative> speechAlternativeList = new ArrayList<>(100);

        speechAlternativeList.add(Recognize.SpeechRecognitionAlternative.newBuilder()
                .setTranscript(finalList[random.nextInt(1)])
                .setConfidence(0.876f)
                .build());

        if (speechAlternativeList.iterator().hasNext()) {
            recognitionResult = Recognize.StreamingRecognitionResult.newBuilder()
                    .setLanguageCode(EN_US)
                    .setMessageType("final recognition response from dialog connector")
                    .setIsFinal(true)
                    .addSpeakerIds(1)
                    .setChannelTag(3)
                    .setResultEndTime(Recognize.Duration.newBuilder()
                            .setSeconds(System.currentTimeMillis() / 1000)
                            .build())
                    .addAlternatives(speechAlternativeList.stream().iterator().next())
                    .build();
        }
        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setRecognitionResult(recognitionResult)
                        .build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    /**
     * Prepare end of input response.
     *
     * @return the response
     */
    public static Response prepareEndOfInputResponse()
    {
        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setRecognitionResult(
                                Recognize.StreamingRecognitionResult.newBuilder()
                                        .setResponseEvent(Recognize.OutputEvent.EVENT_END_OF_INPUT)
                                        .build()
                        ).build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    /**
     * Prepare aa response.
     *
     * @return the response
     */
    public static Response prepareAAResponse() {
        List<String> snippet = new ArrayList<>(100);
        snippet.add("snippet1");
        Suggestions.Answer answer = Suggestions.Answer.newBuilder().setTitle("response from dialog simulator")
                .setUri("")
                .addSnippets(snippet.get(0))
                .setAnswerRecord("You can generate your mTicket online. Click on the Print/SMS ticket link on the home page on www.redBus.in. Enter your TIN number mentioned on the e-ticket we e-mailed you. Choose the SMS option and click on Submit. In case you don't have a copy of the e-ticket either, contact our call center and our executive will assist you.")
                .setConfidence(0.06597688794136047f)
                .setDescription("I didn't receive my mTicket. Can you re-send it?")
                .setSource("projects/ciscoss-dev-9gkv/knowledgeBases/MTI1MDY1OTA3MjMyMDc4NTYxMjg/documents/MTY2NjE3MzY0MjQwMzg0NjU1MzY")
                .setAnswerRecord("projects/ciscoss-dev-9gkv/answerRecords/6ccb05ec305684c5")
                .setUnknownFields(UnknownFieldSet.newBuilder().build())
                .build();

        Suggestions.AgentAnswer agentAnswer = Suggestions.AgentAnswer.newBuilder().addAnswers(0, answer)
                .build();

        Suggestions.AgentAnswerResult agentAnswerResult = Suggestions.AgentAnswerResult.newBuilder()
                .setAgentanswer(agentAnswer)
                .build();

        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setAgentAnswerResult(agentAnswerResult)
                        .build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    /**
     * Prepare final nlu response.
     *
     * @return the response
     * @throws IOException the io exception
     */
    public static Response prepareFinalVAResponse() throws IOException {
        Virtualagent.VirtualAgentResult result = Virtualagent.VirtualAgentResult.newBuilder().setResponsePayload("Final NLU Response")
                .addPrompts(Virtualagent.Prompt.newBuilder()
                        .setText("setting prompt from dialog simulator for final NLU Response")
                        .setAudioContent(ByteString.readFrom(Utils.getInputStreamForBookAFlight()))
                        .setBargein(Boolean.TRUE)
                        .build())
                .setNlu(Virtualagent.NLU.newBuilder()
                        .setIntent(Virtualagent.Intent.newBuilder()
                                .setMatchConfidence(0.32f)
                                .build()))
                .setInputMode(Virtualagent.InputMode.INPUT_VOICE)
                .build();
        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setVaResult(result)
                        .build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }


    /**
     * Prepare call end response.
     *
     * @return the response
     * @throws IOException the io exception
     */
    public static Response prepareCallEndResponse() throws IOException {
        Virtualagent.VirtualAgentResult result = Virtualagent.VirtualAgentResult.newBuilder().setResponsePayload("CALL_END response")
                .addPrompts(Virtualagent.Prompt.newBuilder()
                        .setText("setting up prompt from dialog simulator for CALL_END event")
                        .setAudioContent(ByteString.readFrom(Utils.getInputStreamForBookAFlight()))
                        .setBargein(Boolean.TRUE)
                        .build())
                .setNlu(Virtualagent.NLU.newBuilder()
                        .setIntent(Virtualagent.Intent.newBuilder()
                                .setMatchConfidence(0.32f)
                                .build()))
                .setInputMode(Virtualagent.InputMode.INPUT_VOICE_DTMF)
                .build();
        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setVaResult(result)
                        .build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    /**
     * Prepare dtmf response response.
     *
     * @return the response
     * @throws IOException the io exception
     */
    public static Response prepareDTMFResponse() throws IOException {
        Virtualagent.VirtualAgentResult result = Virtualagent.VirtualAgentResult.newBuilder()
                .addPrompts(Virtualagent.Prompt.newBuilder()
                        .setText("setting up prompt from dialog simulator for DTMF")
                        .setAudioContent(ByteString.readFrom(Utils.getInputStreamForBookAFlight()))
                        .setBargein(Boolean.TRUE)
                        .build())
                .setInputMode(Virtualagent.InputMode.INPUT_DTMF)
                .setNlu(Virtualagent.NLU.newBuilder()
                        .addReplyText("Reply text for DTMF")
                        .setInputText("DTMF event received from client"))
                .setResponsePayload("Response payload for DTMF event")
                .build();
        CcaiApi.StreamingAnalyzeContentResponse response =
                CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                        .setVaResult(result)
                        .build();
        return Response.builder().responses(Collections.singletonList(response)).build();
    }

    public static Response prepareChunkedVAResponse() throws IOException {
        byte[] audioBytes;
        try (InputStream inputStream = Utils.getInputStreamForVaResponse()) {
            audioBytes = getAudioBytes(inputStream);
        }
        if (PROMPT_AUDIO_WAV_HEADER_STRIP) {
            audioBytes = stripWavHeader(audioBytes);
        }
        List<CcaiApi.StreamingAnalyzeContentResponse> responses = createChunkedResponses(audioBytes, CHUNK_SIZE);
        return Response.builder().responses(responses).build();
    }

    private static byte[] getAudioBytes(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("va_response.wav not found in resources/audio");
        }
        return inputStream.readAllBytes();
    }

    private static byte[] stripWavHeader(byte[] audioBytes) throws IOException {
        int wavHeaderSize = Utils.getWavHeaderSize(audioBytes);
        if (wavHeaderSize <= 0) {
            throw new IOException("Invalid WAV header in va_response.wav");
        }
        return Arrays.copyOfRange(audioBytes, wavHeaderSize, audioBytes.length);
    }

    private static List<CcaiApi.StreamingAnalyzeContentResponse> createChunkedResponses(byte[] audioBytes, int chunkSize) {
        int totalChunks = (int) Math.ceil((double) audioBytes.length / chunkSize);
        return IntStream.range(0, totalChunks)
                .mapToObj(i -> createResponseForChunk(audioBytes, i, chunkSize, totalChunks))
                .toList();
    }

    private static CcaiApi.StreamingAnalyzeContentResponse createResponseForChunk(byte[] audioBytes, int chunkIndex, int chunkSize, int totalChunks) {
        int start = chunkIndex * chunkSize;
        int end = Math.min(start + chunkSize, audioBytes.length);
        byte[] chunk = Arrays.copyOfRange(audioBytes, start, end);

        Virtualagent.Prompt.Builder promptBuilder = Virtualagent.Prompt.newBuilder()
                .setAudioContent(ByteString.copyFrom(chunk))
                .setBargein(true)
                .setFinal(chunkIndex == totalChunks - 1);

        Virtualagent.VirtualAgentResult vaResult = Virtualagent.VirtualAgentResult.newBuilder()
                .addPrompts(promptBuilder.build())
                .build();

        return CcaiApi.StreamingAnalyzeContentResponse.newBuilder()
                .setVaResult(vaResult)
                .build();
    }
}

