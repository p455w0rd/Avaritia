package fox.spiteful.avaritia.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityDireCrafting extends TileLudicrous implements ISidedInventory{

    private ItemStack result;
    private ItemStack[] matrix = new ItemStack[81];

    /*@Override
    public boolean canUpdate()
    {
        return false;
    }*/

    @Override
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.matrix)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }


    @Override
    public void readCustomNBT(NBTTagCompound tag)
    {
        this.result = new ItemStack(tag.getCompoundTag("Result"));
        for(int x = 0;x < matrix.length;x++){
            matrix[x] = new ItemStack(tag.getCompoundTag("Craft" + x));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tag)
    {
        if(result != null) {
            NBTTagCompound produce = new NBTTagCompound();
            result.writeToNBT(produce);
            tag.setTag("Result", produce);
        }
        else
            tag.removeTag("Result");

        for(int x = 0;x < matrix.length;x++){
            if(matrix[x] != null){
                NBTTagCompound craft = new NBTTagCompound();
                matrix[x].writeToNBT(craft);
                tag.setTag("Craft" + x, craft);
            }
            else
                tag.removeTag("Craft" + x);
        }
    }

    @Override
    public int getSizeInventory()
    {
        return 82;
    }

    @Override
    public ItemStack getStackInSlot(int slot){
        if(slot == 0)
            return result;
        else if(slot <= matrix.length)
            return matrix[slot - 1];
        else
            return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int decrement){

        if(slot == 0){
            if(result != null){
                for(int x = 1;x <= matrix.length;x++){
                    decrStackSize(x, 1);
                }
                if(result.getCount() <= decrement) {
                    ItemStack craft = result;
                    result = null;
                    return craft;
                }
                ItemStack split = result.splitStack(decrement);
                if(result.getCount() <= 0){
                    result = null;
                }
                return split;
            }
            else
                return null;
        }
        else if(slot <= matrix.length){
            if(matrix[slot - 1] != null){
                if(matrix[slot - 1].getCount() <= decrement){
                    ItemStack ingredient = matrix[slot - 1];
                    matrix[slot - 1] = null;
                    return ingredient;
                }
                ItemStack split = matrix[slot - 1].splitStack(decrement);
                if(matrix[slot - 1].getCount() <= 0){
                    matrix[slot - 1] = null;
                }
                return split;
            }
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot){
        if(slot == 0){
            if(result != null){
                for(int x = 1;x <= matrix.length;x++){
                    decrStackSize(x, 1);
                }

                ItemStack craft = result;
                result = null;
                return craft;

            }
            else
                return null;
        }
        else if(slot <= matrix.length){
            if(matrix[slot - 1] != null){
                ItemStack ingredient = matrix[slot - 1];
                matrix[slot - 1] = null;
                return ingredient;
            }
        }
        return null;
    }

    @Override
    public void openInventory(EntityPlayer player) {}
    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.getWorld().getTileEntity(pos) == this && player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack){

        return false;
    }

    @Override
    public int getInventoryStackLimit(){
        return 64;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack){
        if(slot == 0){
            result = stack;
        }
        else if(slot <= matrix.length){
            matrix[slot - 1] = stack;
        }
    }

    @Override
    public String getName()
    {
        return  "container.dire";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName(){
        if(hasCustomName()) {
            return new TextComponentString(getName());
        }
        return new TextComponentTranslation(getName());
    }

    @Override
    public int[] getSlotsForFace(EnumFacing face){
        return new int[]{};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, EnumFacing face){
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, EnumFacing face){
        return false;
    }

    /**
     * What does this method do?  Nobody knoooooows! OoooooOOOOOOOOooooOOOOOOOO
     * @return
     */
    @Override
    public int getFieldCount() {
        return 0;
    }

    /**
     * What does this method do?  Nobody knoooooows! OoooooOOOOOOOOooooOOOOOOOO
     * @return
     */
    @Override
    public void setField(int id, int value) {}

    /**
     * What does this method do?  Nobody knoooooows! OoooooOOOOOOOOooooOOOOOOOO
     * @return
     */
    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void clear(){
        result = null;
        for(int x = 0;x < matrix.length;x++){
            matrix[x] = null;
        }
    }

}
