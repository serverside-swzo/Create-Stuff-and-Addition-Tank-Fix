package net.swzo.createsa_tank_fix.mixin;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
@Restriction(require = {@Condition("sophisticatedcore")})
@Mixin(value = TankUpgradeWrapper.class, remap = false)
public abstract class TankUpgradeWrapperMixin {
    @Shadow
    protected FluidStack contents;

    @Shadow
    @Final
    private TankUpgradeWrapper.TankComponentItemHandler inventory;

    @Shadow
    @Final
    public static int OUTPUT_RESULT_SLOT;

    @Shadow
    public abstract FluidStack drain(int maxDrain, IFluidHandler.FluidAction action, boolean ignoreInOutLimit);

    @Inject(method = "fillHandler(Lnet/neoforged/neoforge/fluids/capability/IFluidHandlerItem;Ljava/util/function/Consumer;ZZ)Z", at = @At("HEAD"), cancellable = true)
    private void createsa_tank_fix_mixin_fillHandler(IFluidHandlerItem fluidHandler, Consumer<ItemStack> updateContainerStack, boolean moveFullToResult, boolean simulateIncludingFullFill, CallbackInfoReturnable<Boolean> cir) {
        ItemStack containerStack = fluidHandler.getContainer();
        if (containerStack.isEmpty()) return;

        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(containerStack.getItem());
        if (registryName == null || !registryName.getNamespace().equals("create_sa")) return;
        cir.setReturnValue(this.customFillHandler(fluidHandler, updateContainerStack, moveFullToResult, simulateIncludingFullFill));
    }

    private boolean customFillHandler(IFluidHandlerItem fluidHandler, Consumer<ItemStack> updateContainerStack, boolean moveFullToResult, boolean simulateIncludingFullFill) {
        if (this.contents.isEmpty() || !fluidHandler.isFluidValid(0, this.contents)) return false;

        int totalFilledSimulated = 0;
        int fluidToOffer = this.contents.getAmount();

        ItemStack simContainerCopy = fluidHandler.getContainer().copy();
        IFluidHandlerItem simHandler = simContainerCopy.getCapability(Capabilities.FluidHandler.ITEM);
        if (simHandler == null) return false;

        while (totalFilledSimulated < fluidToOffer) {
            FluidStack offerStack = this.contents.copyWithAmount(fluidToOffer - totalFilledSimulated);
            int filledThisLoop = simHandler.fill(offerStack, IFluidHandler.FluidAction.SIMULATE);
            if (filledThisLoop <= 0) break;
            simHandler.fill(this.contents.copyWithAmount(filledThisLoop), IFluidHandler.FluidAction.EXECUTE);
            totalFilledSimulated += filledThisLoop;
        }

        int filled = totalFilledSimulated;
        if (filled <= 0) return false;

        if (moveFullToResult) {
            ItemStack filledCopy = simHandler.getContainer();
            if (!this.inventory.insertItem(OUTPUT_RESULT_SLOT, filledCopy, true).isEmpty()) return false;
        }

        if (simulateIncludingFullFill) {
            boolean wouldBeFull = simHandler.fill(this.contents.copyWithAmount(1), IFluidHandler.FluidAction.SIMULATE) == 0;
            FluidStack drainedSim = this.drain(filled, IFluidHandler.FluidAction.SIMULATE, true);
            if (drainedSim.getAmount() != filled) return false;
            return wouldBeFull;
        }
        FluidStack drained = this.drain(filled, IFluidHandler.FluidAction.EXECUTE, true);
        if (drained.isEmpty()) return false;

        int amountToFill = drained.getAmount();
        int filledActually = 0;
        while (filledActually < amountToFill) {
            FluidStack fillStack = drained.copyWithAmount(amountToFill - filledActually);
            int filledThisLoop = fluidHandler.fill(fillStack, IFluidHandler.FluidAction.EXECUTE);
            if (filledThisLoop <= 0) break;
            filledActually += filledThisLoop;
        }
        ItemStack finalContainer = fluidHandler.getContainer();

        if (moveFullToResult) {
            boolean isFull = fluidHandler.fill(this.contents.copyWithAmount(1), IFluidHandler.FluidAction.SIMULATE) == 0;
            if (isFull) {
                updateContainerStack.accept(ItemStack.EMPTY);
                this.inventory.insertItem(OUTPUT_RESULT_SLOT, finalContainer, false);
            } else updateContainerStack.accept(finalContainer);
        } else updateContainerStack.accept(finalContainer);
        return true;
    }
}