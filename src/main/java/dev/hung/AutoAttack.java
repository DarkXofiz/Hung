package dev.hung;

import dev.hung.compat.VersionCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

/**
 * X tusuyla acilan trigger bot. Sadece nisangah bir oyuncunun uzerindeyken ve menzildeyken
 * (varsayilan 3 blok) vurur; hedef aramaz, kamerayi cevirmez. Vurus attackEntity uzerinden gectigi icin
 * kilic degisimi mixin tarafindan aynen uygulanir.
 */
public final class AutoAttack {
    private AutoAttack() {}

    private static long tickCounter = 0;
    private static long lastAttackTick = -1000;

    public static void tick(MinecraftClient client) {
        tickCounter++;
        HungConfig cfg = HungConfig.get();
        if (!cfg.enabled || !cfg.autoAttack) return;
        ClientPlayerEntity self = client.player;
        if (self == null || client.world == null || client.interactionManager == null) return;
        if (client.currentScreen != null || SwapState.active) return;
        if (!self.isAlive() || self.isSpectator()) return;

        if (cfg.onlyWhileElytra && !VersionCompat.isGliding(self)) return;
        if (cfg.requireFirework && !VersionCompat.isFirework(self.getMainHandStack())) return;

        // Elindeki fisek hizli doldugu icin kilic bari yerine kendi sayacimizi kullaniyoruz
        // (kilic = 1.6 hiz = ~12.5 tick). Boylece vurus vanilla hizinda ve tam hasarli olur.
        if (tickCounter - lastAttackTick < Math.max(1, cfg.attackDelayTicks)) return;

        // Kritik icin vanilla sartlari: havada, dusuyor, tirmanmiyor, suda degil, bineklikte degil, kor degil.
        if (cfg.critOnly) {
            if (self.isOnGround() || self.fallDistance <= 0f) return;
            if (self.isClimbing() || self.isTouchingWater() || self.hasVehicle()) return;
        }

        // Trigger bot: sadece nisangah (crosshair) dogrudan bir oyuncunun ustundeyken vurur.
        // Etrafta arama / otomatik donme yok; kamerayi sen cevirirsin, vurus nisana girince gelir.
        if (!(client.crosshairTarget instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof PlayerEntity target)) return;
        if (target == self || !target.isAlive() || target.isSpectator()) return;

        double range = Math.max(1.0, Math.min(3.0, cfg.autoAttackRange));
        if (self.squaredDistanceTo(target) > range * range) return;

        lastAttackTick = tickCounter;
        client.interactionManager.attackEntity(self, target);
        self.swingHand(Hand.MAIN_HAND);
    }
}
