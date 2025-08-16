package paper.plugin.ndupe;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Ndupe extends JavaPlugin implements Listener {
    private final Map<UUID, Integer> playerShulkerCount = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerLastBreakTime = new ConcurrentHashMap<>();

    public void onEnable() {
        getLogger().info("Ndupe 插件已启用！");
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        getLogger().info("Ndupe 插件已禁用！");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!block.getType().name().contains("SHULKER_BOX")) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastBreakTime = this.playerLastBreakTime.get(playerUUID);
        if (lastBreakTime != null && currentTime - lastBreakTime.longValue() > 60000) {
            this.playerShulkerCount.put(playerUUID, 0);
        }
        this.playerLastBreakTime.put(playerUUID, Long.valueOf(currentTime));
        int currentCount = this.playerShulkerCount.getOrDefault(playerUUID, 0).intValue() + 1;
        this.playerShulkerCount.put(playerUUID, Integer.valueOf(currentCount));
        if (currentCount >= 10) {
            this.playerShulkerCount.put(playerUUID, 0);
            BlockState state = block.getState();
            if (state instanceof ShulkerBox) {
                ShulkerBox shulkerBox = (ShulkerBox) state;
                ItemStack extraShulker = new ItemStack(block.getType());
                ItemMeta meta = extraShulker.getItemMeta();
                if (meta instanceof BlockStateMeta) {
                    BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                    BlockState newBlockState = blockStateMeta.getBlockState();
                    if (newBlockState instanceof ShulkerBox) {
                        ShulkerBox newShulkerBox = (ShulkerBox) newBlockState;
                        newShulkerBox.getInventory().setContents(shulkerBox.getInventory().getContents());
                        PersistentDataContainer originalContainer = shulkerBox.getPersistentDataContainer();
                        PersistentDataContainer newContainer = newShulkerBox.getPersistentDataContainer();
                        for (NamespacedKey key : originalContainer.getKeys()) {
                            if (originalContainer.has(key, PersistentDataType.STRING)) {
                                newContainer.set(key, PersistentDataType.STRING, (String) originalContainer.get(key, PersistentDataType.STRING));
                            } else if (originalContainer.has(key, PersistentDataType.INTEGER)) {
                                newContainer.set(key, PersistentDataType.INTEGER, (Integer) originalContainer.get(key, PersistentDataType.INTEGER));
                            } else if (originalContainer.has(key, PersistentDataType.DOUBLE)) {
                                newContainer.set(key, PersistentDataType.DOUBLE, (Double) originalContainer.get(key, PersistentDataType.DOUBLE));
                            } else if (originalContainer.has(key, PersistentDataType.BYTE)) {
                                newContainer.set(key, PersistentDataType.BYTE, (Byte) originalContainer.get(key, PersistentDataType.BYTE));
                            } else if (originalContainer.has(key, PersistentDataType.LONG)) {
                                newContainer.set(key, PersistentDataType.LONG, (Long) originalContainer.get(key, PersistentDataType.LONG));
                            } else if (originalContainer.has(key, PersistentDataType.FLOAT)) {
                                newContainer.set(key, PersistentDataType.FLOAT, (Float) originalContainer.get(key, PersistentDataType.FLOAT));
                            } else if (originalContainer.has(key, PersistentDataType.BOOLEAN)) {
                                newContainer.set(key, PersistentDataType.BOOLEAN, (Boolean) originalContainer.get(key, PersistentDataType.BOOLEAN));
                            }
                        }
                        blockStateMeta.setBlockState(newShulkerBox);
                        extraShulker.setItemMeta(blockStateMeta);
                        player.getWorld().dropItemNaturally(block.getLocation(), extraShulker);
                        getLogger().info("玩家 " + player.getName() + " 复制了潜影盒");
                    }
                }
            }
        }
    }
}