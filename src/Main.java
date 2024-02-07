import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static final int POOL_SIZE = 10;
    public static final int THREAD_COUNT = 100_000;
    public static int counter1 = 0;
    public static int  counter2 = 0;

    synchronized public static void counter() {
        counter1++;
        counter2++;
    }

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(int i=0; i<THREAD_COUNT; i++){
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    counter();
                }
            }, threadPool
            );

            futures.add(future);
        }

        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        threadPool.shutdown();

        System.out.printf("%d %d", counter1, counter2);

    }


}