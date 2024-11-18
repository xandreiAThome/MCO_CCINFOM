package CustomJPanels;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A custom JPanel that displays a background image.
 */
public class JPanelWithBackground extends JPanel {

    private Image backgroundImage;

    /**
     * Constructs a JPanelWithBackground with the specified image file as the
     * background.
     *
     * @param fileName the path to the image file
     * @throws IOException if there is an error reading the image file
     */
    public JPanelWithBackground(String fileName) throws IOException {
        backgroundImage = ImageIO.read(new File(fileName));
    }

    /**
     * Paints the component with the background image.
     *
     * @param g the Graphics object to paint on
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image scaled = backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);

        // Draw the background image.
        g.drawImage(scaled, 0, 0, this);
    }
}

