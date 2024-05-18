package nl.enjarai.wonkyblock;

import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nl.enjarai.wonkyblock.particle.WonkyBlocksManager;


@Mod(AGoodPlaceForge.MODID)
public class AGoodPlaceForge {
    public static final String MODID = "a_good_place";

    public AGoodPlaceForge() {
        MinecraftForge.EVENT_BUS.register(this);
        //clear on level change
    }

    @SubscribeEvent
    public void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            WonkyBlocksManager.renderParticles(event.getPoseStack(), event.getPartialTick());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.LevelTickEvent tickEvent) {
        if (tickEvent.phase == TickEvent.Phase.END) {
            WonkyBlocksManager.tickParticles();
        }
    }
}
