package minibank;

import minibank.account.Amount;
import minibank.account.Id;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @Test
    // there is no deadlocks at the moment because only one object is locked at a time (for changes to amount)
    // expect deadlocks to occur when both accounts are locked.
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

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Id account1 = Id.of(1);
        Id account2 = Id.of(2);
        Id account3 = Id.of(3);

        service.deposit(account1, Amount.of(100));
        service.deposit(account2, Amount.of(100));
        service.deposit(account3, Amount.of(100));
        Amount transferAmount = Amount.of(100);

        executor.submit(() -> service.transfer(account1, account2, transferAmount));
        executor.submit(() -> service.transfer(account1, account3, transferAmount));

        Amount expectedAmountInAccount1 = Amount.of(0);
        Amount expectedAmountInAccount2 = Amount.of(200);
        Amount expectedAmountInAccount3 = Amount.of(100);

        try {
            executor.awaitTermination(5, SECONDS);
        } catch (InterruptedException e) {
        }

        executor.shutdown();

        await().atMost(5, SECONDS).until(() -> executor.isShutdown());
        assertEquals(expectedAmountInAccount1, service.describeAccount(account1).get().getAmount());
        assertEquals(expectedAmountInAccount2, service.describeAccount(account2).get().getAmount());
        assertEquals(expectedAmountInAccount3, service.describeAccount(account3).get().getAmount());
    }
}
