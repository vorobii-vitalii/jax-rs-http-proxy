package org.vitalii.vorobii;

import net.sf.cglib.proxy.Enhancer;

import javax.ws.rs.Path;

public abstract class AbstractHttpClientProxyProvider implements JaxRsHttpClientProxyProvider {

    @Override
    public <T> T createProxy(ClientConfiguration clientConfiguration, Class<T> resourceClass) {
        if (clientConfiguration == null) {
            throw new RuntimeException("Client configuration is null");
        }
        if (resourceClass == null) {
            throw new RuntimeException("Resource class is null");
        }
        if (!resourceClass.isInterface()) {
            throw new RuntimeException("Provided class is not interface -> proxy cannot be created");
        }
        if (!resourceClass.isAnnotationPresent(Path.class)) {
            throw new RuntimeException(
                    "Interface is not marked with javax.ws.rs.Path annotation, so its not valid JAX-RS interface");
        }
        var enhancer = new Enhancer();

        enhancer.setSuperclass(resourceClass);
        enhancer.setCallback(getInterceptor(clientConfiguration, resourceClass));

        return (T) enhancer.create();
    }

    protected abstract <T> JaxRsHttpCallsMethodInterceptor getInterceptor(
            ClientConfiguration clientConfiguration,
            Class<T> resourceClass);

}
