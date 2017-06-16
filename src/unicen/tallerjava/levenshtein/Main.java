package unicen.tallerjava.levenshtein;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final int TRIM_DISTANCE = 1;
    private static final int SIZE_LIMIT = 500000;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        BufferedReader bis = null;
        ArrayList<LevenshteinSentence> sentences = new ArrayList<>(SIZE_LIMIT);
        StringBuilder stringBuffer= new StringBuilder();

        try {
            bis = new BufferedReader(new InputStreamReader(new FileInputStream("/home/lucas/Descargas/subset.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(bis != null){
            int auxChar;
            String auxString;
            String sentenceString;
            int wordCount = 1;
            while(true) {
                auxChar = bis.read();
                if(auxChar != -1){
                    while((char)auxChar != ' ') {
                        stringBuffer.append((char)auxChar);
                        auxChar = bis.read();
                    }
                    auxString = stringBuffer.toString();
                    stringBuffer.delete(0,stringBuffer.length());
                    auxChar = bis.read();
                    while((char)auxChar != '\r' && (char)auxChar != '\n' && auxChar != -1) {
                        if((char)auxChar == ' ')
                            wordCount++;
                        stringBuffer.append((char)auxChar);
                        auxChar = bis.read();
                    }
                    if((char)auxChar == '\r')
                        bis.skip(1l);
                    sentenceString = stringBuffer.toString();
                    sentences.add(new LevenshteinSentence(auxString, sentenceString, wordCount));
                    stringBuffer.delete(0,stringBuffer.length());
                    wordCount = 1;
                }else{
                    break;
                }
            }
        }
        long startTime = System.currentTimeMillis();
        LevenshteinWordLevelParalell paralellLevenshtein = new LevenshteinWordLevelParalell(sentences,0,sentences.size(),1);
        int found = ForkJoinPool.commonPool().invoke(paralellLevenshtein);
        System.out.println("Tiempo Paralelo Fork Join: " + ((System.currentTimeMillis() - startTime)* 1E-3));
        System.out.println("Found Pairs: " + found);
    }
}
