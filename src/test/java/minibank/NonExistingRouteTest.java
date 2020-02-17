package minibank;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import org.junit.Test;

public class NonExistingRouteTest extends JUnitRouteTest {
    TestRoute routes() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        return testRoute(new HttpServer(service).createRoute());
    }

    @Test
    public void requestForNonExistingRoute() {
        routes().run(HttpRequest.GET("/non-existing-route"))
                .assertStatusCode(StatusCodes.NOT_FOUND);
    }
}
