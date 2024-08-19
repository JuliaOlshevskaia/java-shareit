package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.requests.dto.RequestsDescription;

import java.util.Map;

@Service
public class RequestsClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestsClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(RequestsDescription requestsDescription, Long userId) {
        return post("", userId, requestsDescription);
    }

    public ResponseEntity<Object> getRequestByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getRequestById(Long requestId, Long userId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getRequestByPage(Integer from, Integer size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/owner?state={text}&from={from}&size={size}", userId, parameters);
    }

}
