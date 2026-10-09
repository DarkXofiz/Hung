package dev.hung.mixin;

import dev.hung.HungConfig;
import dev.hung.SwapState;
import dev.hung.compat.VersionCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// attackEntity(PlayerEntity, Entity): imza 1.20.1 ve 1.21.4'te ayni.
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    // Vanilla syncSelectedSlot cift paket yollamasin diye bu alani elle esitliyoruz.
    @Shadow private int lastSelectedSlot;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void hung$before(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (SwapState.active) return;

        HungConfig cfg = HungConfig.get();
        if (!cfg.enabled) return;

        ClientPlayerEntity self = MinecraftClient.getInstance().player;
        if (self == null || player != self) return;

        if (cfg.onlyPlayers && !(target instanceof PlayerEntity)) return;
        if (cfg.onlyWhileElytra && !VersionCompat.isGliding(player)) return;
        if (cfg.requireFirework && !VersionCompat.isFirework(player.getMainHandStack())) return;

        PlayerInventory inv = player.getInventory();
        int current = VersionCompat.getSelectedSlot(inv);
        int swordIdx = 0; // her zaman 1. slot (hotbar'in en solu)

        if (swordIdx == current) return;
        if (!VersionCompat.isSword(inv.getStack(swordIdx))) return;

        SwapState.active = true;
        SwapState.savedSlot = current;

        VersionCompat.setSelectedSlot(inv, swordIdx);
        VersionCompat.sendSlotPacket(self, swordIdx);
        lastSelectedSlot = swordIdx;
    }

    @Inject(method = "attackEntity", at = @At("RETURN"))
    private void hung$after(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!SwapState.active) return;
        SwapState.active = false;

        ClientPlayerEntity self = MinecraftClient.getInstance().player;
        if (self == null) return;

        // Vurustan hemen sonra fisege geri don (ayni tick, ekranda gorunmez).
        int back = SwapState.savedSlot;
        VersionCompat.setSelectedSlot(self.getInventory(), back);
        VersionCompat.sendSlotPacket(self, back);
        lastSelectedSlot = back;
    }
}
