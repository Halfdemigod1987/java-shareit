package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findAllItems(Long userId, Integer from, Integer size) {
        if (size != null && from != null) {
            Map<String, Object> parameters = Map.of(
                    "from", from,
                    "size", size
            );
            return get("?&from={from}&size={size}", userId, parameters);
        } else {
            return get("", userId);
        }
    }

    public ResponseEntity<Object> findItemById(Long userId, Integer itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Integer itemId, Map<String, String> updates) {
        return patch("/" + itemId, userId, updates);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Integer itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text, Integer from, Integer size) {
        if (size != null && from != null) {
            Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);
            return get("/search?&text={text}&from={from}&size={size}", userId, parameters);
        } else {
            return get("/search?&text=" + text, userId);
        }
    }

    public ResponseEntity<Object> createComment(Long userId, Integer itemId, CommentDto comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
