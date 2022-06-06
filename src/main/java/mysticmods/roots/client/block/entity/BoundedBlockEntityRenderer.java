package mysticmods.roots.client.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mysticmods.roots.api.BoundedBlockEntity;
import mysticmods.roots.client.Model3D;
import mysticmods.roots.client.RenderResizableCuboid;
import mysticmods.roots.client.RootsRenderer;
import mysticmods.roots.util.EnumUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BoundedBlockEntityRenderer<T extends BlockEntity & BoundedBlockEntity> implements BlockEntityRenderer<T> {
  protected final BlockEntityRendererProvider.Context context;

  private Model3D model = null;
  // TODO: dynamic bounds?
  private AABB bounds = null;

  private static final int[] colors = new int[EnumUtils.DIRECTIONS.length];

  static {
    colors[Direction.DOWN.ordinal()] = RootsRenderer.getColorARGB(255, 255, 255, 0.62F);
    colors[Direction.UP.ordinal()] = RootsRenderer.getColorARGB(255, 255, 255, 0.62F);
    colors[Direction.NORTH.ordinal()] = RootsRenderer.getColorARGB(255, 255, 255, 0.6F);
    colors[Direction.SOUTH.ordinal()] = RootsRenderer.getColorARGB(255, 255, 255, 0.6F);
    colors[Direction.WEST.ordinal()] = RootsRenderer.getColorARGB(255, 255, 255, 0.58F);
    colors[Direction.EAST.ordinal()] = RootsRenderer.getColorARGB(255, 255, 255, 0.58F);
  }

  public BoundedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.context = context;
  }

  @Override
  public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
    if (bounds == null) {
      BoundingBox box = pBlockEntity.getBoundingBox();
      if (box != null) {
        bounds = AABB.of(box);
      }
    }
    if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
      //  TODO: Check for bounds being shown
      if (model == null) {
        model = new Model3D();
        model.setTexture(RootsRenderer.whiteIcon);
        model.minX = 0;
        model.minY = 0;
        model.minZ = 0;
        model.maxX = 1;
        model.maxY = 1;
        model.maxZ = 1;
      }

      pPoseStack.pushPose();
      //Adjust translation and scale ever so slightly so that no z-fighting happens at the edges if there are blocks there
      pPoseStack.translate(-pBlockEntity.getRadiusX() + 0.01, -pBlockEntity.getRadiusY() + 0.01, -pBlockEntity.getRadiusZ() + 0.01);
      // TODO: ???
      float diameter = (pBlockEntity.getRadiusX() + pBlockEntity.getRadiusZ() + 1) - 0.02f;
      pPoseStack.scale(diameter, diameter, diameter);
      //If we are inside the visualization we don't have to render the "front" face, otherwise we need to render both given how the visualization works
      // we want to be able to see all faces easily
      RenderResizableCuboid.FaceDisplay faceDisplay = isInsideBounds(pBlockEntity.getBlockPos().getX() - pBlockEntity.getRadiusX(), pBlockEntity.getBlockPos().getY() - pBlockEntity.getRadiusY(), pBlockEntity.getBlockPos().getZ() - pBlockEntity.getRadiusZ(), pBlockEntity.getBlockPos().getX() + pBlockEntity.getRadiusX() + 1, pBlockEntity.getBlockPos().getY() + pBlockEntity.getRadiusY() + 1, pBlockEntity.getBlockPos().getZ() + pBlockEntity.getRadiusZ() + 1) ? RenderResizableCuboid.FaceDisplay.BACK : RenderResizableCuboid.FaceDisplay.BOTH;
      RootsRenderer.renderObject(model, pPoseStack, pBufferSource.getBuffer(Sheets.translucentCullBlockSheet()), colors, RootsRenderer.FULL_LIGHT, pPackedOverlay, faceDisplay);
      pPoseStack.popPose();
    }
  }

  protected boolean isInsideBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    Vec3 projectedView = context.getBlockEntityRenderDispatcher().camera.getPosition();
    return minX <= projectedView.x && projectedView.x <= maxX && minY <= projectedView.y && projectedView.y <= maxY && minZ <= projectedView.z && projectedView.z <= maxZ;
  }

  @Override
  public int getViewDistance () {
    return 64*4;
  }

  @Override
  public boolean shouldRenderOffScreen(T pBlockEntity) {
    return true;
  }
}
