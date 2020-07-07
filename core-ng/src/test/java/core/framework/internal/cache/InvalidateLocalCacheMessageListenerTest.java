package core.framework.internal.cache;

import core.framework.internal.json.JSONMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author neo
 */
class InvalidateLocalCacheMessageListenerTest {
    private InvalidateLocalCacheMessageListener listener;
    private LocalCacheStore cacheStore;
    private JSONMapper<InvalidateLocalCacheMessage> mapper;

    @BeforeEach
    void createInvalidateLocalCacheMessageListener() {
        cacheStore = mock(LocalCacheStore.class);
        mapper = new JSONMapper<>(InvalidateLocalCacheMessage.class);
        listener = new InvalidateLocalCacheMessageListener(cacheStore, mapper);
    }

    @Test
    void onSubscribe() {
        listener.onSubscribe();

        verify(cacheStore).flushAll();
    }

    @Test
    void onMessage() {
        var message = new InvalidateLocalCacheMessage();
        message.clientIP = "remoteIP";
        message.keys = List.of("key1", "key2");
        listener.onMessage(mapper.toJSON(message));

        verify(cacheStore).delete("key1", "key2");
    }
}