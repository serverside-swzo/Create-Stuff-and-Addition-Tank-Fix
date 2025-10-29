package net.swzo.createsa_tank_fix.capability;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class TankCapability implements IFluidHandlerItem {

    public static final String TAG_STOCK = "tagStock";
    public static final int FLUID_TO_POINTS_RATIO = 10;
    public static final int MAX_POINTS_PER_OPERATION = 100;

    protected final ItemStack container;
    private final int capacity;
    private final Fluid validFluid;

    public TankCapability(@NotNull ItemStack container, Void context, int capacity, Fluid validFluid) {
        this.container = container;
        this.capacity = Math.max(1, capacity);
        this.validFluid = validFluid;
    }

    @NotNull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    private double getStock() {
        return container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getDouble(TAG_STOCK);
    }

    private void setStock(double stock) {
        container.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, cd -> {
            CompoundTag newTag = cd.copyTag();
            newTag.putDouble(TAG_STOCK, stock);
            return CustomData.of(newTag);
        });
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid() == this.validFluid;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource)) return 0;

        double currentStock = getStock();
        int pointsSpaceLeft = this.capacity - (int) currentStock;

        if (pointsSpaceLeft <= 0) return 0;

        int pointsFromOfferedFluid = resource.getAmount() / FLUID_TO_POINTS_RATIO;
        int pointsToAdd = Math.min(pointsSpaceLeft, pointsFromOfferedFluid);
        pointsToAdd = Math.min(pointsToAdd, MAX_POINTS_PER_OPERATION);

        if (pointsToAdd <= 0) return 0;
        int fluidActuallyConsumed = pointsToAdd * FLUID_TO_POINTS_RATIO;

        if (action.execute()) setStock(currentStock + pointsToAdd);
        return fluidActuallyConsumed;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }
}