package de.melanx.utilitix.compat.quark;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.tweaks.module.DoubleDoorOpeningModule;

public class QuarkCompat {

    private static final MutableComponent WARNING = Component.translatable("utilitix.compat.quark.disable_piston_logic_replacement").withStyle(ChatFormatting.RED);

    public static Component warningForGlue() {
        Component component = null;

        if (GeneralConfig.usePistonLogicRepl) {
            component = WARNING;
        }

        return component;
    }

    public static boolean useDoorOpening() {
        return !ModuleLoader.INSTANCE.isModuleEnabled(DoubleDoorOpeningModule.class);
    }
}
