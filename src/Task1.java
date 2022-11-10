import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

class SumThreadOther implements Runnable{

    public long partSum = 0;
    int[] mas;
    int begin, end;
    CyclicBarrier cb;
    List<Long> partialResults;

    SumThreadOther(int[] curr_mas, int curr_begin, int curr_end, CyclicBarrier curr_cb, List<Long> curr_partialResults) {
        mas = curr_mas;
        begin = curr_begin;
        end = curr_end;
        cb = curr_cb;
        partialResults = curr_partialResults;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (int i = begin; i <= end; i++) {
            partSum += mas[i];
        }

        partialResults.add(partSum);

        try {
            cb.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class AggregatorThread implements Runnable {
    List<Long> partialResults;

    AggregatorThread(List<Long> curr_partialResults) {
        partialResults = curr_partialResults;
    }

    @Override
    public void run() {
        long sum = 0;

        for (Long threadResult : partialResults) {
            sum += threadResult;
        }

        System.out.println("Сума в багатопоточному режимі:");
        System.out.println(sum);
    }
}

class Task1 {
    public static void main(String[] args) {
        Task1 main = new Task1();
        main.starter();
    }

    public void starter(){
        int size = 1000000;
        int[] mas = new int[size];
        long SumMas = 0;
        for (int i = 0; i < size; i++)
            mas[i] = i;

        for (int i = 0; i < size; i++)
            SumMas = SumMas + mas[i];
        System.out.println("Сума елементів масиву:");
        System.out.println(SumMas);

        int NumThread = 10;

        int[] beginMas = new int[NumThread];
        int[] endMas = new int[NumThread];
        for (int i = 0; i < NumThread; i++)
            beginMas[i] = size / NumThread * i;
        for (int i = 0; i < NumThread - 1; i++)
            endMas[i] = beginMas[i + 1] - 1;
        endMas[NumThread - 1] = size - 1;

        List<Long> partialResults = Collections.synchronizedList(new ArrayList<>());
        CyclicBarrier cb = new CyclicBarrier(NumThread, new AggregatorThread(partialResults));

        for (int i = 0; i < NumThread; i++) {
            new SumThreadOther(mas, beginMas[i], endMas[i], cb, partialResults);
        }
    }
}