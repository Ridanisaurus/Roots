package epicsquid.roots.integration.crafttweaker.recipes;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import epicsquid.roots.recipe.IRootsRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

// TODO: Utility class?
public interface CTTransformer<T extends TileEntity> extends IRootsRecipe<T> {
  static <T extends TileEntity> List<ItemStack> transformIngredients(List<IIngredient> ingredients, List<ItemStack> items, T entity) {
    List<IIngredient> postCopy = new ArrayList<>(ingredients);
    List<ItemStack> result = new ArrayList<>();

    for (ItemStack orig : items) {
      IItemStack inSlot = CraftTweakerMC.getIItemStack(orig);

      IIngredient match = null;
      for (IIngredient ingredient : postCopy) {
        if (ingredient.matches(inSlot)) {
          match = ingredient;
          break;
        }
      }
      if (match == null) {
        continue;
      }
      if (!postCopy.remove(match)) {
        continue;
      }
      boolean container = true;
      if (match.hasNewTransformers()) {
        IItemStack transformed;
        try {
          transformed = match.applyNewTransform(inSlot);
        } catch (Throwable e) {
          CraftTweakerAPI.logError("Could not execute recipe transformer on " + match.toCommandString(), e);
          continue;
        }

        result.add(CraftTweakerMC.getItemStack(transformed));
        container = false;
      }
      if (container) {
        ItemStack item = ForgeHooks.getContainerItem(orig);
        if (!item.isEmpty()) {
          result.add(item);
        }
      }
    }

    return result;
  }
}
