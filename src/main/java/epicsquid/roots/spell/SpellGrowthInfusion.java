package epicsquid.roots.spell;

import epicsquid.mysticallib.network.PacketHandler;
import epicsquid.mysticallib.util.ItemUtil;
import epicsquid.mysticallib.util.RayCastUtil;
import epicsquid.mysticallib.util.Util;
import epicsquid.mysticalworld.recipe.Ingredients;
import epicsquid.roots.Roots;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.mechanics.Growth;
import epicsquid.roots.modifiers.*;
import epicsquid.roots.modifiers.instance.staff.StaffModifierInstanceList;
import epicsquid.roots.network.fx.MessageLifeInfusionFX;
import epicsquid.roots.network.fx.MessageRampantLifeInfusionFX;
import epicsquid.roots.properties.Property;
import epicsquid.roots.recipe.FlowerRecipe;
import epicsquid.roots.recipe.OreChances;
import epicsquid.roots.util.OreDictCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreIngredient;

import java.util.List;
import java.util.Random;

public class SpellGrowthInfusion extends SpellBase {
  public static Property.PropertyCooldown PROP_COOLDOWN = new Property.PropertyCooldown(0);
  public static Property.PropertyCastType PROP_CAST_TYPE = new Property.PropertyCastType(EnumCastType.CONTINUOUS);
  public static Property.PropertyCost PROP_COST_1 = new Property.PropertyCost(0, new SpellCost("terra_moss", 0.08));
  public static Property<Integer> PROP_RADIUS_X = new Property<>("radius_x", 7).setDescription("radius on the X axis of the area in which the spell takes effect");
  public static Property<Integer> PROP_RADIUS_Y = new Property<>("radius_y", 7).setDescription("radius on the Y axis of the area in which the spell takes effect");
  public static Property<Integer> PROP_RADIUS_Z = new Property<>("radius_z", 7).setDescription("radius on the Z axis of the area in which the spell takes effect");
  public static Property<Integer> PROP_RADIUS_BREED_X = new Property<>("radius_breed_x", 6).setDescription("radius on the X axis of the area in which the spell takes effect for the purposes of causing animals to go into breeding mode");
  public static Property<Integer> PROP_RADIUS_BREED_Y = new Property<>("radius_breed_y", 6).setDescription("radius on the Y axis of the area in which the spell takes effect for the purposes of causing animals to go into breeding mode");
  public static Property<Integer> PROP_RADIUS_BREED_Z = new Property<>("radius_breed_z", 6).setDescription("radius on the Z axis of the area in which the spell takes effect for the purposes of causing animals to go into breeding mode");
  public static Property<Integer> PROP_RADIUS_BOOST = new Property<>("radius_boost", 8).setDescription("how much the radius of the spell is boosted by with each rampant growth modifier");
  public static Property<Integer> PROP_TICKS = new Property<>("ticks", 3).setDescription("the number of times a random chance to grow the crop is applied every tick");
  public static Property<Integer> PROP_COUNT = new Property<>("count", 2).setDescription("the number of crops selected to be grown each tick");
  public static Property<Integer> PROP_ADDITIONAL_COUNT = new Property<>("additional_count", 4).setDescription("an additional number of crops from zero to the specified value minus 1 added to the default count");
  public static Property<Float> PROP_EMBIGGEN_CHANCE = new Property<>("embiggen_chance", 0.05f).setDescription("chance per tick to turn a mushroom into a big mushroom");
  public static Property<Integer> PROP_ANIMAL_GROWTH = new Property<>("animal_growth_base", 20).setDescription("default number of ticks to age an entity by (1 second per channel)");
  public static Property<Integer> PROP_ANIMAL_GROWTH_VARY = new Property<>("animal_growth_vary", 4 * 20).setDescription("additional ticks to age an entity by (varying from 0 to this value)");
  public static Property<Integer> PROP_VILLAGER_GROWTH = new Property<>("villager_growth_base", 20).setDescription("default number of ticks to age an entity by (1 second per channel)");
  public static Property<Integer> PROP_VILLAGER_GROWTH_VARY = new Property<>("villager_growth_vary", 4 * 20).setDescription("additional ticks to age an entity by (varying from 0 to this value)");
  public static Property<String> PROP_STONE_DICT = new Property<>("stone_dictionary", "stone").setDescription("the ore dictionary entry that should be used to determine if a block can be targetted for ore conversion");
  public static Property<Float> PROP_STONE_CHANCE = new Property<>("stone_chance", 0.01f).setDescription("the chance per tick of eligible stone being converted to ore");

  public static Modifier RADIUS1 = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "rampant_growth_i"), ModifierCores.PERESKIA, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.PERESKIA, 1)));
  public static Modifier BREED = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "rampant_breeding"), ModifierCores.WILDEWHEET, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.WILDEWHEET, 1)));
  public static Modifier FLOWERS = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "flower_spreading"), ModifierCores.WILDROOT, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.WILDROOT, 1)));
  public static Modifier VILLAGERS = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "false_night"), ModifierCores.MOONGLOW_LEAF, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.MOONGLOW_LEAF, 1)));
  public static Modifier RADIUS2 = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "rampant_growth_ii"), ModifierCores.SPIRIT_HERB, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.SPIRIT_HERB, 1)));
  public static Modifier EMBIGGEN = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "embiggening"), ModifierCores.BAFFLE_CAP, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.BAFFLE_CAP, 1)));
  public static Modifier RADIUS3 = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "arms_of_air"), ModifierCores.CLOUD_BERRY, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.CLOUD_BERRY, 1)));
  public static Modifier ANIMAL_GROWTH = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "incubation"), ModifierCores.INFERNAL_BULB, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.INFERNAL_BULB, 1)));
  public static Modifier ORE = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "ore_infusion"), ModifierCores.STALICRIPE, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.STALICRIPE, 1)));
  public static Modifier HYDRATE = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "hydration"), ModifierCores.DEWGONIA, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.DEWGONIA, 1)));

  static {
    // Conflicts
    FLOWERS.addConflicts(RADIUS1, RADIUS2, RADIUS3); // Targets specific flowers
    ORE.addConflicts(RADIUS1, RADIUS2, RADIUS3); // Can't AOE
    EMBIGGEN.addConflicts(RADIUS1, RADIUS2, RADIUS3); // Again can't aoe
  }

  public static ResourceLocation spellName = new ResourceLocation(Roots.MODID, "spell_growth_infusion");
  public static SpellGrowthInfusion instance = new SpellGrowthInfusion(spellName);

  private AxisAlignedBB breedingBox;
  private int radius_x, radius_y, radius_z, ticks, additional_count, count, radius_boost, radius_breed_x, radius_breed_y, radius_breed_z, animal_growth, animal_growth_vary, villager_growth, villager_growth_vary;
  private float embiggen_chance, stone_chance;
  private String stone_dict;

  public SpellGrowthInfusion(ResourceLocation name) {
    super(name, TextFormatting.YELLOW, 48f / 255f, 255f / 255f, 48f / 255f, 192f / 255f, 255f / 255f, 192f / 255f);
    properties.addProperties(PROP_COOLDOWN, PROP_CAST_TYPE, PROP_COST_1, PROP_RADIUS_X, PROP_RADIUS_Y, PROP_RADIUS_Z, PROP_TICKS, PROP_COUNT, PROP_ADDITIONAL_COUNT, PROP_RADIUS_BOOST, PROP_RADIUS_BREED_X, PROP_RADIUS_BREED_Y, PROP_RADIUS_BREED_Z, PROP_EMBIGGEN_CHANCE, PROP_ANIMAL_GROWTH, PROP_ANIMAL_GROWTH_VARY, PROP_STONE_CHANCE, PROP_STONE_DICT, PROP_VILLAGER_GROWTH, PROP_VILLAGER_GROWTH_VARY);
    acceptsModifiers(RADIUS1, BREED, FLOWERS, VILLAGERS, RADIUS2, EMBIGGEN, RADIUS3, ANIMAL_GROWTH, ORE, HYDRATE);
  }

  @Override
  public void init() {
    addIngredients(
        new OreIngredient("treeSapling"),
        new OreIngredient("treeSapling"),
        new ItemStack(ModItems.terra_moss),
        Ingredients.AUBERGINE,
        new OreIngredient("cropWheat")
    );
  }

  @Override
  public boolean cast(EntityPlayer player, StaffModifierInstanceList info, int ticks) {
    // TODO: HYDRATION
    boolean didSomething = false;
    if (info.has(BREED)) {
      List<EntityAnimal> animals = player.world.getEntitiesWithinAABB(EntityAnimal.class, breedingBox.offset(player.getPosition()));
      int count = 0;
      for (EntityAnimal animal : animals) {
        if (animal.isInLove() || animal.getGrowingAge() != 0) {
          continue;
        }
        count++;
        if (!player.world.isRemote) {
          animal.setInLove(player);
          // TODO: PARTICLE
        }
      }
      if (count != 0) {
        didSomething = true;
      }
    }

    boolean aoe = false;
    if (info.has(RADIUS1) || info.has(RADIUS2)) {
      aoe = true;
    }
    int boost = 0;
    if (aoe && (info.has(RADIUS1) || info.has(RADIUS2))) {
      boost = radius_boost;
    }
    if (aoe) {
      List<BlockPos> positions = Growth.collect(player.world, player.getPosition(), radius_x + boost, radius_y + boost, radius_z + boost);
      if (positions.isEmpty()) return false;
      if (!player.world.isRemote) {
        for (int i = 0; i < ampInt(count) + player.world.rand.nextInt((ampSubInt(additional_count))); i++) {
          BlockPos pos = positions.get(player.world.rand.nextInt(positions.size()));
          IBlockState state = player.world.getBlockState(pos);
          for (int j = 0; j < ticks; j++) {
            state.getBlock().randomTick(player.world, pos, state, Util.rand);
          }
          IBlockState below = player.world.getBlockState(pos.down());
          if (below.getPropertyKeys().contains(BlockFarmland.MOISTURE)) {
            player.world.setBlockState(pos.down(), below.withProperty(BlockFarmland.MOISTURE, 7), 2 | 16);
          }
          // TODO: CENTRALISE EFFECT COLOURS
          if (player.world.rand.nextInt(3) == 0) {
            PacketHandler.sendToAllTracking(new MessageRampantLifeInfusionFX(pos.getX(), pos.getY(), pos.getZ()), player);
          }
        }
      }
      didSomething = true;
    } else {
      RayCastUtil.RayTraceAndEntityResult entityResult = RayCastUtil.rayTraceMouseOver(player, 8.0d);
      Entity resultEntity = entityResult.getPointedEntity();
      if (resultEntity != null) {
        if (info.has(ANIMAL_GROWTH) && resultEntity instanceof EntityAgeable) {
          EntityAgeable ageable = (EntityAgeable) resultEntity;
          // TODO: CONSIDER ADDING HOSTILE CHECK
          if (ageable.isChild()) {
            didSomething = true;
            if (!player.world.isRemote) {
              int amount = animal_growth + Util.rand.nextInt(animal_growth_vary);
              ageable.addGrowth(amount);
              // TODO: PARTICLES
            }
          }
        }
        if (info.has(VILLAGERS) && resultEntity instanceof EntityVillager) {
          // TODO public net.minecraft.entity.passive.EntityVillager field_70961_j # timeUntilReset
          EntityVillager villager = (EntityVillager) resultEntity;
          if (!villager.isChild()) {
            didSomething = true;
            if (!player.world.isRemote) {
              villager.timeUntilReset = Math.min(0, villager.timeUntilReset - villager_growth + Util.rand.nextInt(villager_growth_vary));
              // TODO: Particles
            }
          }
        }
      }

      RayTraceResult result = RayCastUtil.rayTraceBlocksSight(player.world, player, 8.0f);
      if (result != null) {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
          BlockPos pos = result.getBlockPos();
          IBlockState state = player.world.getBlockState(pos);
          Block block = state.getBlock();

          // Test for flower
          if (info.has(FLOWERS)) {
            FlowerRecipe recipe = ModRecipes.getFlowerRecipe(state);
            if (recipe != null) {
              didSomething = true;
              if (!player.world.isRemote) {
                // TODO: PARTICLE
                ItemUtil.spawnItem(player.world, pos, recipe.getStack());
              }
            }
          }

          // Test for mushroom or tree
          if (info.has(EMBIGGEN)) {
            if (block instanceof BlockMushroom) {
              didSomething = true;
              if (!player.world.isRemote && Util.rand.nextFloat() < embiggen_chance) {
                BlockMushroom shroom = (BlockMushroom) block;

                if (shroom.generateBigMushroom(player.world, pos, state, Util.rand)) {
                  // TODO: Particles/sound?
                }
              }
            } else if (block instanceof BlockSapling) {
              didSomething = true;
              if (!player.world.isRemote && Util.rand.nextFloat() < embiggen_chance) {
                BlockSapling sapling = (BlockSapling) block;
                sapling.generateTree(player.world, pos, state, Util.rand);
              }
            }
          }

          // TODO: Stone -> ore conversion
          if (info.has(ORE)) {
            if (OreDictCache.matches(this.stone_dict, state) && Util.rand.nextFloat() < this.stone_chance) {
              didSomething = true;
              if (!player.world.isRemote) {
                player.world.setBlockState(pos, OreChances.getRandomState());
              }
            }
          }

          // Test for growth last
          if (!didSomething && Growth.canGrow(player.world, pos, state)) {
            if (!player.world.isRemote) {
              for (int i = 0; i < ampInt(ticks); i++) {
                state.getBlock().randomTick(player.world, pos, state, new Random());
              }
              IBlockState below = player.world.getBlockState(pos.down());
              if (below.getPropertyKeys().contains(BlockFarmland.MOISTURE)) {
                player.world.setBlockState(pos.down(), below.withProperty(BlockFarmland.MOISTURE, 7), 2 | 16);
              }
              PacketHandler.sendToAllTracking(new MessageLifeInfusionFX(pos.getX(), pos.getY(), pos.getZ()), player);
              didSomething = true;
            }
          }
        }
      }
    }
    return didSomething;
  }

  @Override
  public void doFinalise() {
    this.castType = properties.get(PROP_CAST_TYPE);
    this.cooldown = properties.get(PROP_COOLDOWN);
    this.radius_x = properties.get(PROP_RADIUS_X);
    this.radius_y = properties.get(PROP_RADIUS_Y);
    this.radius_z = properties.get(PROP_RADIUS_Z);
    this.radius_breed_x = properties.get(PROP_RADIUS_BREED_X);
    this.radius_breed_y = properties.get(PROP_RADIUS_BREED_Y);
    this.radius_breed_z = properties.get(PROP_RADIUS_BREED_Z);
    this.ticks = properties.get(PROP_TICKS);
    this.count = properties.get(PROP_COUNT);
    this.additional_count = properties.get(PROP_ADDITIONAL_COUNT);
    this.radius_boost = properties.get(PROP_RADIUS_BOOST);
    this.breedingBox = new AxisAlignedBB(-this.radius_breed_x, -this.radius_breed_y, -this.radius_breed_z, this.radius_breed_x + 1, this.radius_breed_y + 1, this.radius_breed_z + 1);
    this.embiggen_chance = properties.get(PROP_EMBIGGEN_CHANCE);
    this.animal_growth = properties.get(PROP_ANIMAL_GROWTH);
    this.animal_growth_vary = properties.get(PROP_ANIMAL_GROWTH_VARY);
    this.villager_growth = properties.get(PROP_VILLAGER_GROWTH);
    this.villager_growth_vary = properties.get(PROP_VILLAGER_GROWTH_VARY);
    this.stone_chance = properties.get(PROP_STONE_CHANCE);
    this.stone_dict = properties.get(PROP_STONE_DICT);
  }
}
