package mysticmods.roots.api.spells;

import mysticmods.roots.api.herbs.Cost;
import mysticmods.roots.api.modifier.Modifier;
import mysticmods.roots.api.property.SpellProperty;
import mysticmods.roots.api.registry.DescribedRegistryEntry;
import mysticmods.roots.api.registry.IHasCost;
import mysticmods.roots.api.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Spell extends DescribedRegistryEntry<Spell> implements IHasCost {
  protected final Type type;
  protected final List<Cost> costs = new ArrayList<>();
  protected final Set<Modifier> modifiers = new HashSet<>();
  protected int cooldown = 0;

  public Spell(Type type, List<Cost> costs) {
    this.type = type;
    setCosts(costs);
  }

  @Override
  public List<Cost> getCosts() {
    return costs;
  }

  @Override
  public void setCosts(List<Cost> costs) {
    this.costs.clear();
    this.costs.addAll(costs);
  }

  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  public abstract SpellProperty<Integer> getCooldownProperty ();

  public int getCooldown() {
    return cooldown;
  }

  public Type getType() {
    return type;
  }

  public void addModifier(Modifier modifier) {
    modifiers.add(modifier);
  }

  protected void initializeProperties () {
    SpellProperty<Integer> cooldownProperty = getCooldownProperty ();
    if (cooldownProperty != null) {
      this.cooldown = cooldownProperty.getValue();
    }
  }

  public abstract void initialize();

  public void init () {
  }

  public abstract void cast(Player pPlayer, ItemStack pStack, InteractionHand pHand, Costing costs, SpellInstance instance, int ticks);

  @Override
  protected String getDescriptor() {
    return "spell";
  }

  @Override
  public ResourceLocation getKey() {
    return Registries.SPELL_REGISTRY.get().getKey(this);
  }

  public enum Type {
    INSTANT,
    CONTINUOUS
  }
}
