package nl.enjarai.wonkyblock.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import nl.enjarai.wonkyblock.WonkyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.*;

import java.util.Random;

public class PlacingBlockParticle extends Particle {
    private static final Random RANDOM = new Random();
    private static final net.minecraft.util.math.random.Random MC_RANDOM = net.minecraft.util.math.random.Random.create();

    public BlockPos pos;

    Block block;
    BlockState blockState;

    BlockModelRenderer modelRenderer;

    BakedModel model;

    MinecraftClient client;

    Direction facing;

    Vec3d prevRot;
    Vec3d rot;

    float startingHeight;
    float startingAngle;
    float step = 0.00275f;

    float height;
    float prevHeight;

    float smoothHeight;

    boolean lookingUp;
    long tick = -1;
    boolean inPosition = false;

    BlockEntity tileEntity;

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
            return new PlacingBlockParticle(
                    world, x, y, z,
                    world.getBlockState(new BlockPos(x, y, z))
            );
        }
    }

    public PlacingBlockParticle(ClientWorld world, double x, double y, double z, BlockState state) {
        super(world, x, y, z);

        pos = new BlockPos(x, y, z);

        client = MinecraftClient.getInstance();

        assert client.player != null;
        facing = client.player.getHorizontalFacing();

        lookingUp = MathHelper.wrapDegrees(client.player.getPitch()) <= 0;

        prevHeight = height = startingHeight = (float) RANDOM.nextDouble(0.065, 0.115);
        startingAngle = (float) RANDOM.nextDouble(0.03125, 0.0635);

        prevRot = new Vec3d(0, 0, 0);

        rot = switch (facing) {
            case EAST -> new Vec3d(-startingAngle, 0, -startingAngle);
            case NORTH -> new Vec3d(-startingAngle, 0, startingAngle);
            case SOUTH -> new Vec3d(startingAngle, 0, -startingAngle);
            case WEST -> new Vec3d(startingAngle, 0, startingAngle);
            default -> new Vec3d(0, 0, 0);
        };

        block = (blockState = state).getBlock();

        modelRenderer = client.getBlockRenderManager().getModelRenderer();

        collidesWithWorld = false;

        model = client.getBlockRenderManager().getModels().getModel(state);

        if (model == null) {
            collidesWithWorld = true;
            dead = true;
        }

        tileEntity = world.getBlockEntity(pos);
    }

    @Override
    public void tick() {
        if (++age >= 10 || inPosition) {
            if (WonkyBlock.isBlockInvisible(pos)) {
                WonkyBlock.removeInvisibleBlock(pos);
            }
        }
        if (age >= 11) {
            killParticle();
        }

//        if (!collidesWithWorld) {
//            var s = world.getBlockState(pos);
//
//            if (s.getBlock() != FBP.FBPBlock || s.getBlock() == block) {
//                if (blockSet && s.getBlock() == Blocks.AIR) {
//                    // the block was destroyed during the animation
//                    killParticle();
//
//                    FBP.FBPBlock.onBlockDestroyedByPlayer(client.world, pos, s);
//                    client.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
//                    return;
//                }
//
//                FBP.FBPBlock.copyState(client.world, pos, blockState, this);
//                client.world.setBlockState(pos, FBP.FBPBlock.getDefaultState(), 2);
//
//                Chunk c = world.getChunk(pos);
//                c.resetRelightChecks();
//                c.setLightPopulated(true);
//
//                FBPRenderUtil.markBlockForRender(pos);
//
//                blockSet = true;
//            }
//
//            spawned = true;
//        }

        if (dead || client.isPaused())
            return;

        prevHeight = height;

        prevRot = rot;

        rot = switch (facing) {
            case EAST -> rot.add(step, 0, step);
            case NORTH -> rot.add(step, 0, -step);
            case SOUTH -> rot.add(-step, 0, step);
            case WEST -> rot.add(-step, 0, -step);
            default -> new Vec3d(0, 0, 0);
        };

        height -= step * 5f;

        step *= 1.5678982f;
    }

    @SuppressWarnings({"SuspiciousNameCombination"})
    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float delta) {
        if (dead)
            return;

        if (collidesWithWorld) {
            if (tick >= 1) {
                killParticle();
                return;
            }

            tick++;
        }

        float deltaX = (float) (prevPosX + (x - prevPosX) * delta - velocityX) - 0.5f;
        float deltaY = (float) (prevPosY + (y - prevPosY) * delta - velocityY) - 0.5f;
        float deltaZ = (float) (prevPosZ + (z - prevPosZ) * delta - velocityZ) - 0.5f;

        smoothHeight = ((float) (prevHeight + (height - prevHeight) * (double) delta));

        if (smoothHeight <= 0)
            smoothHeight = 0;

        var tRot = switch (facing) {
            case EAST -> new Vec3d(1, 0, -1);
            case NORTH -> new Vec3d(-1, 0, -1);
            case SOUTH -> new Vec3d(1, 0, 1);
            case WEST -> new Vec3d(-1, 0, 1);
            default -> new Vec3d(0, 0, 0);
        };

        var t = switch (facing) {
            case EAST -> new Vec3d(-smoothHeight, smoothHeight, smoothHeight);
            case NORTH -> new Vec3d(smoothHeight, smoothHeight, smoothHeight);
            case SOUTH -> new Vec3d(-smoothHeight, smoothHeight, -smoothHeight);
            case WEST -> new Vec3d(smoothHeight, smoothHeight, -smoothHeight);
            default -> new Vec3d(0, 0, 0);
        };

        var smoothRot = prevRot.lerp(rot, delta);
        switch (facing) {
            case EAST -> {
                if (smoothRot.z > 0) {
                    inPosition = true;
                    smoothRot = new Vec3d(0, smoothRot.getY(), 0);
                }
            }
            case NORTH -> {
                if (smoothRot.z < 0) {
                    inPosition = true;
                    smoothRot = new Vec3d(0, smoothRot.getY(), 0);
                }
            }
            case SOUTH -> {
                if (smoothRot.x < 0) {
                    inPosition = true;
                    smoothRot = new Vec3d(0, smoothRot.getY(), 0);
                }
            }
            case WEST -> {
                if (smoothRot.z < 0) {
                    inPosition = true;
                    smoothRot = new Vec3d(0, smoothRot.getY(), 0);
                }
            }
        }

//        if (FBP.spawnPlaceParticles && canCollide && tick == 0) {
//            if ((!(FBP.frozen && !FBP.spawnWhileFrozen)
//                    && (FBP.spawnRedstoneBlockParticles || block != Blocks.REDSTONE_BLOCK))
//                    && client.gameSettings.particleSetting < 2) {
////                spawnParticles();
//            }
//        }

//        var buff = (BufferBuilder) vertexConsumer;
        var renderLayer = RenderLayers.getMovingBlockLayer(blockState);
        var blockVertexConsumer =
                client.getBufferBuilders().getEntityVertexConsumers().getBuffer(renderLayer);
        var matrices = new MatrixStack();

//        matrices.multiply(camera.getRotation());
        var cameraPos = camera.getPos();
        matrices.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
//        matrices.multiplyPositionMatrix(MixinHooks.particleMatrixStack.peek().getPositionMatrix());
//        var matrices = MixinHooks.particleMatrixStack;

        matrices.push();
//        matrices.translate(-pos.getX(), -pos.getY(), -pos.getZ());
//        buff.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
//
//        Tessellator.getInstance().draw();
//        RenderSystem.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
//        blockVertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

//        RenderSystem.pushMatrix();
//        matrices.push();

        RenderSystem.enableCull();
//        RenderSystem.enableColorMaterial();
//        GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);

        matrices.translate(0.5, 0.5, 0.5);
        matrices.translate(deltaX, deltaY, deltaZ);

        matrices.translate(tRot.x, tRot.y, tRot.z);
//
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) smoothRot.x));
        matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion((float) smoothRot.z));
//
        matrices.translate(-tRot.x, -tRot.y, -tRot.z);
        matrices.translate(t.x, t.y, t.z);

        modelRenderer.render(world, model, blockState, pos, matrices, blockVertexConsumer, false, MC_RANDOM, blockState.getRenderingSeed(pos), OverlayTexture.DEFAULT_UV);
//        if (FBP.animSmoothLighting)
//            modelRenderer.renderSmooth(client.world, model, blockState, pos, buff, false, textureSeed);
//        else
//            modelRenderer.renderFlat(client.world, model, blockState, pos, buff, false, textureSeed);

//        buff.setTranslation(0, 0, 0);

//        Tessellator.getInstance().draw();
//        GlStateManager.popMatrix();
//        matrices.pop();
        matrices.pop();

        client.getBufferBuilders().getEntityVertexConsumers().draw();
//        client.getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
//        buff.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

//    private void spawnParticles() {
//        if (client.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockAir)
//            return;
//
//        AxisAlignedBB aabb = block.getSelectedBoundingBox(blockState, client.world, pos);
//
//        // z- = north
//        // x- = west // block pos
//
//        Vector2d[] corners = new Vector2d[]{new Vector2d(aabb.minX, aabb.minZ), new Vector2d(aabb.maxX, aabb.maxZ),
//
//                new Vector2d(aabb.minX, aabb.maxZ), new Vector2d(aabb.maxX, aabb.minZ)};
//
//        Vector2d middle = new Vector2d(pos.getX() + 0.5f, pos.getZ() + 0.5f);
//
//        for (Vector2d corner : corners) {
//            double mX = middle.x - corner.x;
//            double mZ = middle.y - corner.y;
//
//            mX /= -0.5;
//            mZ /= -0.5;
//
//            client.effectRenderer.addEffect(new FBPParticleDigging(client.world, corner.x, pos.getY() + 0.1f, corner.y, mX, 0,
//                    mZ, 0.6f, 1, 1, 1, block.getActualState(blockState, client.world, pos), null, this.particleTexture)
//                    .multipleParticleScaleBy(0.5f).multiplyVelocity(0.5f));
//        }
//
//        for (Vector2d corner : corners) {
//            if (corner == null)
//                continue;
//
//            double mX = middle.x - corner.x;
//            double mZ = middle.y - corner.y;
//
//            mX /= -0.45;
//            mZ /= -0.45;
//
//            client.effectRenderer.addEffect(
//                    new FBPParticleDigging(client.world, corner.x, pos.getY() + 0.1f, corner.y, mX / 3, 0, mZ / 3, 0.6f, 1,
//                            1, 1, block.getActualState(blockState, client.world, pos), null, this.particleTexture)
//                            .multipleParticleScaleBy(0.75f).multiplyVelocity(0.75f));
//        }
//    }

    public void killParticle() {
        dead = true;
    }

    @Override
    public void markDead() {
        WonkyBlock.removeInvisibleBlock(pos);
    }
}
