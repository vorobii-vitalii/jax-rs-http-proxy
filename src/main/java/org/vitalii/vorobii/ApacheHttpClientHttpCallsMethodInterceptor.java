package org.vitalii.vorobii;

import lombok.RequiredArgsConstructor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public final class ApacheHttpClientHttpCallsMethodInterceptor implements JaxRsHttpCallsMethodInterceptor {

    private static final String SLASH = "/";
    private static final String ALL_MIME_TYPES = "*/*";
    private static final String EMPTY_STRING = "";

    private final ClientConfiguration clientConfiguration;
    private final Class<?> resourceClass;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        var httpMethod = retrieveHttpMethod(method);

        var url = joinWithSlashIfNeeded(
                joinWithSlashIfNeeded(
                        clientConfiguration().getTargetUrl(),
                        resourceClass.getAnnotation(Path.class).value()),
                Optional.ofNullable(method.getAnnotation(Path.class))
                        .map(Path::value)
                        .orElse(EMPTY_STRING));

        var consumesMimeTypes =
                Optional.ofNullable(method.getAnnotation(Consumes.class))
                    .map(Consumes::value)
                    .orElseGet(() ->
                            Optional.ofNullable(resourceClass.getAnnotation(Consumes.class))
                                    .map(Consumes::value)
                                    .orElse(new String[] {ALL_MIME_TYPES}));

        var producesMimeTypes =
                Optional.ofNullable(method.getAnnotation(Produces.class))
                        .map(Produces::value)
                        .orElseGet(() ->
                                Optional.ofNullable(resourceClass.getAnnotation(Produces.class))
                                        .map(Produces::value)
                                        .orElse(new String[] {ALL_MIME_TYPES}));

        try (var httpclient = HttpClients.createDefault()) {
            var httpUriRequest = RequestBuilder.create(httpMethod.getCode()).setUri(url).build();

            try (var response = httpclient.execute(httpUriRequest)) {
                var entity = response.getEntity();

                try (var content = entity.getContent()) {
                    var byteArrayOutputStream = new ByteArrayOutputStream();

                    int k = content.read();

                    while (k != -1) {
                        byteArrayOutputStream.write(k);
                        k = content.read();
                    }
                    var serializer = chooseSerializer(consumesMimeTypes);

                }
                EntityUtils.consume(entity);
            }
        }

        return null;
    }

    private Serializer chooseSerializer(String[] consumesMimeTypes) {
        var serializerConfig = clientConfiguration.getSerializerConfig();

        var canMatchAll = false;

        for (String mimeType : consumesMimeTypes) {
            if (mimeType.equals(ALL_MIME_TYPES)) {
                canMatchAll = true;
            }
            if (serializerConfig.containsKey(mimeType)) {
                return serializerConfig.get(mimeType);
            }
        }
        if (canMatchAll) {
            return serializerConfig.values()
                    .stream()
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("No serializer was provided"));
        }
        throw new RuntimeException("Unable to find appropriate serializer");
    }

    private HttpRequestMethod retrieveHttpMethod(Method method) {

        assertOneAnnotationIsApplied(
                Set.of(HttpMethod.class, GET.class, POST.class, PUT.class, HEAD.class, OPTIONS.class), method);

        if (method.isAnnotationPresent(HttpMethod.class)) {
            return HttpRequestMethod.valueOf(method.getAnnotation(HttpMethod.class).value());
        }
        if (method.isAnnotationPresent(GET.class)) {
            return HttpRequestMethod.GET;
        }
        if (method.isAnnotationPresent(POST.class)) {
            return HttpRequestMethod.POST;
        }
        if (method.isAnnotationPresent(PUT.class)) {
            return HttpRequestMethod.PUT;
        }
        if (method.isAnnotationPresent(HEAD.class)) {
            return HttpRequestMethod.HEAD;
        }
        return HttpRequestMethod.OPTIONS;
    }

    private void assertOneAnnotationIsApplied(Set<Class<? extends Annotation>> annotations, Method method) {
        var numberOfCandidates = annotations.stream().filter(method::isAnnotationPresent).count();

        if (numberOfCandidates == 0) {
            throw new IllegalArgumentException(
                    "Could not determine HTTP request method because of corresponding annotation absence");
        }
        if (numberOfCandidates > 1) {
            throw new IllegalArgumentException(
                    "Could not determine HTTP request method because there are several options");
        }
    }

    private String joinWithSlashIfNeeded(String path1, String path2) {
        var n2 = path2.length();

        if (path1.endsWith(SLASH) && path2.startsWith(SLASH)) {
            if (n2 == 1) {
                return path1;
            }
            return path1 + path2.substring(1);
        } else if (path1.endsWith(SLASH)) {
            return path1 + path2;
        } else if (path2.startsWith(SLASH)) {
            return path1 + path2;
        } else {
            return path1 + SLASH + path2;
        }
    }

    public ClientConfiguration clientConfiguration() {
        return clientConfiguration;
    }

}
