package epicsquid.roots.integration.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import epicsquid.roots.Roots;
import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.recipe.PyreCraftingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Map;
import java.util.stream.Stream;

@ZenRegister
@ZenClass("mods." + Roots.MODID + ".Pyre")
public class PyreCraftingTweaker {

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient[] inputs) throws IllegalArgumentException {
    addRecipe(name, output, inputs, 0);
  }

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient[] inputs, int xp) throws IllegalArgumentException {
    if (inputs.length != 5) {
      throw new IllegalArgumentException("Pyre Crafting Ritual must have 5 items: " + name);
    }
    CraftTweaker.LATE_ACTIONS.add(new Add(name, CraftTweakerMC.getItemStack(output), Stream.of(inputs).map(CraftTweakerMC::getIngredient).toArray(Ingredient[]::new), xp));
  }

  @ZenMethod
  public static void removeRecipe(IItemStack output) {
    String recipeName = null;
    for (Map.Entry<String, PyreCraftingRecipe> r : ModRecipes.getPyreCraftingRecipes().entrySet()) {
      if (output.matches(InputHelper.toIItemStack(r.getValue().getResult()))) {
        recipeName = r.getKey();
        break;
      }
    }

    if (recipeName == null) {
      CraftTweakerAPI.logError("No Pyre Crafting recipe found for output: " + LogHelper.getStackDescription(output));
    } else {
      CraftTweaker.LATE_ACTIONS.add(new Remove(new ResourceLocation(Roots.MODID, recipeName)));
    }
  }

  private static class Add extends BaseAction {
    private String name;
    private ItemStack output;
    private Ingredient[] inputs;
    private int xp = 0;

    private Add(String name, ItemStack output, Ingredient[] inputs, int xp) {
      super("Pyre Crafting Ritual Add");

      this.name = name;
      this.output = output;
      this.inputs = inputs;
      this.xp = xp;
    }

    @Override
    public String describe() {
      return "Adding Pyre Crafting Ritual for " + LogHelper.getStackDescription(output);
    }

    @Override
    public void apply() {
      PyreCraftingRecipe recipe = new PyreCraftingRecipe(this.output, this.xp);
      recipe.addIngredients((Object[]) inputs);
      recipe.setName(name);
      ModRecipes.addPyreCraftingRecipe(new ResourceLocation(Roots.MODID, name), recipe);
    }
  }

  private static class Remove extends BaseAction {
    private ResourceLocation name;

    @Override
    public String describe() {
      return "Removing Pyre Altar Crafting recipe " + name.toString();
    }

    private Remove(ResourceLocation recipeName) {
      super("Pyre Crafting Ritual");
      this.name = recipeName;
    }

    @Override
    public void apply() {
      ModRecipes.removePyreCraftingRecipe(name);
    }
  }
}
