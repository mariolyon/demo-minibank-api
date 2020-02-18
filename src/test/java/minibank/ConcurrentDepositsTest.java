package minibank;

import minibank.account.Amount;
import minibank.account.Id;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrentDepositsTest {
    @Test
    public void shouldSupportConcurrentDeposits() {
        Accounts accounts =  new Accounts();
        accounts.createAccount();

        Service service = new Service(accounts);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Id accountId = Id.of(1);


        int countOfDeposits = 100;
        Amount depositAmount = Amount.of(1);

        IntStream.range(0, countOfDeposits).
                forEach(i ->
                        executor.submit(
                                () -> {
                                    return service.deposit(accountId, depositAmount);
                                }));

        Amount expectedAmount = Amount.of(countOfDeposits * depositAmount.value);

        try {
            executor.awaitTermination(5, SECONDS);
        } catch (InterruptedException e) {

        }

        executor.shutdown();

        await().atMost(5, SECONDS).until(() -> executor.isShutdown());
        assertEquals(expectedAmount, service.describeAccount(accountId).get().getAmount());
    }
}
