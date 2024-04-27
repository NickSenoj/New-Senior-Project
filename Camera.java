import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Camera {

    public static void captureImage(String fileName) {
        try {
            // Get the default video device
            java.awt.Robot robot = new java.awt.Robot();

            // Capture the screen
            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage image = robot.createScreenCapture(new java.awt.Rectangle(screenSize));

            // Save the image to a file
            File outputFile = new File(fileName);
            ImageIO.write(image, "jpg", outputFile);

            System.out.println("Image captured and saved to " + fileName);
        } catch (IOException | java.awt.AWTException e) {
            System.err.println("Error capturing image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Capture an image and save it to "image.jpg"
        captureImage("image.jpg");
    }
}
