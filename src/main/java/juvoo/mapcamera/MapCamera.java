package juvoo.mapcamera;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import juvoo.mapcamera.image.Renderer;
import juvoo.mapcamera.image.Screenshot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.plugin.java.JavaPlugin;

public class MapCamera extends JavaPlugin {
   private ArrayList<ItemStack> cameras;

   public void onEnable() {
      if (GraphicsEnvironment.isHeadless()) {
         Bukkit.getServer().getConsoleSender().sendMessage("-------------------------------------------------------------------------------------------");
         Bukkit.getServer().getConsoleSender().sendMessage("Server is running in a headless environment!");
         Bukkit.getServer().getConsoleSender().sendMessage("This means that the plugin cannot take screenshots, and will not function.");
         Bukkit.getServer().getConsoleSender().sendMessage("The server is probably being hosted via a third party service (Aternos, Minehut, etc.)");
         Bukkit.getServer().getConsoleSender().sendMessage("You need to set up a server to host on your own device.");
         Bukkit.getServer().getConsoleSender().sendMessage("For more help, join the support Discord: https://discord.gg/9AnkH7FuGu");
         Bukkit.getServer().getConsoleSender().sendMessage("-------------------------------------------------------------------------------------------");
         this.getServer().getPluginManager().disablePlugin(this);
      } else {
         this.cameras = new ArrayList();
         Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Iterator var1 = Bukkit.getOnlinePlayers().iterator();

            while(var1.hasNext()) {
               Player player = (Player)var1.next();
               if (this.cameras.contains(player.getInventory().getItemInMainHand())) {
                  this.updateCamera(player.getInventory().getItemInMainHand());
               }

               if (this.cameras.contains(player.getInventory().getItemInOffHand())) {
                  this.updateCamera(player.getInventory().getItemInOffHand());
               }
            }

         }, 0L, 5L);
      }
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (args.length == 0 && (label.equalsIgnoreCase("mapcamera") || label.equalsIgnoreCase("mapcamera:mapcamera"))) {
         if (sender instanceof Player) {
            this.giveCamera((Player)sender);
            sender.sendMessage(ChatColor.GREEN + "You have been given a camera!");
         } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
         }
      } else if (args.length != 1) {
         sender.sendMessage(ChatColor.RED + "Invalid Usage. Please try:");
         sender.sendMessage(ChatColor.GREEN + "/mapcamera");
      }

      return true;
   }

   private void giveCamera(Player player) {
      ItemStack camera = new ItemStack(Material.FILLED_MAP);
      this.cameras.add(camera);
      MapMeta cameraMeta = (MapMeta)camera.getItemMeta();

      assert cameraMeta != null;

      cameraMeta.setDisplayName("§fCamera");
      MapView mv = Bukkit.getServer().createMap((World)Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getUID())));
      cameraMeta.setMapView(mv);
      camera.setItemMeta(cameraMeta);
      if (this.inventoryIsFull(player)) {
         player.getWorld().dropItem(player.getLocation(), camera);
      } else {
         player.getInventory().addItem(new ItemStack[]{camera});
      }

   }

   private boolean inventoryIsFull(Player player) {
      int count = 0;

      for(int i = 0; i < 36; ++i) {
         if (player.getInventory().getItem(i) != null) {
            ++count;
         }
      }

      return count == 36;
   }

   private void updateCamera(ItemStack camera) {
      MapMeta cameraMeta = (MapMeta)camera.getItemMeta();
      MapView view = cameraMeta.getMapView();
      view.getRenderers().clear();
      Renderer renderer = new Renderer();
      view.addRenderer(renderer);
      BufferedImage screenshot = Screenshot.takeScreenshot();
      if (screenshot == null) {
         this.cameras.remove(camera);
      } else {
         renderer.load(screenshot);
         view.setScale(Scale.FARTHEST);
         view.setTrackingPosition(false);
      }
   }
}
