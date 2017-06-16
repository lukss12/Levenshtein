package unicen.tallerjava.levenshtein;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by lucas on 15/06/17.
 */
public class LevenshteinWordLevelParalell extends RecursiveTask<Integer> {
    private static final long SEQUENTIAL_THRESHOLD = 10000000000l;

    private List<LevenshteinSentence> sentences;
    private int start;
    private int end;
    private int last;
    private int trim;

    public LevenshteinWordLevelParalell(List<LevenshteinSentence> sentences, int start, int end, int trim){
        this.sentences = sentences;
        this.start = start;
        this.end = end;
        this.last = sentences.size();
        this.trim = trim;
    }

    //sum c - i,i=a to b
    public static long summation(long a, long b, long c){
        return (a - b - 1l) * (a + b - 2*c) / 2;
    }

    @Override
    protected Integer compute() {
        long summation = LevenshteinWordLevelParalell.summation(this.start + 1, this.end, this.last);
        if (summation <= SEQUENTIAL_THRESHOLD) {
            System.out.println("Ejecutando de " + this.start + " a "+ this.end + ".Carga: " + summation);
            return Levenshtein.sequentialWordLevelLevenshtein(this.sentences,this.start,this.end,this.trim);
        }else{
            int mid = this.start + (this.end - this.start) / 2;
            LevenshteinWordLevelParalell left = new LevenshteinWordLevelParalell(this.sentences, this.start, mid, this.trim);
            LevenshteinWordLevelParalell right = new LevenshteinWordLevelParalell(this.sentences, mid + 1, this.end, this.trim);
            left.fork();
            int rightResult = right.compute();
            int leftResult = left.join();
            return rightResult + leftResult;
        }
    }
}
