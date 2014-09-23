package org.apache.git.maven;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutionService {
    private static ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactory() {
                                               @Override
                                               public Thread newThread(Runnable r) {
                                                   Thread t = new Thread(r, "Misc-Executions");

                                                   t.setDaemon(true);
                                                   return t;
                                               }
                                           });

    // ~--- methods ------------------------------------------------------------

    public static void execute(final Runnable r) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable e) {
                }
            }
        });
    }
}
