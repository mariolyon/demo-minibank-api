package minibank;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import org.junit.Test;

public class GetAccountRouteTest extends JUnitRouteTest {
    @Test
    public void requestToGetAccountWhenTheAccountDoesNotExist() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        testRoute(new HttpServer(service).createRoute()).run(HttpRequest.GET("/accounts/1"))
                .assertStatusCode(StatusCodes.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void requestToGetAccountWhenTheAccountExists() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        Service service = new Service(accounts);
        testRoute(new HttpServer(service).createRoute()).run(HttpRequest.GET("/accounts/1"))
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("{\"amount\":0,\"id\":1}");
    }

}
