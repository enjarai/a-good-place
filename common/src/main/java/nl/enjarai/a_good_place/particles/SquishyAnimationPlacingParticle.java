package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SquishyAnimationPlacingParticle extends PlacingBlockParticle{
    public SquishyAnimationPlacingParticle(ClientLevel world, BlockPos blockPos, Direction face) {
        super(world, blockPos, face);
    }

    @Override
    public void applyAnimation(PoseStack poseStack, float partialTicks) {

    }
}
