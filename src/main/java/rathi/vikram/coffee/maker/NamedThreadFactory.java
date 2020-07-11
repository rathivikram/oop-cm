package rathi.vikram.coffee.maker;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    final AtomicInteger threadNumber = new AtomicInteger(0);
    final String threadName;

    public NamedThreadFactory(Class<?> poolClazz) {
        this.threadName = poolClazz.getSimpleName();
    }

    public NamedThreadFactory(Class<?> poolClazz, String name) {
        this.threadName = poolClazz.getSimpleName() + "-" + name;
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        this.setName(thread);
        return thread;
    }

    protected void setName(Thread thread) {
        thread.setName(this.threadName + "-" + this.threadNumber.getAndIncrement());
    }
}