package dev.hung;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/** 6 acma/kapama ses seti. Sesler assets/hung/sounds altindaki ozel .ogg dosyalari. */
public final class ToggleSounds {
    private ToggleSounds() {}

    public static final int COUNT = 6;

    public static String name(int set) {
        return switch (set) {
            case 1 -> "Crystal";
            case 2 -> "Bubble";
            case 3 -> "Marimba";
            case 4 -> "Pad";
            case 5 -> "Harp";
            default -> "Sparkle";
        };
    }

    public static void play(int set, boolean on) {
        play(set, on, 1.0f);
    }

    public static void play(int set, boolean on, float pitch) {
        try {
            int s = Math.max(1, Math.min(COUNT, set));
            Identifier id = Identifier.tryParse("hung:s" + s + (on ? "_on" : "_off"));
            if (id == null) return;
            MinecraftClient.getInstance().getSoundManager()
                    .play(PositionedSoundInstance.master(SoundEvent.of(id), pitch, 0.8f));
        } catch (Throwable t) {
            System.err.println("[hung] ses calinamadi: " + t);
        }
    }
}
