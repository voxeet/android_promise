package com.voxeet.promise;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.voxeet.promise.solve.Solver;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PromiseQueue {

    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public PromiseQueue() {
        // creates a new queue where every calls will consist of enqueue(solver -> {})
    }

    @NonNull
    public <T> Promise<T> enqueue(@NonNull RunnableNext<T> runnableNext) {
        return new Promise<>(solver -> {
            Runnable runnable = () -> {
                Promise<T> promise = new Promise<>(runnableNext::run);

                promise.then(value -> {
                    solver.resolve(value);
                    HandlerFactory.getHandler().post(this::next);
                }).error(error -> {
                    solver.reject(error);
                    HandlerFactory.getHandler().post(this::next);
                });
            };

            HandlerFactory.getHandler().post(() -> enqueue(runnable));
        });
    }

    @MainThread
    private void enqueue(@NonNull Runnable runnable) {
        queue.add(runnable);

        if (queue.size() == 1) {
            HandlerFactory.getHandler().post(runnable);
        }
    }

    @MainThread
    private void next() {
        try {
            if (!queue.isEmpty()) queue.remove();
        } catch (NoSuchElementException throwable) {
            // silently manage cases where the next item would not exist
            // method called normally from a root or runnable but to simplify its use
        }

        // don't use poll so that the queue will keep reference to the pending call and will let next()
        // method manage the removal & post of the next item if any
        Runnable runnable = queue.peek();
        if (null != runnable) {
            HandlerFactory.getHandler().post(runnable);
        } //else nothing to do since we don't have an item to manage yet, enqueue will manage post()
    }

    public interface RunnableNext<T> {
        void run(@NonNull Solver<T> solver);
    }
}
