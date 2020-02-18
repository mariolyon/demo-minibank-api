package minibank;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import minibank.account.Amount;
import minibank.account.Id;
import org.junit.Test;

public class TransferBetweenAccountsRouteTest extends JUnitRouteTest {
    @Test
    public void requestToTransferToAccountWhenTheAccountExists() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        accounts.createAccount();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/deposit?amount=50")).assertStatusCode(StatusCodes.OK).assertEntity("{\"amount\":50,\"id\":1}");;
        route.run(HttpRequest.POST("/accounts/1/transfer?amount=30&recipient=2")).assertStatusCode(StatusCodes.OK).assertEntity("[{\"amount\":20,\"id\":1},{\"amount\":30,\"id\":2}]");;
    }

    @Test
    public void requestToTransferToAccountWhenTheAmountIsNotGiven() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        accounts.createAccount();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/transfer?recipient=2")).assertStatusCode(StatusCodes.NOT_FOUND);
    }

    @Test
    public void requestToTransferToAccountWhenTheRecipientIsNotGiven() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        accounts.createAccount();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/transfer?amount=20")).assertStatusCode(StatusCodes.NOT_FOUND);
    }

    @Test
    public void requestToTransferToAccountWhenTheAccountDoesNotExist() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/transfer?recipient=2&amount=20"))
                .assertStatusCode(StatusCodes.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void requestToTransferToAccountWhenTheRecipientAccountDoesNotExist() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        accounts.createAccount();
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/transfer?recipient=2&amount=20"))
                .assertStatusCode(StatusCodes.UNPROCESSABLE_ENTITY);
    }


    @Test
    public void requestToTransferToAccountWhenTheRecipientAccountIsSameAsSource() {
        Accounts accounts = new Accounts();
        Service service = new Service(accounts);
        accounts.createAccount();
        accounts.createAccount();
        service.deposit(Id.of(1), Amount.of(20));
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/transfer?recipient=1&amount=20"))
                .assertStatusCode(StatusCodes.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void requestToTransferToAccountWhenTheAccountDoesNotHaveEnouhgFunds() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        accounts.createAccount();
        Service service = new Service(accounts);
        TestRoute route = testRoute(new HttpServer(service).createRoute());
        route.run(HttpRequest.POST("/accounts/1/deposit?amount=50")).assertStatusCode(StatusCodes.OK).assertEntity("{\"amount\":50,\"id\":1}");;
        route.run(HttpRequest.POST("/accounts/1/transfer?amount=60&recipient=2")).assertStatusCode(StatusCodes.UNPROCESSABLE_ENTITY);
    }

}
