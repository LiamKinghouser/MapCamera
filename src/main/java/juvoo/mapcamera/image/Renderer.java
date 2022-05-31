package juvoo.mapcamera.image;

import java.awt.image.BufferedImage;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer {
   private BufferedImage image;
   private boolean done = false;

   public void load(BufferedImage image) {
      image = MapPalette.resizeImage(image);
      this.image = image;
   }

   public void render(MapView map, MapCanvas canvas, Player player) {
      if (!this.done) {
         canvas.drawImage(0, 0, this.image);
         this.image = null;
         this.done = true;
      }
   }
}
