package com.mysaml.websocket;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Class representing a promise that can be manually resolved or rejected.
 * @param <T> the type of the promised result
 */
public class WsPromise<T> extends CompletableFuture<T> {
    private Status status;

    public WsPromise() {
        this.status = Status.PENDING;
    }

    public WsPromise(long timeout) {
        this.status = Status.PENDING;
        if (timeout > 0) {
            CompletableFuture.delayedExecutor(timeout, TimeUnit.MILLISECONDS).execute(() -> {
                if (!isDone()) {
                    cancel("timeout expired");
                }
            });
        }
    }

    public Status getStatus() {
        return status;
    }

    public boolean isFinished() {
        return status != Status.PENDING;
    }

    public boolean isResolved() {
        return status == Status.RESOLVED;
    }

    public boolean isRejected() {
        return status == Status.REJECTED;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    /**
     * Resolves the promise with the specified value.
     * @param value the value to resolve the promise with
     */
    public void resolve(T value) {
        if (isFinished()) {
            System.out.println("WsPromise already finished");
            return;
        }
        this.status = Status.RESOLVED;
        super.complete(value);
    }

    /**
     * Rejects the promise with the specified reason.
     * @param reason the reason for rejecting the promise
     */
    public void reject(String reason) {
        if (isFinished()) {
            System.out.println("WsPromise already finished");
            return;
        }
        this.status = Status.REJECTED;
        super.completeExceptionally(new RuntimeException(reason));
    }

    /**
     * Cancels the promise with an optional reason.
     * @param reason the reason for cancelling the promise
     */
    public void cancel(String reason) {
        if (isFinished()) {
            System.out.println("WsPromise already finished");
            return;
        }
        this.status = Status.CANCELLED;
        super.completeExceptionally(new CancellationException("Cancelled" + (reason != null ? ": " + reason : "")));
    }

    public enum Status {
        PENDING,
        RESOLVED,
        REJECTED,
        CANCELLED
    }
}
