import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

class SumThread implements Runnable {
    long[] mas;
    public int index;
    int size;
    CyclicBarrier cb;
    List<Long> list;

    SumThread(long[] curr_mas, int curr_index, int curr_size, CyclicBarrier curr_cb, List<Long> curr_list) {
        mas = curr_mas;
        index = curr_index;
        size = curr_size;
        cb = curr_cb;
        list = curr_list;
    }

    @Override
    public void run()  {
        list.add(mas[index] + mas[size - 1 - index]);

        try {
            cb.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class Task2 {
    public static void main(String[] args) {
        Task2 main = new Task2();
        main.starter();
    }

    public void starter() {
        int size = 20;
        long SumMas = 0;

        long[] mas = new long[size];

        for (int i = 0; i < size; i++)
            mas[i] = i;

        for (int i = 0; i < size; i++)
            SumMas = SumMas + mas[i];

        System.out.println("Сума елементів масиву:");
        System.out.println(SumMas);

        int NumThread = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(NumThread);

        System.out.println("Сума в багатопоточному режимі:");
        System.out.println(findArraySum(mas, executorService));
    }

    public long findArraySum(long[] mas, ExecutorService executorService) {
        int size = mas.length;

        List<Long> list = new ArrayList<>();
        do {
            list.clear();
            CyclicBarrier cb = new CyclicBarrier(size / 2 + 1);

            for (int i = 0; i < size / 2; i++) {
                executorService.execute(new SumThread(mas, i, size, cb, list));
            }

            try {
                cb.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < size / 2; i++) {
                mas[i] = list.get(i);
            }

            size = size / 2 + size % 2;
        } while (size > 1);

        executorService.shutdown();

        return mas[0];
    }
}