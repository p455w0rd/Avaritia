package fox.spiteful.avaritia.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerCustomWorkbench extends ContainerWorkbench {

    public BlockPos pos;
    public World worldObj;

    public ContainerCustomWorkbench(InventoryPlayer inv, World world, BlockPos pos){
        super(inv, world, pos);
        this.pos = pos;
        this.worldObj = world;
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        return !this.worldObj.isAirBlock(pos) && player.getDistanceSq(pos) <= 64.0D;
    }
}
