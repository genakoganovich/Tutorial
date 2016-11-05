package tutorial;

import java.io.Serializable;

class Entry implements Serializable {
    private String question;
    private String answer;
    Entry() {this("nothing", "nothing");}
    Entry(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    void setQuestion(String question) {this.question = question;}
    void setAnswer(String answer) {this.answer = answer;}
    String getQuestion() {return question;}
    String getAnswer() {return answer;}
    @Override
    public String toString() {return "(" + question + ", " + answer + ")";}
}
