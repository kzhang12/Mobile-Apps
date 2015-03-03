package bkim54kzhang12.flashboard;

/**
 * Created by Kevin on 3/2/2015.
 */
public class CardItem {
    private String subject;
    private String question;
    private String answer;

    public CardItem(String s, String q, String a)
    {
        subject = s;
        question = q;
        answer = a;
    }

    public String toString()
    {
        return subject + "  " + question + "  " + answer;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getQuestion()
    {
        return question;
    }

    public String getAnswer()
    {
        return answer;
    }

}
