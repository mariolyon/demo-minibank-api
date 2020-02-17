package minibank;

import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class HttpServer extends AllDirectives {
    Route createRoute() {
        return concat(
                path("hello", () ->
                        get(() ->
                                complete("hi there"))));
    }
}
