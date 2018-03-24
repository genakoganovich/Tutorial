package tutorial;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

class InvisibleDialog extends JDialog {
    private TranslucentPane translucentPane;
    private TutorialFrame frame;

    InvisibleDialog(TutorialFrame frame) {
        this.frame = frame;
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        translucentPane = new TranslucentPane(this);
        setContentPane(translucentPane);
        pack();
        setVisible(true);
    }
    public Dimension getPreferredSize() { return Toolkit.getDefaultToolkit().getScreenSize(); }
    TutorialFrame getFrame() {return frame;}
}
class TranslucentPane extends JPanel {
    private Rectangle2D screenRect;
    private InvisibleDialog dialog;

    TranslucentPane(InvisibleDialog dialog) {
        this.dialog = dialog;
        setOpaque(false);
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseHandler());
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if(screenRect != null) {
            g2d.setColor(Color.BLACK);
            g2d.draw(screenRect);
        }
    }
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();
            screenRect = new Rectangle2D.Double(x, y, 1, 1);
        }
        @Override
        public void mouseDragged(MouseEvent event) {
            double width = StrictMath.abs(screenRect.getX() - event.getX());
            double height = StrictMath.abs(screenRect.getY() - event.getY());
            screenRect.setFrame(screenRect.getX(), screenRect.getY(), width, height);
            repaint();
        }
        @Override
        public void mouseReleased(MouseEvent event) {
            try {
                Robot robot = new Robot();
                BufferedImage image = robot.createScreenCapture(new Rectangle(
                        (int) screenRect.getX(),
                        (int) screenRect.getY(),
                        (int) screenRect.getWidth(),
                        (int) screenRect.getHeight())
                );
                //dialog.getFrame().setImage(image);
                ImageSelection imgSel = new ImageSelection(image);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
                dialog.dispose();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
    // This class is used to hold an image while on the clipboard.
    static class ImageSelection implements Transferable
    {
        private Image image;

        public ImageSelection(Image image)
        {
            this.image = image;
        }

        // Returns supported flavors
        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException
        {
            if (!DataFlavor.imageFlavor.equals(flavor))
            {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }

}
