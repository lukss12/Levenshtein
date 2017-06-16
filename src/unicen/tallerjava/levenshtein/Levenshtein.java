package unicen.tallerjava.levenshtein;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by lucas on 01/06/17.
 */
public class Levenshtein {
    private static  ExecutorService executor;

    /*Encontrar todos los pares en un conjunto de oraciones cuya distancia levenshtein a nivel palabra
    * sea menor o igual a un valor dado*/
    public static int sequentialWordLevelLevenshtein(List<LevenshteinSentence> sentences, int start,int end, int trim){
        int count = 0;
        SimpleEntry<LevenshteinSentence,LevenshteinSentence> aux;
        for(int i = start; i < end; i++){
            for(int j = i + 1; j < sentences.size(); j++){
                aux = minDistance(sentences.get(i),sentences.get(j),trim);
                if( aux != null ){
                    count++;
                    System.out.println(aux.getKey().getSentence());
                    System.out.println(aux.getValue().getSentence());
                }
            }
        }
        return count;
    }

    /*Calcular si la distancia levenshtein entre 2 oraciones a nivel palabra es menor o igual a
    * un valor dado*/
    public static SimpleEntry<LevenshteinSentence,LevenshteinSentence> minDistance(LevenshteinSentence leftLevenshtein, LevenshteinSentence rightLevenshtein , int trim) {
        int leftSentenceCantWords = leftLevenshtein.getCantWords() + 1; //+1 para la columna inicial del arreglo
        int rightSentenceCantWords = rightLevenshtein.getCantWords() + 1;

        String leftSentence = leftLevenshtein.getSentence();
        String rightStentence = rightLevenshtein.getSentence();

        int realLen0 = leftSentence.length();
        int realLen1 = rightStentence.length();

        //Si la diferencia de palabras es mayor al limite no es posible
        int diff = Math.abs(leftSentenceCantWords - rightSentenceCantWords);
        if(diff > trim)
            return null;

        //Array de distancias, estos arrays se intercambian para no tener que alojar una
        //matriz, ya que el algoritmo es incremental
        int[] cost = new int[leftSentenceCantWords];
        int[] newcost = new int[leftSentenceCantWords];
        int[] swap;

        //Placeholders para las nuevas distancias
        int cost_replace;
        int cost_insert;
        int cost_delete;

        //Costo inicial de pasar todas las palabras-prefijo de la oracion izquierda initial
        for (int j = 0; j < leftSentenceCantWords; j++)
            cost[j] = j;

        //Calcular dinamicamente el array de distancias
        //Donde comienza la palabra actual de la oracion derecha
        int lastRightSentenceIndex = -1;
        //Indices para iterar las oraciones
        int leftSentenceIndex;
        int rightSentenceIndex;
        //Indica si las palabras comparadas son iguales
        int match;
        //Placeholders para los chars leidos de las oraciones
        char leftChar;
        char rightChar;

        boolean finish;
        //Condicion para saber si la distancia requerida puede ser alcanzada
        boolean trimCondition;

        //Comparar cada palabra de la oracion derecha contra todas las de la izquierda
        for (int i = 1; i < rightSentenceCantWords; i++) { // right sentence
            trimCondition = true;
            //Costo de transformar todas las palabras en la oracion derecha
            newcost[0] = i;
            leftSentenceIndex = 0;
            if(lastRightSentenceIndex == -1){
                lastRightSentenceIndex = 0;
            }else{
                lastRightSentenceIndex++;
                while(lastRightSentenceIndex < realLen1){
                    leftChar = rightStentence.charAt(lastRightSentenceIndex);
                    if(leftChar == ' '){
                        lastRightSentenceIndex++;
                        break;
                    }
                    lastRightSentenceIndex++;
                }
            }

            for(int j = 1; j < leftSentenceCantWords; j++) { //left sentence
                rightSentenceIndex = lastRightSentenceIndex;
                //Comparar palabras actuales en cada oracion
                finish = false;
                match = 1;
                while (!finish){
                    if(leftSentenceIndex < realLen0 && rightSentenceIndex < realLen1){
                        leftChar = leftSentence.charAt(leftSentenceIndex);
                        rightChar = rightStentence.charAt(rightSentenceIndex);
                        if(leftChar == rightChar){
                            //Si ambos llegan a un espacio al mismo tiempo las palabras son iguales
                            if(leftChar == ' ' && rightChar == ' ') {
                                match = 0;
                                finish = true;
                            }
                        }else{
                            //Se encontro una diferencia y hay que acomodar la oracion izquierda para la proxima palabra
                            finish = true;
                            while(leftChar != ' ' && ++leftSentenceIndex < realLen0){
                                leftChar = leftSentence.charAt(leftSentenceIndex);
                            }
                        }
                    }else{
                        finish = true;
                        //Si los 2 llegan al final al mismo tiempo las palabras comparadas son iguales
                        if(leftSentenceIndex == realLen0 && rightSentenceIndex == realLen1){
                            match = 0;
                        }else if(leftSentenceIndex == realLen0 && rightStentence.charAt(rightSentenceIndex) == ' '){
                            //Si llega al final uno y el proximo caracter de la otra es un espacio las palabras cmparadas son iguales
                            match = 0;
                        }else if(rightSentenceIndex == realLen1 && leftSentence.charAt(leftSentenceIndex) == ' '){
                            match = 0;
                        }
                    }
                    leftSentenceIndex++;
                    rightSentenceIndex++;
                }

                // computing cost for each transformation
                cost_replace = cost[j - 1] + match;
                cost_insert  = cost[j] + 1;
                cost_delete  = newcost[j - 1] + 1;

                //Nos quedamos con el costo minimo
                newcost[j] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            //Verificar si es posible la distancia pretendida
            //Si ninguno de los costos actuales es menor o igual al pretendido no se puede lograr
            for (int k : newcost) {
                if(k <= trim){
                    trimCondition = false;
                }
            }

            if(trimCondition){
                return null;
            }

            //Se intercambia el array de costos actual para usarlo de base en la proxima iteracion
            swap = cost;
            cost = newcost;
            newcost = swap;
        }

        //System.out.println("Distancia Levenshtein: " + cost[leftSentenceCantWords-1]);
        return new SimpleEntry(leftLevenshtein,rightLevenshtein);
    }
}
