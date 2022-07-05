package com.iot.sync.service.impl;

import com.iot.sync.model.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ResService {
    private final RestTemplate restTemplate = getRestTemplate();

    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();

        HttpClient httpClient =
                HttpClientBuilder.create()
                        .setRedirectStrategy(new LaxRedirectStrategy())
                        .build();

        factory.setHttpClient(httpClient);

        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNTU0NzcwMjU5NyIsInRlcm1pbmFsIjoicGMiLCJleHAiOjE2NTk0ODY1MjUsImlhdCI6MTY1Njg5NDUyNSwianRpIjoiOGQyMmUxNmVhYjk4NGM5ZjljMjJmMjVhOGEyZGMzY2YiLCJ1c2VybmFtZSI6IjE1NTQ3NzAyNTk3In0.cN7vET04REUSOb5M064V7aEfz45eucUdP46L6dc9dV8";
    private static final String cookie = "MAS_AUTH_TICKET=f9a1d3f95fee4d05b2fa854e2a8c9e55; JSESSIONID=481FFE785E61DA6DFC6C384FC178CF4D";
    public String postFile(String url, MultiValueMap<String, Object> map) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setConnection("Keep-Alive");
        headers.setCacheControl("no-cache");
        headers.add("token", TOKEN);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        return responseEntity.getBody();
    }

    public <T> T post(String url, String json, ParameterizedTypeReference<R<T>> responseBodyType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        ResponseEntity<R<T>> resEntity = restTemplate.exchange(url.trim(), HttpMethod.POST, requestEntity, responseBodyType);
        return resEntity.getBody().getData();
    }

    public <T> T postCookie(String url, String json, ParameterizedTypeReference<R<T>> responseBodyType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", cookie);
        headers.add("token", "06da7d0025a5425c947f9b2ec36025c8");
        headers.add("user-agent", "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");


        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        ResponseEntity<R<T>> resEntity = restTemplate.exchange(url.trim(), HttpMethod.POST, requestEntity, responseBodyType);
        return resEntity.getBody().getData();
    }

    public <T> T get(String url, ParameterizedTypeReference<R<T>> responseBodyType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", TOKEN);


        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<R<T>> resEntity = restTemplate.exchange(url.trim(), HttpMethod.GET, requestEntity, responseBodyType);
        return resEntity.getBody().getData();
    }

    public String postString(String url, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        String result = restTemplate.exchange(url.trim(), HttpMethod.POST, requestEntity, String.class).getBody();
        return result;
    }

    public String getString(String url) {
        HttpHeaders headers = new HttpHeaders();

        headers.add("token", TOKEN);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url.trim(), HttpMethod.GET, requestEntity, String.class).getBody();
    }


}
