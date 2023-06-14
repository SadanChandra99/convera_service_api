package com.convera.product.utils;

import com.convera.common.template.CommonResponse;
import com.convera.common.template.response.ResponseMetadata;
import com.convera.common.template.response.error.ResponseError;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class CommonResponseCustomDeserializer extends StdDeserializer {

    public CommonResponseCustomDeserializer() {
        this(null);
    }

    public CommonResponseCustomDeserializer(Class clazz) {
        super(clazz);
    }

    @Override
    public CommonResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException, IOException {
        CommonResponse commonResponse = new CommonResponse();
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode customMetadata = jsonNode.get("metadata");
        ResponseMetadata responseMetadata = getResponseMetadata(customMetadata);
        commonResponse.setMetadata(responseMetadata);
        return commonResponse;
    }

    private ResponseMetadata getResponseMetadata(JsonNode customMetadata) {
        ResponseMetadata responseMetadata = null;

        if(customMetadata!=null){
            responseMetadata = new ResponseMetadata();

            if(customMetadata.get("timestamp")!=null && StringUtils.isNotBlank(customMetadata.get("timestamp").asText())){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                LocalDateTime dateTime = LocalDateTime.parse(customMetadata.get("timestamp").asText(), formatter);
                responseMetadata.setTimestamp(dateTime);
            }
            if(customMetadata.get("statusCode")!=null){
                responseMetadata.setStatusCode(customMetadata.get("statusCode").asInt());
            }
            if(customMetadata.get("statusDescription")!=null && StringUtils.isNotBlank(customMetadata.get("statusDescription").asText())){
                responseMetadata.setStatusDescription(customMetadata.get("statusDescription").asText());
            }
            if(customMetadata.get("path")!=null && StringUtils.isNotBlank(customMetadata.get("path").asText())){
                responseMetadata.setPath(customMetadata.get("path").asText());
            }

            if(customMetadata.get("correlationId")!=null && StringUtils.isNotBlank(customMetadata.get("correlationId").asText())){
                responseMetadata.setCorrelationId(customMetadata.get("correlationId").asText());
            }

            responseMetadata.setErrors(getErrorList(customMetadata.get("errors")));
        }
        return responseMetadata;
    }

    private List<ResponseError> getErrorList(JsonNode errorsNode) {
        List<ResponseError> result = null;
        if(errorsNode!=null && errorsNode.isArray()){
            result = new ArrayList();
            List<JsonNode> lst = StreamSupport
                    .stream(errorsNode.spliterator(), false)
                    .collect(Collectors.toList());

            for (JsonNode e:lst){
                result.add(getError(e));
            }
        }
        return result;
    }

    private ResponseError getError(JsonNode e) {
        ResponseError error = new ResponseError();
        if (e.get("errorCode") != null) {
            error.setErrorCode(e.get("errorCode").asInt());
        }
        if (e.get("errorDetails") != null && StringUtils.isNotBlank(e.get("errorDetails").asText())) {
            error.setErrorDetails(e.get("errorDetails").asText());
        }
        if (e.get("errorMessage") != null && StringUtils.isNotBlank(e.get("errorMessage").asText())) {
            error.setErrorMessage(e.get("errorMessage").asText());
        }
        if (e.get("serviceName") != null && StringUtils.isNotBlank(e.get("serviceName").asText())) {
            error.setServiceName(e.get("serviceName").asText());
        }
        return error;
    }


}
