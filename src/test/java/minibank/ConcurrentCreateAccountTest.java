package minibank;

import minibank.account.Id;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConcurrentCreateAccountTest {
    @Test
    //if accounts.createAccount() was not synchronised, this test would fail
    public void shouldSupportConcurrentCreateAccounts() {
        Accounts accounts =  new Accounts();
        accounts.createAccount();

        Service service = new Service(accounts);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        int countOfAccounts = 100;

        IntStream.range(0, countOfAccounts).
                forEach(i ->
                        executor.submit(
                                () -> {
                                    return service.createAccount();
                                }));

        try {
            executor.awaitTermination(5, SECONDS);
        } catch (InterruptedException e) {

        }

        executor.shutdown();

        await().atMost(5, SECONDS).until(() -> executor.isShutdown());
        IntStream.range(0, countOfAccounts).
                forEach(i -> assertTrue(service.describeAccount(Id.of(i+1)).isPresent()));
    }
}
