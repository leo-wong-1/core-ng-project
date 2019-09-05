package core.framework.impl.web.session;

import core.framework.crypto.Hash;
import core.framework.impl.web.request.RequestImpl;
import core.framework.internal.log.ActionLog;
import core.framework.web.CookieSpec;
import core.framework.web.Request;
import core.framework.web.Response;
import core.framework.web.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author neo
 */
public final class SessionManager {
    private final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private CookieSpec cookieSpec;
    private String header;
    private Duration timeout = Duration.ofMinutes(20);
    private SessionStore store;

    public Session load(Request request, ActionLog actionLog) {
        if (store == null) return null;  // session store is not initialized
        if (!"https".equals(request.scheme())) return null;  // only load session under https

        var session = new SessionImpl();
        sessionId(request).ifPresent(sessionId -> {
            logger.debug("load session");
            Map<String, String> values = store.getAndRefresh(sessionId, timeout);
            if (values != null) {
                actionLog.context("sessionHash", Hash.md5Hex(sessionId));
                session.id = sessionId;
                session.values.putAll(values);
            }
        });
        return session;
    }

    public void save(RequestImpl request, Response response, ActionLog actionLog) {
        SessionImpl session = (SessionImpl) request.session;
        if (session == null || session.saved) return;

        save(session, response, actionLog);
    }

    void save(SessionImpl session, Response response, ActionLog actionLog) {
        session.saved = true;   // it will try to save session on both normal and exception flows, here is to only attempt once in case of store throws exception
        if (session.invalidated) {
            if (session.id != null) {
                logger.debug("invalidate session");
                store.invalidate(session.id);
                putSessionId(response, null);
            }
        } else if (session.changed()) {
            logger.debug("save session");
            if (session.id == null) {
                session.id = UUID.randomUUID().toString();
                actionLog.context("sessionHash", Hash.md5Hex(session.id));
                putSessionId(response, session.id);
            }
            store.save(session.id, session.values, session.changedFields, timeout);
        }
    }

    private Optional<String> sessionId(Request request) {
        if (header != null) return request.header(header);
        return request.cookie(cookieSpec);
    }

    void putSessionId(Response response, String sessionId) {
        if (header != null) {
            response.header(header, sessionId == null ? "" : sessionId);
        } else {
            response.cookie(cookieSpec, sessionId);
        }
    }

    public void store(SessionStore store) {
        if (this.store != null) throw new Error("session store is already configured, previous=" + this.store);
        this.store = store;
    }

    public void timeout(Duration timeout) {
        if (timeout == null) throw new Error("timeout must not be null");
        this.timeout = timeout;
    }

    public void cookie(String name, String domain) {
        if (name == null) throw new Error("name must not be null");
        cookieSpec = new CookieSpec(name).domain(domain).path("/").sessionScope().httpOnly().secure().sameSite();
    }

    public void header(String name) {
        if (name == null) throw new Error("name must not be null");
        header = name;
    }
}
