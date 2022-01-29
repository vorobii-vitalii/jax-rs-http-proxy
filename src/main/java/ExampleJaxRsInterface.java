import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("get")
public interface ExampleJaxRsInterface {

    @GET
    Long getLastUserId();

}
