package dev.hung.compat;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Method;

/** Surume ozel tum API cagrilari burada. 1.20.1 - 1.21.4 arasi. */
public final class VersionCompat {
    private VersionCompat() {}

    // PlayerInventory.selectedSlot: 1.20.1 ve 1.21.4'te public int alan.
    // 1.21.5+ ile private oldu (getSelectedSlot/setSelectedSlot), o surumde burasi degisir.
    public static int getSelectedSlot(PlayerInventory inv) {
        return inv.selectedSlot;
    }

    public static void setSelectedSlot(PlayerInventory inv, int slot) {
        inv.selectedSlot = slot;
    }

    // UpdateSelectedSlotC2SPacket(int) + networkHandler.sendPacket(Packet): 1.20.1 ve 1.21.4'te ayni.
    public static void sendSlotPacket(ClientPlayerEntity player, int slot) {
        ClientPlayNetworkHandler handler = player.networkHandler;
        if (handler != null) {
            handler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    // ItemStack.isOf(Item): 1.20.1 ve 1.21.4'te ayni.
    public static boolean isFirework(ItemStack stack) {
        return stack.isOf(Items.FIREWORK_ROCKET);
    }

    // SwordItem sinifi 1.20.1 ve 1.21.4'te mevcut (1.21.5+ bilesen tabanli, orada tag kullan).
    public static boolean isSword(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    // KeyBinding(String, InputUtil.Type, int, String): 1.20.1 ve 1.21.4'te ayni
    // (1.21.9+ kategori String yerine KeyBinding.Category oldu).
    public static KeyBinding createToggleKey() {
        return new KeyBinding("key.hung.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.hung");
    }

    public static KeyBinding createAutoAttackKey() {
        return new KeyBinding("key.hung.autoattack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "category.hung");
    }

    public static KeyBinding createSoundKey() {
        return new KeyBinding("key.hung.sound", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.hung");
    }

    // isFallFlying (1.20.1) -> isGliding (1.21.2+): intermediary adi ayni (method_6128),
    // bu yuzden Fabric MappingResolver ile calisma zamaninda cozuyoruz, tek kaynak iki surumde derlenir.
    private static Method glideMethod;
    private static boolean glideResolved;

    public static boolean isGliding(PlayerEntity player) {
        if (!glideResolved) {
            glideResolved = true;
            String[] owners = {"net.minecraft.class_1309", "net.minecraft.class_1297"};
            for (String owner : owners) {
                try {
                    String name = FabricLoader.getInstance().getMappingResolver()
                            .mapMethodName("intermediary", owner, "method_6128", "()Z");
                    glideMethod = player.getClass().getMethod(name);
                    break;
                } catch (Throwable ignored) {
                }
            }
            if (glideMethod == null) {
                System.err.println("[hung] isGliding bulunamadi, elytra kontrolu devre disi");
            }
        }
        if (glideMethod == null) return true;
        try {
            return (Boolean) glideMethod.invoke(player);
        } catch (Throwable t) {
            return true;
        }
    }
}
