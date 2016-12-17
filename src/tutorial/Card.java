package tutorial;

import java.io.Serializable;

class Card implements Serializable {
    private String question;
    private String answer;

    Card() {this("", "");}
    Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    void setQuestion(String question) {this.question = question;}
    void setAnswer(String answer) {this.answer = answer;}
    String getQuestion() {return question;}
    String getAnswer() {return answer;}
    //@Override
    //public String toString() {return "(" + question + ", " + answer + ")";}
}
