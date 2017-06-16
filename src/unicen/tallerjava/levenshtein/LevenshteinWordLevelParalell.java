package unicen.tallerjava.levenshtein;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by lucas on 15/06/17.
 */

/*Esta clase busca paralelizar el trabajo usando el metodo fork-join*/
public class LevenshteinWordLevelParalell extends RecursiveTask<Integer> {
    //Este "Limita Secuencial" puede irse ajustando para encontrar una division del trabajo
    //mas uniforme
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

    //Esta sumatoria es util para estimar la carga de trabajo en un intervalo (sum c - i,i=a to b)
    //Cuenta la cantidad de pares a comparar en un intervalo dado donde "c" es el total de oraciones
    //a y b el comienzo y fin del intervalo y se debe comparar cada oracion del intervalo con las siguientes
    //oraciones hasta el final
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
