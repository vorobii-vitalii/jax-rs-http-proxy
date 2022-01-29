package org.vitalii.vorobii;

public class ApacheHttpClientProxyProvider extends AbstractHttpClientProxyProvider {

    @Override
    protected <T> JaxRsHttpCallsMethodInterceptor getInterceptor(
            ClientConfiguration clientConfiguration,
            Class<T> resourceClass
    ) {
        return new ApacheHttpClientHttpCallsMethodInterceptor(clientConfiguration, resourceClass);
    }

}
