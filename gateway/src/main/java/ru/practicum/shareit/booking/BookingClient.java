package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingsByUser(String text, Integer from, Integer size, long userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("text", text);
        if (from != null && size != null) {
            parameters.put("from", from);
            parameters.put("size", size);
            return get("?state={text}&from={from}&size={size}", userId, parameters);
        } else if (from == null && size != null) {
            parameters.put("size", size);
            return get("?state={text}&size={size}", userId, parameters);
        } else if (from != null && size == null) {
            parameters.put("from", from);
            return get("?state={text}&from={from}", userId, parameters);
        } else {
            return get("?state={text}", userId, parameters);
        }
    }

    public ResponseEntity<Object> create(long userId, BookingDto request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getBookingById(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> approved(Long bookingId, boolean approved, long userId) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBookingsByOwner(String text, Integer from, Integer size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "state", text,
                "from", from,
                "size", size
        );
        ResponseEntity<Object> o = get("/owner?state={text}&from={from}&size={size}", userId, parameters);
        return get("/owner?state={text}&from={from}&size={size}", userId, parameters);
    }

}
