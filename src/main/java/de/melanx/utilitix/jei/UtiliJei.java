//package de.melanx.utilitix.jei;
//
//import com.google.common.collect.ImmutableList;
//import de.melanx.utilitix.UtilitiX;
//import de.melanx.utilitix.content.brewery.ScreenAdvancedBrewery;
//import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
//import de.melanx.utilitix.recipe.BreweryRecipe;
//import de.melanx.utilitix.recipe.EffectTransformer;
//import de.melanx.utilitix.registration.ModBlocks;
//import de.melanx.utilitix.registration.ModEntities;
//import de.melanx.utilitix.registration.ModItems;
//import de.melanx.utilitix.registration.ModRecipes;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.registration.IGuiHandlerRegistration;
//import mezz.jei.api.registration.IRecipeCatalystRegistration;
//import mezz.jei.api.registration.IRecipeCategoryRegistration;
//import mezz.jei.api.registration.IRecipeRegistration;
//import mezz.jei.api.runtime.IJeiRuntime;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.minecraft.resources.ResourceLocation;
//
//import javax.annotation.Nonnull;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@JeiPlugin
//public class UtiliJei implements IModPlugin {
//
//    private static IJeiRuntime runtime = null;
//
//    @Nonnull
//    @Override
//    public ResourceLocation getPluginUid() {
//        return new ResourceLocation(UtilitiX.getInstance().modid, "jeiplugin");
//    }
//
//    @Override
//    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
//        registration.addRecipeCategories(
//                new BreweryCategory(registration.getJeiHelpers().getGuiHelper()),
//                new GildingCategory(registration.getJeiHelpers().getGuiHelper())
//        );
//    }
//
//    @Override
//    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
//        ClientLevel level = Minecraft.getInstance().level;
//        RecipeManager recipes = Objects.requireNonNull(world).getRecipeManager();
//        List<BreweryRecipe> simpleBrewery = recipes.getAllRecipesFor(ModRecipes.BREWERY).stream()
//                .filter(r -> r.getAction() instanceof EffectTransformer.Apply)
//                .collect(Collectors.toList());
//        registration.addRecipes(simpleBrewery, BreweryCategory.ID);
//        registration.addRecipes(GildingArmorRecipe.getRecipes(), GildingCategory.ID);
//
//        registration.addIngredientInfo(new ItemStack(ModBlocks.advancedBrewery), VanillaTypes.ITEM, "description.utilitix.advanced_brewery", "description.utilitix.advanced_brewery.brewing", "description.utilitix.advanced_brewery.merging", "description.utilitix.advanced_brewery.upgrading", "description.utilitix.advanced_brewery.cloning");
//        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.comparatorRedirectorUp), new ItemStack(ModBlocks.comparatorRedirectorDown)), VanillaTypes.ITEM, "description.utilitix.comparator_redirector");
//        registration.addIngredientInfo(new ItemStack(ModBlocks.weakRedstoneTorch), VanillaTypes.ITEM, "description.utilitix.weak_redstone_torch");
//        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModItems.tinyCoal), new ItemStack(ModItems.tinyCharcoal)), VanillaTypes.ITEM, "description.utilitix.tiny_coal");
//        registration.addIngredientInfo(new ItemStack(ModItems.handBell), VanillaTypes.ITEM, "description.utilitix.hand_bell");
//        registration.addIngredientInfo(new ItemStack(ModItems.mobBell), VanillaTypes.ITEM, "description.utilitix.mob_bell");
//        registration.addIngredientInfo(new ItemStack(ModItems.failedPotion), VanillaTypes.ITEM, "description.utilitix.failed_potion");
//        registration.addIngredientInfo(new ItemStack(ModItems.armedStand), VanillaTypes.ITEM, "description.utilitix.armed_stand");
//        registration.addIngredientInfo(new ItemStack(ModItems.glueBall), VanillaTypes.ITEM, "description.utilitix.glue_ball");
//        registration.addIngredientInfo(new ItemStack(ModItems.linkedCrystal), VanillaTypes.ITEM, "description.utilitix.linked_crystal");
//        registration.addIngredientInfo(new ItemStack(ModBlocks.linkedRepeater), VanillaTypes.ITEM, "description.utilitix.linked_repeater");
//        registration.addIngredientInfo(new ItemStack(ModItems.minecartTinkerer), VanillaTypes.ITEM, "description.utilitix.minecart_tinkerer");
//        registration.addIngredientInfo(new ItemStack(ModBlocks.highspeedRail), VanillaTypes.ITEM, "description.utilitix.highspeed_rail");
//        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.directionalRail), new ItemStack(ModBlocks.directionalHighspeedRail)), VanillaTypes.ITEM, "description.utilitix.directional_rail");
//        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.crossingRail), new ItemStack(ModBlocks.reinforcedCrossingRail)), VanillaTypes.ITEM, "description.utilitix.crossing_rail");
//        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.filterRail), new ItemStack(ModBlocks.reinforcedFilterRail)), VanillaTypes.ITEM, "description.utilitix.filter_rail");
//        registration.addIngredientInfo(new ItemStack(ModBlocks.reinforcedRail), VanillaTypes.ITEM, "description.utilitix.reinforced_rail");
//        registration.addIngredientInfo(new ItemStack(ModEntities.enderCart.item()), VanillaTypes.ITEM, "description.utilitix.ender_cart");
//        registration.addIngredientInfo(new ItemStack(ModEntities.pistonCart.item()), VanillaTypes.ITEM, "description.utilitix.piston_cart");
//        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.pistonControllerRail), new ItemStack(ModBlocks.reinforcedPistonControllerRail)), VanillaTypes.ITEM, "description.utilitix.piston_controller_rail");
//        registration.addIngredientInfo(new ItemStack(ModEntities.stonecutterCart.item()), VanillaTypes.ITEM, "description.utilitix.stonecutter_cart");
//        registration.addIngredientInfo(new ItemStack(ModEntities.anvilCart.item()), VanillaTypes.ITEM, "description.utilitix.anvil_cart");
//        registration.addIngredientInfo(new ItemStack(ModBlocks.crudeFurnace), VanillaTypes.ITEM, "description.utilitix.crude_furnace");
//    }
//
//    @Override
//    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
//        registration.addRecipeCatalyst(new ItemStack(ModBlocks.advancedBrewery), BreweryCategory.ID);
//        registration.addRecipeCatalyst(new ItemStack(Blocks.SMITHING_TABLE), GildingCategory.ID);
//    }
//
//    @Override
//    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
//        registration.addRecipeClickArea(ScreenAdvancedBrewery.class, 98, 17, 7, 26, BreweryCategory.ID);
//    }
//
//    @Override
//    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
//        runtime = jeiRuntime;
//    }
//
//    public static void runtime(Consumer<IJeiRuntime> action) {
//        if (runtime != null) {
//            action.accept(runtime);
//        }
//    }
//
//    public static <T> Optional<T> runtime(Function<IJeiRuntime, T> action) {
//        if (runtime != null) {
//            return Optional.of(action.apply(runtime));
//        } else {
//            return Optional.empty();
//        }
//    }
//}
