package minibank;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import org.junit.Test;

public class ListAccountsRouteTest extends JUnitRouteTest {
    TestRoute routes() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        return testRoute(new HttpServer(service).createRoute());
    }

    @Test
    public void requestToListAccountsWhenNoAccountsExist() {
        routes().run(HttpRequest.GET("/accounts"))
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("[]");
    }

    @Test
    public void requestToListAccountsWhenOneAccountExists() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        accounts.createAccount();
        testRoute(new HttpServer(service).createRoute()).run(HttpRequest.GET("/accounts"))
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("[{\"amount\":0,\"id\":1}]");
    }

    @Test
    public void requestToListAccountsWhenTwoAccountsExists() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        accounts.createAccount();
        accounts.createAccount();
        testRoute(new HttpServer(service).createRoute()).run(HttpRequest.GET("/accounts"))
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("[{\"amount\":0,\"id\":1},{\"amount\":0,\"id\":2}]");
    }
}
