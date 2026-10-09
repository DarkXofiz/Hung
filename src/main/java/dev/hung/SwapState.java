package dev.hung;

import dev.hung.compat.VersionCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/** Reentrancy bayragi + kaydedilen fisek slotu. */
public final class SwapState {
    private SwapState() {}

    public static boolean active = false;
    public static int savedSlot = 0;

    /** Bir sebeple RETURN calismazsa tick sonunda slotu geri al. */
    public static void recover(MinecraftClient client) {
        if (!active) return;
        active = false;
        ClientPlayerEntity player = client.player;
        if (player != null) {
            VersionCompat.setSelectedSlot(player.getInventory(), savedSlot);
            VersionCompat.sendSlotPacket(player, savedSlot);
        }
    }
}
