package minibank.error;

import java.util.Optional;

public class ResultOrError<T> {
    public final Optional<T> maybeResult;
    public final Optional<AppError> maybeError;

    public ResultOrError(T result) {
        this.maybeResult = Optional.of(result);
        this.maybeError = Optional.empty();
    }

    public ResultOrError(AppError appError) {
        this.maybeResult = Optional.empty();
        this.maybeError = Optional.of(appError);
    }
}
