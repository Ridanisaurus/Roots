package epicsquid.roots.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import epicsquid.mysticallib.util.ConfigUtil;
import epicsquid.roots.Roots;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Config.LangKey("config.roots.category.moss")
@Config(modid = Roots.MODID, name = "roots/moss", category = "moss")
@SuppressWarnings("unused")
public class MossConfig {
  @Config.Comment(("List of dimension IDs where terra moss harvesting shouldn't work"))
  public static String[] BlacklistDimensions = new String[]{};

  @Config.Ignore
  public static List<Integer> blacklistDimensions = null;

  public static List<Integer> getBlacklistDimensions() {
    if (blacklistDimensions == null) {
      blacklistDimensions = new ArrayList<>();
      for (String dim : BlacklistDimensions) {
        blacklistDimensions.add(Integer.parseInt(dim));
      }
    }
    return blacklistDimensions;
  }

  @Config.Comment(("List of mod:item:meta,mod:item:meta (meta optional) of mossy blocks and what to convert them into when scraping with knives [note that logs or blocks with positional data are unsuited for this purpose]"))
  public static String[] MossyCobblestones = new String[]{"minecraft:mossy_cobblestone,minecraft:cobblestone", "minecraft:stonebrick:1,minecraft:stonebrick", "minecraft:monster_egg:3,minecraft:monster_egg:2"};

  @Config.Ignore
  private static Map<ItemStack, ItemStack> mossyCobblestones = null;

  @Config.Ignore
  private static BiMap<Block, Block> mossyBlocks = HashBiMap.create();

  @Config.Ignore
  private static BiMap<BlockState, BlockState> mossyStates = HashBiMap.create();

  @SuppressWarnings("deprecation")
  public static Map<ItemStack, ItemStack> getMossyCobblestones() {
    if (mossyCobblestones == null) {
      mossyCobblestones = ConfigUtil.parseMap(new HashMap<>(), ConfigUtil::parseItemStack, ConfigUtil::parseItemStack, ",", MossyCobblestones);

      mossyBlocks.clear();
      mossyStates.clear();
      for (Map.Entry<ItemStack, ItemStack> entry : mossyCobblestones.entrySet()) {
        ItemStack in = entry.getKey();
        ItemStack out = entry.getValue();
        if (in.getItem() instanceof BlockItem && out.getItem() instanceof BlockItem) {
          Block blockIn = ((BlockItem) in.getItem()).getBlock();
          Block blockOut = ((BlockItem) out.getItem()).getBlock();
          if (in.getMetadata() != 0 || out.getMetadata() != 0) {
            BlockState stateIn = blockIn.getStateFromMeta(in.getMetadata());
            BlockState stateOut = blockOut.getStateFromMeta(out.getMetadata());
            mossyStates.put(stateIn, stateOut);
          } else {
            mossyBlocks.put(blockIn, blockOut);
          }
        }
      }
    }

    return mossyCobblestones;
  }

  @Nullable
  public static BlockState scrapeResult(BlockState state) {
    Map<ItemStack, ItemStack> mossy = getMossyCobblestones();

    if (mossyStates.containsKey(state)) {
      return mossyStates.get(state);
    }

    if (mossyBlocks.containsKey(state.getBlock())) {
      return mossyBlocks.get(state.getBlock()).getDefaultState();
    }

    return null;
  }

  @Nullable
  public static BlockState mossConversion(BlockState state) {
    Map<ItemStack, ItemStack> mossy = getMossyCobblestones();

    if (mossyStates.inverse().containsKey(state)) {
      return mossyStates.inverse().get(state);
    }

    if (mossyBlocks.inverse().containsKey(state.getBlock())) {
      return mossyBlocks.inverse().get(state.getBlock()).getDefaultState();
    }

    return null;
  }
}
