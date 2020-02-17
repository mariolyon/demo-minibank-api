package minibank;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import org.junit.Test;

public class DepositToAccountRouteTest extends JUnitRouteTest {
    @Test
    public void requestToDepositToAccountWhenTheAccountExists() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/deposit?amount=20")).assertStatusCode(StatusCodes.OK);
    }

    @Test
    public void requestToDepositToAccountWhenTheAmountIsNotGiven() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/deposit")).assertStatusCode(StatusCodes.NOT_FOUND);
    }

    @Test
    public void requestToDepositToAccountWhenTheAccountDoesNotExist() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/deposit?amount=20"))
                .assertStatusCode(StatusCodes.UNPROCESSABLE_ENTITY);
    }

}
