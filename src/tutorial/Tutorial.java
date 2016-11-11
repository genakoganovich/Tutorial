package tutorial;

import java.io.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Logger;

enum Direction {NONE, PREVIOUS, NEXT}

class Tutorial {
    private static final Logger LOGGER = Logger.getLogger(Tutorial.class.getName());
    private ArrayList<Entry> quiz;
    private ListIterator<Entry> iterator;
    private Direction direction;

    Tutorial() {
        quiz = new ArrayList<>();
        iterator = null;
        direction = Direction.NONE;
    }
    void add(Entry entry) {
        quiz.add(entry);
        iterator = quiz.listIterator(quiz.size());
        direction = Direction.NONE;
    }
    Entry next() {
        if(iterator == null) {
            return null;
        } else if(!iterator.hasNext()) {
            return null;
        } else {
            if(direction == Direction.PREVIOUS) {
                iterator = quiz.listIterator(iterator.nextIndex() + 1);
            }
            return returnNext();
        }
    }
    Entry get(int index) {
        iterator = quiz.listIterator(index);
        if(iterator.hasNext()) {
            return returnNext();
        } else {
            return null;
        }
    }
    private Entry returnNext() {
        direction = Direction.NEXT;
        return iterator.next();
    }
    Entry previous() {
        if(iterator == null) {
            return null;
        } else if(!iterator.hasPrevious()) {
            if(!quiz.isEmpty()) {
                return quiz.get(0);
            } else {
                return null;
            }
        } else {
            if(direction == Direction.NEXT || direction == Direction.NONE) {
                iterator = quiz.listIterator(iterator.previousIndex());
            }
            direction = Direction.PREVIOUS;
            return iterator.previous();
        }
    }
    void set(Entry entry) {
        iterator.set(entry);
    }
    int indexOf(Entry entry) {return quiz.indexOf(entry);}
    void save(File file) {
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(quiz);
            LOGGER.info(quiz.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void load(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
            quiz = (ArrayList<Entry>) in.readObject();
            iterator = quiz.listIterator();
        } catch (IOException |  ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    void clear() {
        quiz.clear();
        iterator = null;
    }
    @Override
    public String toString() {return quiz.toString();}
    int size() {return quiz.size();}
}
