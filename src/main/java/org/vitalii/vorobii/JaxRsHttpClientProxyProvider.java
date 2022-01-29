package org.vitalii.vorobii;

public interface JaxRsHttpClientProxyProvider {
    <T> T createProxy(ClientConfiguration clientConfiguration, Class<T> resourceClass);
}
