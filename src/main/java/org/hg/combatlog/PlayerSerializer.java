package org.hg.combatlog;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PlayerSerializer {
    public static String compressedPlayer(Player player) {
        Map<String, Object> playerData = new HashMap<>();

        // Сохраняем данные игрока
        playerData.put("name", player.getName());
        playerData.put("location", player.getLocation().serialize());
        ItemStack[] weapon = new ItemStack[54];
        weapon[0] = player.getInventory().getItemInMainHand();
        playerData.put("weapon", serializeItems(weapon));
        Gson gson = new Gson();
        return gson.toJson(playerData);
//        return playerData;
    }
    public static class decompressedPlayer{
        public final Location location;
        public final String name;
        public ItemStack weapon;
        public decompressedPlayer(Map<String, Object> playerData){
            location = deserializeLocation((Map<String, Object>) playerData.get("location"));
            name = playerData.get("name").toString();
            weapon = deserializeItems(playerData.get("weapon").toString())[0];

        }
    }
    public static Location deserializeLocation(Map<String, Object> serializedLocation) {
        World world = Bukkit.getWorld(serializedLocation.get("world").toString());
        double x = (double) serializedLocation.get("x");
        double y = (double) serializedLocation.get("y");
        double z = (double) serializedLocation.get("z");
        float yaw = Float.parseFloat(serializedLocation.get("yaw").toString());
        float pitch = Float.parseFloat(serializedLocation.get("pitch").toString());

        return new Location(world, x, y, z, yaw, pitch);
    }
    private static String serializeItems(ItemStack[] items) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.getType() != Material.AIR && item.getType() != Material.BLACK_STAINED_GLASS_PANE) {
                builder.append(i).append('ᴗ').append(item.getAmount()).append('ᴗ').append(item.getType().name()).append('ᴗ');
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    builder.append(itemMetaToString(meta)).append('ᴗ');
                } else {
                    builder.append('ᴗ');//.append('ᴗ');
                }
                builder.append(item.getDurability()).append('ᴗ').append(item.getEnchantments().toString()).append('•');

            }
        }
        return builder.toString();
    }

    private static ItemStack[] deserializeItems(String str) {
        ItemStack[] items = new ItemStack[54];
        String[] pairs = str.split("•");
        for (String pair : pairs) {
            String[] values = pair.split("ᴗ");
            if (values.length >= 5) {
                int slot = Integer.parseInt(values[0]);
                if (slot >= 0 && slot < 54) {
                    int amount = Integer.parseInt(values[1]);
                    Material material = Material.getMaterial(values[2]);
                    if (material != null) {
                        ItemStack item = new ItemStack(material, amount);
                        if (!values[5].isEmpty()) {
                            item = new ItemStack(material, amount, Short.parseShort(values[4]));
                        }
                        ItemMeta meta = item.getItemMeta();
                        if (!values[3].isEmpty()) {
                            item.setItemMeta(stringToItemMeta(values[3]));
                            meta = item.getItemMeta();
                        }
                        items[slot] = item;
                    }
                }
            }
        }
        return items;
    }
    public static String itemMetaToString(ItemMeta itemMeta) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(itemMeta);
            dataOutput.close();
            return new String(outputStream.toByteArray(), StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemMeta stringToItemMeta(String itemMetaString) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(itemMetaString.getBytes(StandardCharsets.ISO_8859_1));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemMeta itemMeta = (ItemMeta) dataInput.readObject();
            dataInput.close();
            return itemMeta;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /// далее не мое
    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = toBase64(playerInventory);
        String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

        return new String[] { content, armor };
    }
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
