package me.robertlit.moblimiter;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class MobLimiter extends JavaPlugin implements Listener {

    private final Map<EntityType, Integer> MAXIMUM_MAP = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMaximumAmounts();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadMaximumAmounts() {
        for (String key : getConfig().getKeys(false)) {
            EntityType type;
            try {
                type = EntityType.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException exception) {
                getLogger().info("Could not find entity of type " + key);
                continue;
            }
            int amount = getConfig().getInt(key, 0);
            MAXIMUM_MAP.put(type, amount);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Integer maximum = MAXIMUM_MAP.get(event.getEntityType());
        if (maximum == null) {
            return;
        }
       long count = Stream.of(event.getLocation().getChunk().getEntities()).filter(entity -> entity.getType() == event.getEntityType()).count();
        if (count >= maximum) {
            event.setCancelled(true);
        }
    }
}
