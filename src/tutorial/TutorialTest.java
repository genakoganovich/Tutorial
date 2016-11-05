package tutorial;

import java.awt.*;
import javax.swing.*;

public class TutorialTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new TutorialFrame();
            frame.setTitle("TutorialTest");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
