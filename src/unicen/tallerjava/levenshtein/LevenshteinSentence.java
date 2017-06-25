package unicen.tallerjava.levenshtein;

public class LevenshteinSentence {
    String id;
    String sentence;
    int cantWords;

    public LevenshteinSentence( String id, String sentence, int cantWords){
        this.id = id;
        this.sentence = sentence;
        this.cantWords = cantWords;
    }

    public String getId() {
        return id;
    }

    public String getSentence() {
        return sentence;
    }

    public int getCantWords() {
        return cantWords;
    }
}
