package com.khpi.ddruppov.rest;

import com.khpi.ddruppov.rest.request.CreateModelRequest;
import com.khpi.ddruppov.rest.request.PredictForecastRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

@Service
public class PredictorRestImpl implements IPredictorRest {

    private static final Logger LOGGER = Logger.getLogger(PredictorRestImpl.class.toString());
    private static final String SERVER_DOMAIN = "http://localhost:8090/predictor/";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public HttpStatus train(CreateModelRequest request) throws IOException {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("trainFile", getUserFileResource(request.getPathToTrainingFile()));
        map.add("modelName", request.getModelName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>>
            requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
            map, headers);
        ResponseEntity<String> result = restTemplate.exchange(
            SERVER_DOMAIN + "train", HttpMethod.POST, requestEntity,
            String.class);
        return result.getStatusCode();
    }

    @Override
    public List<Double> predict(PredictForecastRequest request) throws IOException {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("dataToPredict", getUserFileResource(request.getPathToDataToPredict()));
        map.add("modelName", request.getPathToModel());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>>
            requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
            map, headers);
        ResponseEntity<List> result = restTemplate.exchange(
            SERVER_DOMAIN + "predict", HttpMethod.POST, requestEntity,
            List.class);
        System.out.println(result.getBody());
        LOGGER.info(result.getBody().toString());
        return (List<Double>) result.getBody();
    }


    @Override
    public String[] getTrainModels() {
        return restTemplate.getForEntity(SERVER_DOMAIN + "models", String[].class).getBody();
    }

    private static Resource getUserFileResource(String fileName) throws IOException {
        //todo replace tempFile with a real file
        System.out.println("fileName: " + fileName + ", " + fileName.replaceFirst("[.][^.]+$", ""));
        Path tempFile = Paths.get(fileName);
        //Files.write(tempFile, Files.readAllBytes(new File(fileName).toPath()));
        File file = tempFile.toFile();
        //to upload in-memory bytes use ByteArrayResource instead
        return new FileSystemResource(file);
    }
}
