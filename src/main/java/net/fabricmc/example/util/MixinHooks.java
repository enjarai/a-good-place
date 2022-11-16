package net.fabricmc.example.util;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class MixinHooks {
    public static int invisibleBlockCount = 0;
    public static MatrixStack particleMatrixStack = null;
    public static VertexConsumerProvider particleVertexConsumerProvider = null;
}
