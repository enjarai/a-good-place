package nl.enjarai.wonkyblock.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.wonkyblock.particle.PlacingBlockParticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// static class so we have 1 less method call for get instance as isHidden will be called a lot
public class WonkyBlocksManager {

    private static final List<PlacingBlockParticle> PARTICLES = new ArrayList<>();
    //replace each time so its thread safe. Supposedly faster than a concurrent set
    private static Set<BlockPos> hiddenBlocks = Set.of();


    public static void addParticle(BlockPos pos, Level level) {
        PARTICLES.add(new PlacingBlockParticle((ClientLevel) level, pos));
        hideBlock(pos);
    }

    public static void hideBlock(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<>(hiddenBlocks);
        var success = list.add(pos);
        if (success) {
            hiddenBlocks = Set.of(list.toArray(new BlockPos[0]));
        }
    }

    public static boolean isBlockHidden(BlockPos pos) {
        return hiddenBlocks.contains(pos);
    }

    public static void unHideBlock(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<>(hiddenBlocks);
        var success = list.remove(pos);
        if (success) {
            hiddenBlocks = Set.of(list.toArray(new BlockPos[0]));
            markBlockForRender(pos);
        }
    }

    private static void markBlockForRender(BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockState state = level.getBlockState(pos);
        //this just calls set block dirty which calls set section dirty for neighbors
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }


    public static void tickParticles() {
        PARTICLES.forEach(PlacingBlockParticle::tick);
    }

    public static void renderParticles(PoseStack poseStack, float tickDelta) {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        for (var p : PARTICLES) {
            p.render(null, camera, tickDelta);
        }
    }


}
