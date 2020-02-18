package minibank;

import minibank.account.Amount;
import minibank.account.Id;
import minibank.dto.AccountDescription;
import minibank.error.ResultOrError;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrentTransfersTest {
    @Test
    public void shouldSupportConcurrentTransfers() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        accounts.createAccount();

        Service service = new Service(accounts);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Id account1 = Id.of(1);
        Id account2 = Id.of(2);

        int countOfDeposits = 100;
        Amount transferAmount = Amount.of(1);

        service.deposit(account1, Amount.of(100));
        IntStream.range(0, countOfDeposits).
                forEach(i ->
                        executor.submit(() -> service.transfer(account1, account2, transferAmount)));

        Amount expectedAmountInFromAccount = Amount.of(0);
        Amount expectedAmountInToAccount = Amount.of(countOfDeposits * transferAmount.value);

        try {
            executor.awaitTermination(5, SECONDS);
        } catch (InterruptedException e) {

        }

        executor.shutdown();

        await().atMost(5, SECONDS).until(() -> executor.isShutdown());
        assertEquals(expectedAmountInFromAccount, service.describeAccount(account1).get().getAmount());
        assertEquals(expectedAmountInToAccount, service.describeAccount(account2).get().getAmount());
    }

    @RepeatedTest(10)
    // this test would fail if the objects are not locked in the same order each time
    public void shouldNotHaveDeadlocks() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        accounts.createAccount();

        Service service = new Service(accounts);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Id account1 = Id.of(1);
        Id account2 = Id.of(2);

        int countOfTransfers = 100;
        Amount transferAmount = Amount.of(10);

        service.deposit(account1, Amount.of(100));
        service.deposit(account2, Amount.of(100));
        IntStream.range(0, countOfTransfers).
                forEach(i -> {
                            if (i % 2 == 0)
                                executor.submit(() -> service.transfer(account1, account2, transferAmount));
                            else
                                executor.submit(() -> service.transfer(account2, account1, transferAmount));

                        }
                );

        Amount expectedAmountInAccount1 = Amount.of(100);
        Amount expectedAmountInAccount2 = Amount.of(100);

        try {
            executor.awaitTermination(5, SECONDS);
        } catch (InterruptedException e) {
        }

        executor.shutdown();

        await().atMost(5, SECONDS).until(() -> executor.isShutdown());
        assertEquals(expectedAmountInAccount1, service.describeAccount(account1).get().getAmount());
        assertEquals(expectedAmountInAccount2, service.describeAccount(account2).get().getAmount());
    }

    @RepeatedTest(10)
    // this test sometimes fails (the fromAccount goes negative) if the objects are not locked for the transfer
    public void concurrentTransfersShouldRespectSufficientMoneyForTransferPolicy() {
        Accounts accounts = new Accounts();
        accounts.createAccount();
        accounts.createAccount();
        accounts.createAccount();

        Service service = new Service(accounts);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Id account1 = Id.of(1);
        Id account2 = Id.of(2);
        Id account3 = Id.of(3);

        service.deposit(account1, Amount.of(100));
        service.deposit(account2, Amount.of(100));
        service.deposit(account3, Amount.of(100));
        Amount transferAmount = Amount.of(100);

        AtomicLong expectedValueInAccount1 = new AtomicLong(100);
        AtomicLong expectedValueInAccount2 = new AtomicLong(100);
        AtomicLong expectedValueInAccount3 = new AtomicLong(100);

        executor.submit(() -> {
            ResultOrError<List<AccountDescription>> resultOrError  = service.transfer(account1, account2, transferAmount);
            if (resultOrError.maybeResult.isPresent()) {
                expectedValueInAccount1.set(0);
                expectedValueInAccount2.set(200);
                expectedValueInAccount3.set(100);
            }
        });
        executor.submit(() -> {
            ResultOrError<List<AccountDescription>> resultOrError  = service.transfer(account1, account3, transferAmount);
            if (resultOrError.maybeResult.isPresent()) {
                expectedValueInAccount1.set(0);
                expectedValueInAccount2.set(100);
                expectedValueInAccount3.set(200);
            }
        });

        try {
            executor.awaitTermination(5, SECONDS);
        } catch (InterruptedException e) {
        }

        executor.shutdown();

        await().atMost(5, SECONDS).until(() -> executor.isShutdown());

        //there are two possible states now depending
        assertEquals(expectedValueInAccount1.get(), service.describeAccount(account1).get().getAmount().value);
        assertEquals(expectedValueInAccount2.get(), service.describeAccount(account2).get().getAmount().value);
        assertEquals(expectedValueInAccount3.get(), service.describeAccount(account3).get().getAmount().value);
    }
}
