package epicsquid.roots.item.itemblock;

import epicsquid.mysticallib.network.PacketHandler;
import epicsquid.roots.Roots;
import epicsquid.roots.config.GeneralConfig;
import epicsquid.roots.init.ModBlocks;
import epicsquid.roots.network.fx.ElementalSoilTransformFX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;

public class ItemBlockElementalSoil extends ItemBlock {

  public ItemBlockElementalSoil(Block block) {
    super(block);
  }

  @Override
  public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {

      Block oldblock = world.getBlockState(pos.offset(side.getOpposite())).getBlock();

      if (oldblock instanceof BlockFarmland && player.inventory.addItemStackToInventory(new ItemStack(oldblock))) {
        pos = pos.offset(side.getOpposite());
      }
    return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
  }

  @Override
  public boolean onEntityItemUpdate(EntityItem entityItem) {

    if (block == ModBlocks.elemental_soil)
    {
      World world = entityItem.world;
      int count = entityItem.getItem().getCount();

      if (!world.isRemote && entityItem.isInLava())
      {
        world.spawnEntity(new EntityItem(world, entityItem.posX, entityItem.posY + 0.5, entityItem.posZ,
                new ItemStack(ModBlocks.elemental_soil_fire, count)));
        PacketHandler.sendToAllTracking(new ElementalSoilTransformFX(entityItem.posX, entityItem.posY, entityItem.posZ, 0), entityItem);
        entityItem.setDead();
        return true;
      }

      if (!world.isRemote && entityItem.ticksExisted >= 40)
      {
        if (entityItem.isInWater()) {
          world.spawnEntity(new EntityItem(world, entityItem.posX, entityItem.posY, entityItem.posZ,
                  new ItemStack(ModBlocks.elemental_soil_water, count)));
          PacketHandler.sendToAllTracking(new ElementalSoilTransformFX(entityItem.posX, entityItem.posY, entityItem.posZ, 1), entityItem);
          entityItem.setDead();
          return true;
        }
        if (entityItem.posY <= GeneralConfig.EarthSoilMinY) {
          BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(entityItem.getPosition());
          boolean found_roof = false;
          for (int i = pos.getY(); i < Math.min(GeneralConfig.EarthSoilMinY + 20, world.getChunk(pos).getHeight(pos)); i++) {
            pos.setY(i);
            if (world.isAirBlock(pos)) continue;
            found_roof = true;
          }
          if (!found_roof) return super.onEntityItemUpdate(entityItem);

          world.spawnEntity(new EntityItem(world, entityItem.posX, entityItem.posY, entityItem.posZ,
                  new ItemStack(ModBlocks.elemental_soil_earth, count)));
          PacketHandler.sendToAllTracking(new ElementalSoilTransformFX(entityItem.posX, entityItem.posY, entityItem.posZ, 3), entityItem);
          entityItem.setDead();
          return true;
        }
        if (entityItem.posY >= GeneralConfig.AirSoilMinY) {
          BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(entityItem.getPosition());
          int height = world.getChunk(pos).getHeight(pos);
          if (pos.getY() < height) return super.onEntityItemUpdate(entityItem);

          world.spawnEntity(new EntityItem(world, entityItem.posX, entityItem.posY, entityItem.posZ,
                  new ItemStack(ModBlocks.elemental_soil_air, count)));
          PacketHandler.sendToAllTracking(new ElementalSoilTransformFX(entityItem.posX, entityItem.posY, entityItem.posZ, 2), entityItem);
          entityItem.setDead();
          return true;
        }
      }
    }
    return super.onEntityItemUpdate(entityItem);
  }
}
