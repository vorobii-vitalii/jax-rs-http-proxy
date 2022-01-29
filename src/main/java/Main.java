import org.vitalii.vorobii.AbstractHttpClientProxyProvider;
import org.vitalii.vorobii.ApacheHttpClientProxyProvider;
import org.vitalii.vorobii.ClientConfiguration;

public class Main {

    public static void main(String[] args) {
        AbstractHttpClientProxyProvider abstractHttpClientProxyProvider =
                new ApacheHttpClientProxyProvider();

        final ExampleJaxRsInterface proxy = abstractHttpClientProxyProvider.createProxy(
                ClientConfiguration.builder().targetUrl("http://httpbin.org").build(),
                ExampleJaxRsInterface.class
        );
        proxy.getLastUserId();
    }

}
