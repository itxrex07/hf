package p001hx.util;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/* loaded from: classes3.dex */
public final class Tasks {
    private Tasks() {
    }

    public static <T> T await(Future<T> future) throws IOException {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        } catch (ExecutionException e2) {
            throw failureOf(e2);
        }
    }

    public static IOException failureOf(ExecutionException executionException) {
        Throwable cause = executionException.getCause();
        return cause instanceof IOException ? (IOException) cause : new IOException(cause);
    }
}
