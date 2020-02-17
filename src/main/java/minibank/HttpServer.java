package minibank;

import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.common.JsonEntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatcher1;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;

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

    Route createRoute() {
        return concat(
                path(accountMatcher, id ->
                        get(() ->
                                {
                                    Optional<AccountDescription> maybeAccountDescription = service.describeAccount(Id.of(id));

                                    if(maybeAccountDescription.isPresent()) {
                                        AccountDescription accountDescription = maybeAccountDescription.get();
                                        return complete(StatusCodes.OK, accountDescription, Jackson.<AccountDescription>marshaller());
                                    } else {
                                        return complete(StatusCodes.UNPROCESSABLE_ENTITY);
                                    }
                                }
                        )),

                path("accounts", () ->
                        get(() ->
                                complete(StatusCodes.OK, service.describeAccounts(), Jackson.<List<AccountDescription>>marshaller()))));
    }
}
