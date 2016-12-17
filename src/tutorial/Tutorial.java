package tutorial;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

class Tutorial {
    private static final Logger LOGGER = Logger.getLogger(Tutorial.class.getName());
    private ArrayList<Card> quiz;
    private int index;

    Tutorial() {
        quiz = new ArrayList<>();
        index = -1;
    }
    private boolean hasNext() {return index > -1 && index + 1 < quiz.size();}
    private boolean hasPrevious() {return index > 0 && index < quiz.size();}
    private boolean isValid(int index) {return index > -1 && index < quiz.size();}

    void add(Card card) {
        quiz.add(card);
        index++;
    }
    Card move(int index) {
        if(isValid(index)) {
            this.index = index;
            return quiz.get(index);
        } else if(index == quiz.size()) {
            this.index = index - 1;
            return null;
        }else  {
            return null;
        }
    }

    Card next() {
        if(hasNext()) {
            index++;
            return quiz.get(index);
        } else {
            return null;
        }
    }
    Card previous() {
        if(hasPrevious()) {
            index--;
            return quiz.get(index);
        } else {
            index = 0;
            return quiz.get(index);
        }
    }
    void set(Card card) {quiz.set(index, card);}
    int indexOf(Card card) {return quiz.indexOf(card);}
    void save(File file) {
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(quiz);
            //LOGGER.info(quiz.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void load(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
            quiz = (ArrayList<Card>) in.readObject();
        } catch (IOException |  ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    void clear() {
        quiz.clear();
        index = -1;
    }
    @Override
    public String toString() {return quiz.toString();}
    int size() {return quiz.size();}
}
