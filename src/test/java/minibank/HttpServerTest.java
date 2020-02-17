package minibank;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import org.junit.Test;

public class HttpServerTest extends JUnitRouteTest {
    TestRoute appRoute = testRoute(new HttpServer().createRoute());

    @Test
    public void testCalculatorAdd() {
        appRoute.run(HttpRequest.GET("/hello"))
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("hi there");
    }
}
