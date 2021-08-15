package de.melanx.utilitix.content.bell;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemHandBell extends BellBase {

    public ItemHandBell(ModX mod, Item.Properties properties) {
        super(mod, properties);
    }

    @Override
    protected boolean entityFilter(LivingEntity entity, ItemStack stack) {
        return entity.isAlive() && entity.getType().is(EntityTypeTags.RAIDERS);
    }

    @Override
    protected boolean notifyNearbyEntities() {
        return true;
    }
}
