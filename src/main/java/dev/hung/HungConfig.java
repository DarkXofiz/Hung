package dev.hung;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HungConfig {
    public boolean enabled = true;
    public boolean onlyWhileElytra = true;
    public boolean onlyPlayers = true;
    public boolean requireFirework = true;
    public boolean autoAttack = false;
    public double autoAttackRange = 3.0;    // 1.0-3.0
    public int attackDelayTicks = 13;       // vurus araligi (20 tick = 1 sn); kilic icin 13 ideal
    public boolean critOnly = true;         // sadece kritik vurabildigi anda vur
    public boolean soundEnabled = true;
    public int soundSet = 1;                // 1-6

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HungConfig instance = new HungConfig();

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("hung.json");
    }

    public static HungConfig get() {
        return instance;
    }

    public static void load() {
        Path p = path();
        if (Files.exists(p)) {
            try {
                HungConfig loaded = GSON.fromJson(Files.readString(p), HungConfig.class);
                if (loaded != null) instance = loaded;
            } catch (Exception e) {
                System.err.println("[hung] config okunamadi, varsayilan kullaniliyor: " + e);
            }
        }
        instance.soundSet = Math.max(1, Math.min(6, instance.soundSet));
        instance.attackDelayTicks = Math.max(4, Math.min(40, instance.attackDelayTicks == 0 ? 13 : instance.attackDelayTicks));
        save();
    }

    public static void save() {
        try {
            Files.writeString(path(), GSON.toJson(instance));
        } catch (IOException e) {
            System.err.println("[hung] config yazilamadi: " + e);
        }
    }
}
