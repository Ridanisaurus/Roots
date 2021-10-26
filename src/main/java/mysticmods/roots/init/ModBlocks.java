package mysticmods.roots.init;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import mysticmods.roots.RootsTags;
import mysticmods.roots.blocks.*;
import mysticmods.roots.blocks.crops.ElementalCropBlock;
import mysticmods.roots.blocks.crops.ThreeStageCropBlock;
import mysticmods.roots.blocks.crops.WaterElementalCropBlock;
import net.minecraft.block.*;
import net.minecraft.tags.BlockTags;
import noobanidus.libs.noobutil.block.BaseBlocks;
import noobanidus.libs.noobutil.data.BlockstateGenerator;

import static mysticmods.roots.Roots.REGISTRATE;
import static noobanidus.libs.noobutil.block.BaseBlocks.SeededCropsBlock;

public class ModBlocks {
  public static class Decoration {
    public static class RunedObsidian {
      public static NonNullUnaryOperator<AbstractBlock.Properties> RUNED_PROPERTIES = r -> AbstractBlock.Properties.copy(Blocks.OBSIDIAN);

      public static BlockEntry<RunedObsidianBlocks.Block> RUNED_OBSIDIAN = REGISTRATE.block("runed_obsidian", RunedObsidianBlocks.Block::new)
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();

      public static BlockEntry<RunedObsidianBlocks.Button> RUNED_BUTTON = REGISTRATE.block("runed_button", RunedObsidianBlocks.Button::new)
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();

      public static BlockEntry<RunedObsidianBlocks.PressurePlate> RUNED_PRESSURE_PLATE = REGISTRATE.block("runed_pressure_plate", (p) -> new RunedObsidianBlocks.PressurePlate(PressurePlateBlock.Sensitivity.MOBS, p))
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();

      public static BlockEntry<RunedObsidianBlocks.Slab> RUNED_SLAB = REGISTRATE.block("runed_slab", RunedObsidianBlocks.Slab::new)
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();

      public static BlockEntry<RunedObsidianBlocks.Stairs> RUNED_STAIRS = REGISTRATE.block("runed_stairs", RunedObsidianBlocks.Stairs::new)
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();

      public static BlockEntry<RunedObsidianBlocks.Wall> RUNED_WALL = REGISTRATE.block("runed_wall", RunedObsidianBlocks.Wall::new)
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();

      public static BlockEntry<RunedObsidianBlocks.NarrowPost> RUNED_NARROW_POST = REGISTRATE.block("runed_narrow_post", RunedObsidianBlocks.NarrowPost::new)
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();

      public static BlockEntry<RunedObsidianBlocks.WidePost> RUNED_WIDE_POST = REGISTRATE.block("runed_wide_post", RunedObsidianBlocks.WidePost::new)
          .properties(RUNED_PROPERTIES)
          .tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
          .register();
    }
  }

  public static class Crops {
    public static BlockEntry<ThreeStageCropBlock> WILDROOT_CROP = REGISTRATE.block("wildroot_crop", (p) -> new ThreeStageCropBlock(p, ModItems.Herbs.WILDROOT))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::cropBlockstate)
        .tag(RootsTags.Blocks.WILDROOT_CROP)
        .register();

    public static BlockEntry<ElementalCropBlock> CLOUD_BERRY_CROP = REGISTRATE.block("cloud_berry_crop", (p) -> new ElementalCropBlock(p, ModItems.Herbs.CLOUD_BERRY))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::cropBlockstate)
        .tag(RootsTags.Blocks.CLOUD_BERRY_CROP)
        .register();

    public static BlockEntry<WaterElementalCropBlock> DEWGONIA_CROP = REGISTRATE.block("dewgonia_crop", (p) -> new WaterElementalCropBlock(p, ModItems.Herbs.DEWGONIA))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::cropBlockstate)
        .tag(RootsTags.Blocks.DEWGONIA_CROP)
        .register();

    public static BlockEntry<ElementalCropBlock> INFERNAL_BULB_CROP = REGISTRATE.block("infernal_bulb_crop", (p) -> new ElementalCropBlock(p, ModItems.Herbs.INFERNAL_BULB))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::crossBlockstate)
        .tag(RootsTags.Blocks.INFERNAL_BULB_CROP)
        .register();

    public static BlockEntry<ElementalCropBlock> STALICRIPE_CROP = REGISTRATE.block("stalicripe_crop", (p) -> new ElementalCropBlock(p, ModItems.Herbs.STALICRIPE))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::cropBlockstate)
        .tag(RootsTags.Blocks.STALICRIPE_CROP)
        .register();

    public static BlockEntry<SeededCropsBlock> MOONGLOW_LEAF_CROP = REGISTRATE.block("moonglow_leaf_crop", (p) -> new SeededCropsBlock(p, ModItems.Seeds.MOONGLOW_LEAF_SEEDS))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::cropBlockstate)
        .tag(RootsTags.Blocks.MOONGLOW_LEAF_CROP)
        .register();
    public static BlockEntry<SeededCropsBlock> PERESKIA_CROP = REGISTRATE.block("pereskia_crop", (p) -> new SeededCropsBlock(p, ModItems.Seeds.PERESKIA_BULB))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::crossBlockstate)
        .tag(RootsTags.Blocks.PERESKIA_CROP)
        .register();
    public static BlockEntry<ThreeStageCropBlock> SPIRIT_HERB_CROP = REGISTRATE.block("spirit_herb_crop", (p) -> new ThreeStageCropBlock(p, ModItems.Seeds.SPIRIT_HERB_SEEDS))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::cropBlockstate)
        .tag(RootsTags.Blocks.SPIRIT_HERB_CROP)
        .register();
    public static BlockEntry<SeededCropsBlock> WILDEWHEET_CROP = REGISTRATE.block("wildewheet_crop", (p) -> new SeededCropsBlock(p, ModItems.Seeds.WILDEWHEET_SEEDS))
        .properties(o -> AbstractBlock.Properties.copy(Blocks.WHEAT))
        .blockstate(BlockstateGenerator::cropBlockstate)
        .tag(RootsTags.Blocks.WILDEWHEET_CROP)
        .register();
  }

  public static BlockEntry<FeyLightBlock> FEY_LIGHT = REGISTRATE.block("fey_light", FeyLightBlock::new)
      .properties(o -> AbstractBlock.Properties.copy(Blocks.TORCH))
      .register();
  public static BlockEntry<CatalystPlateBlock> CATALYST_PLATE = REGISTRATE.block("catalyst_plate", CatalystPlateBlock::new).register();
  public static BlockEntry<ElementalSoilBlock> ELEMENTAL_SOIL = REGISTRATE.block("elemental_soil", ElementalSoilBlock::new).register();
  public static BlockEntry<FeyCrafterBlock> FEY_CRAFTER = REGISTRATE.block("fey_crafter", FeyCrafterBlock::new).register();
  public static BlockEntry<ImbuerBlock> IMBUER = REGISTRATE.block("imbuer", ImbuerBlock::new).register();
  public static BlockEntry<ImposerBlock> IMPOSER = REGISTRATE.block("imposer", ImposerBlock::new).register();
  public static BlockEntry<IncensePlateBlock> INCENSE_BURNER = REGISTRATE.block("incense_burner", IncensePlateBlock::new).register();
  public static BlockEntry<MortarBlock> MORTAR = REGISTRATE.block("mortar", MortarBlock::new).register();
  public static BlockEntry<PyreBlock> PYRE = REGISTRATE.block("pyre", PyreBlock::new).register();

  public static void load() {
  }
}
