package minibank;

import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.common.JsonEntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatcher1;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.StringUnmarshallers;
import minibank.account.Amount;
import minibank.account.Id;
import minibank.dto.AccountDescription;
import minibank.error.AppError;
import minibank.error.ResultOrError;

import java.util.List;
import java.util.Optional;

import static akka.http.javadsl.server.PathMatchers.integerSegment;

public class HttpServer extends AllDirectives {
    final private Service service;

    final JsonEntityStreamingSupport jsonStreaming = EntityStreamingSupport.json();

    HttpServer(Service service) {
        this.service = service;
    }

    PathMatcher1<Integer> accountMatcher = PathMatchers.segment("accounts").slash(integerSegment());
    PathMatcher1<Integer> depositToAccountMatcher = PathMatchers.segment("accounts").slash(integerSegment()).slash("deposit");
    PathMatcher1<Integer> transferToAccountMatcher = PathMatchers.segment("accounts").slash(integerSegment()).slash("transfer");

    Route createRoute() {
        return concat(
                path(accountMatcher, id ->
                        pathEnd(() ->
                                get(() -> {
                                    Optional<AccountDescription> maybeAccountDescription = service.describeAccount(Id.of(id));

                                    if (maybeAccountDescription.isPresent()) {
                                        AccountDescription accountDescription = maybeAccountDescription.get();
                                        return complete(StatusCodes.OK, accountDescription, Jackson.<AccountDescription>marshaller());
                                    } else {
                                        return complete(StatusCodes.UNPROCESSABLE_ENTITY);
                                    }
                                }))
                ),
                path(depositToAccountMatcher, id ->
                        post(() ->
                                parameter(StringUnmarshallers.INTEGER, "amount", amount ->
                                {
                                    ResultOrError<AccountDescription> resultOrError = service.deposit(Id.of(id), Amount.of(amount));
                                    return resultOrError.maybeResult.isPresent() ?
                                            complete(StatusCodes.OK,
                                                    resultOrError.maybeResult.get(),
                                                    Jackson.<AccountDescription>marshaller()) :
                                            complete(StatusCodes.UNPROCESSABLE_ENTITY);
                                }))),
                path(transferToAccountMatcher, id ->
                        post(() ->
                                parameter(StringUnmarshallers.INTEGER, "amount", amount ->
                                        parameter(StringUnmarshallers.INTEGER, "recipient", recipient ->
                                        {
                                            ResultOrError<List<AccountDescription>> resultOrError = service.transfer(Id.of(id), Id.of(recipient), Amount.of(amount));
                                            return resultOrError.maybeResult.isPresent() ?
                                                    complete(StatusCodes.OK,
                                                            resultOrError.maybeResult.get(),
                                                            Jackson.<List<AccountDescription>>marshaller()) :
                                                    complete(StatusCodes.UNPROCESSABLE_ENTITY);
                                        })))),
                path("accounts", () ->
                        concat(post(() -> complete(StatusCodes.CREATED, service.createAccount(), Jackson.<AccountDescription>marshaller())),
                                get(() -> complete(StatusCodes.OK, service.describeAccounts(), Jackson.<List<AccountDescription>>marshaller())))))
                ;
    }
}
