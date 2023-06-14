package com.convera.product;


;
import com.convera.payments.data.model.CommonResponse;
import com.convera.payments.data.model.PaymentDetailsAndPayeeesResponseBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class ReadJsonUtil {


    private static ObjectMapper objectMapper = new ObjectMapper();

    public static PaymentDetailsAndPayeeesResponseBean getCommonResponse(String json) {
        objectMapper.registerModule(new JavaTimeModule());
        PaymentDetailsAndPayeeesResponseBean result = null;
        try {
            result = objectMapper.readValue(json, PaymentDetailsAndPayeeesResponseBean.class);
        } catch (JsonProcessingException e) {
            log.error("Exception in getCommonResponse:", e.getMessage());
        }

        return result;
    }


    public static String readFileAsString(String fileName) {
        String jsonString = "";
        try {
            jsonString = Files.readString(Paths.get("src/test/resources/responses/expected" + fileName));
        } catch (IOException ex) {
            log.error("Exception in readFileAsString:",ex);
        }
        return jsonString;
    }

    public static PaymentDetailsAndPayeeesResponseBean getFileContent(String fileName) throws IOException {
        return getCommonResponse(readFileAsString(fileName));
    }

    public static String getFileContentString(String fileName) throws IOException {
        return readFileAsString(fileName);
    }

}
