package juvoo.mapcamera.image;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class Screenshot {
   
   // Take a screenshot of the entire screen and return it as a BufferedImage
   public static BufferedImage takeScreenshot() {
      Robot robot = null;

      try {
         robot = new Robot();
      } catch (AWTException var2) {
         var2.printStackTrace();
      }

      assert robot != null;

      BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
      return screenshot;
   }
}
