package minibank;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import org.junit.Test;

public class CreateAccountRouteTest extends JUnitRouteTest {
    @Test
    public void requestToCreateAccount() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        testRoute(new HttpServer(service).createRoute()).run(HttpRequest.POST("/accounts"))
                .assertStatusCode(StatusCodes.CREATED)
                .assertEntity("{\"amount\":0,\"id\":1}");
    }
}
