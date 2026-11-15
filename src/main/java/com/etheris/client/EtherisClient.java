package com.etheris.client;
import com.etheris.machine.MachineMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = "etheris", dist = Dist.CLIENT)
public final class EtherisClient {
    public EtherisClient(IEventBus bus) { bus.addListener(EtherisClient::screens); }
    private static void screens(RegisterMenuScreensEvent event) { event.register(MachineMenu.TYPE.get(), MachineScreen::new); }
}
