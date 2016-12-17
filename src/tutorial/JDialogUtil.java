package tutorial;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

class JDialogUtil {
    static void installEscapeCloseOperation(final JDialog dialog) {
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            // close the dialog when the user presses escape
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        };
        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        dialog.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }
}
