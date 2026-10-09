package dev.hung;

import dev.hung.compat.VersionCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public class HungMod implements ClientModInitializer {
    public static final String MOD_ID = "hung";
    private static KeyBinding toggleKey;
    private static KeyBinding soundKey;
    private static KeyBinding autoAttackKey;

    @Override
    public void onInitializeClient() {
        HungConfig.load();
        toggleKey = KeyBindingHelper.registerKeyBinding(VersionCompat.createToggleKey());
        soundKey = KeyBindingHelper.registerKeyBinding(VersionCompat.createSoundKey());
        autoAttackKey = KeyBindingHelper.registerKeyBinding(VersionCompat.createAutoAttackKey());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                HungConfig cfg = HungConfig.get();
                cfg.enabled = !cfg.enabled;
                HungConfig.save();
                if (cfg.soundEnabled) ToggleSounds.play(cfg.soundSet, cfg.enabled);
                if (client.player != null) {
                    // sendMessage(Text, boolean): 1.20.1 ve 1.21.4'te ayni; true = actionbar
                    client.player.sendMessage(Text.literal("hung: " + (cfg.enabled ? "ON" : "OFF")), true);
                }
            }
            while (soundKey.wasPressed()) {
                HungConfig cfg = HungConfig.get();
                cfg.soundSet = cfg.soundSet % ToggleSounds.COUNT + 1;
                cfg.soundEnabled = true;
                HungConfig.save();
                ToggleSounds.play(cfg.soundSet, true);
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("hung sound: " + cfg.soundSet + "/" + ToggleSounds.COUNT + " (" + ToggleSounds.name(cfg.soundSet) + ")"), true);
                }
            }
            while (autoAttackKey.wasPressed()) {
                HungConfig cfg = HungConfig.get();
                cfg.autoAttack = !cfg.autoAttack;
                HungConfig.save();
                if (cfg.soundEnabled) ToggleSounds.play(cfg.soundSet, cfg.autoAttack, 1.25f);
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("hung auto attack: " + (cfg.autoAttack ? "ON" : "OFF")), true);
                }
            }
            AutoAttack.tick(client);
            SwapState.recover(client);
        });
    }
}
