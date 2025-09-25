package core.framework.internal.web.response;

import core.framework.api.http.HTTPStatus;
import core.framework.http.ContentType;
import core.framework.util.Maps;
import core.framework.web.CookieSpec;
import core.framework.web.Response;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * @author neo
 */
public final class ResponseImpl implements Response {
    public final Body body;
    final Map<HttpString, String> headers = Maps.newHashMap();
    @Nullable
    Map<CookieSpec, String> cookies;
    @Nullable
    private ContentType contentType;
    private HTTPStatus status = HTTPStatus.OK;

    public ResponseImpl(Body body) {
        this.body = body;
    }

    @Override
    public HTTPStatus status() {
        return status;
    }

    @Override
    public Response status(HTTPStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public Optional<String> header(String name) {
        return Optional.ofNullable(headers.get(new HttpString(name)));
    }

    @Override
    public Response header(String name, @Nullable String value) {
        if (Headers.CONTENT_TYPE.equalToString(name)) throw new Error("must not use header() to update content type, please use response.contentType()");
        headers.put(new HttpString(name), value);
        return this;
    }

    @Override
    public Optional<ContentType> contentType() {
        return Optional.ofNullable(contentType);
    }

    @Override
    public Response contentType(ContentType contentType) {
        this.contentType = contentType;
        headers.put(Headers.CONTENT_TYPE, contentType.toString());
        return this;
    }

    @Override
    public Response cookie(CookieSpec spec, @Nullable String value) {
        if (cookies == null) cookies = Maps.newHashMap();
        cookies.put(spec, value);
        return this;
    }
}
