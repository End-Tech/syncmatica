package ch.endte.syncmatica.litematica_mixin;

import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SubRegionPlacement.class)
public interface MixinSubregionPlacement {
    @Accessor(value = "defaultPos", remap = false)
    BlockPos getDefaultPosition();

    @Accessor(value = "pos", remap = false)
    void setBlockPosition(BlockPos pos);

    @Accessor(value = "rotation", remap = false)
    void setBlockRotation(BlockRotation pos);

    @Accessor(value = "mirror", remap = false)
    void setBlockMirror(BlockMirror pos);

    @Invoker(value = "resetToOriginalValues", remap = false)
    void reset();
}
