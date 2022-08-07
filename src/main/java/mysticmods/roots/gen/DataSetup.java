package mysticmods.roots.gen;

import mysticmods.roots.api.RootsAPI;
import mysticmods.roots.network.*;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = RootsAPI.MODID)
public class DataSetup {
  @SubscribeEvent
  public static void onReloadListeners(AddReloadListenerEvent event) {
    event.addListener(RitualPropertyReloadListener.getInstance());
    event.addListener(SpellPropertyReloadListener.getInstance());
    event.addListener(ModifierPropertyReloadListener.getInstance());
    event.addListener(SpellCostReloadListener.getInstance());
  }

  @SubscribeEvent
  public static void onDataReloaded(OnDatapackSyncEvent event) {
    if (event.getPlayer() != null) {
      Networking.sendTo(new ClientBoundRitualPropertyPacket(), event.getPlayer());
      Networking.sendTo(new ClientBoundSpellPropertyPacket(), event.getPlayer());
      Networking.sendTo(new ClientBoundModifierPropertyPacket(), event.getPlayer());
      Networking.sendTo(new ClientBoundSpellCostsPacket(), event.getPlayer());
    } else {
      Networking.send(PacketDistributor.ALL.noArg(), new ClientBoundRitualPropertyPacket());
      Networking.send(PacketDistributor.ALL.noArg(), new ClientBoundSpellPropertyPacket());
      Networking.send(PacketDistributor.ALL.noArg(), new ClientBoundModifierPropertyPacket());
      Networking.send(PacketDistributor.ALL.noArg(), new ClientBoundSpellCostsPacket());
    }
  }
}
