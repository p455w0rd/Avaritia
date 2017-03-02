package fox.spiteful.avaritia.crafting;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ExtremeCraftingManager {
    /** The static instance of this class */
    private static final ExtremeCraftingManager instance = new ExtremeCraftingManager();
    /** A list of all the recipes added */
    private List<IRecipe> recipes = new ArrayList<IRecipe>();

    /**
     * Returns the static instance of this class
     */
    public static final ExtremeCraftingManager getInstance()
    {
        /** The static instance of this class */
        return instance;
    }

    public ExtremeShapedRecipe addRecipe(ItemStack result, Object ... recipe)
    {
        String s = "";
        int i = 0;
        int width = 0;
        int height = 0;

        if (recipe[i] instanceof String[])
        {
            String[] astring = (String[])((String[])recipe[i++]);

            for (int l = 0; l < astring.length; ++l)
            {
                String s1 = astring[l];
                ++height;
                width = s1.length();
                s = s + s1;
            }
        }
        else
        {
            while (recipe[i] instanceof String)
            {
                String s2 = (String)recipe[i++];
                ++height;
                width = s2.length();
                s = s + s2;
            }
        }

        HashMap hashmap;

        for (hashmap = new HashMap(); i < recipe.length; i += 2)
        {
            Character character = (Character)recipe[i];
            ItemStack itemstack1 = null;

            if (recipe[i + 1] instanceof Item)
            {
                itemstack1 = new ItemStack((Item)recipe[i + 1]);
            }
            else if (recipe[i + 1] instanceof Block)
            {
                itemstack1 = new ItemStack((Block)recipe[i + 1], 1, 32767);
            }
            else if (recipe[i + 1] instanceof ItemStack)
            {
                itemstack1 = (ItemStack)recipe[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        ItemStack[] ingredients = new ItemStack[width * height];

        for (int i1 = 0; i1 < width * height; ++i1)
        {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(Character.valueOf(c0)))
            {
                ingredients[i1] = ((ItemStack)hashmap.get(Character.valueOf(c0))).copy();
            }
            else
            {
                ingredients[i1] = null;
            }
        }

        ExtremeShapedRecipe shapedrecipes = new ExtremeShapedRecipe(width, height, ingredients, result);
        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    public ExtremeShapedOreRecipe addExtremeShapedOreRecipe(ItemStack result, Object... recipe){
        ExtremeShapedOreRecipe craft = new ExtremeShapedOreRecipe(result, recipe);
        recipes.add(craft);
        return craft;
    }

    public ExtremeShapelessRecipe addShapelessRecipe(ItemStack result, Object ... ingredients)
    {
        ArrayList arraylist = new ArrayList();
        Object[] aobject = ingredients;
        int i = ingredients.length;

        for (int j = 0; j < i; ++j)
        {
            Object object1 = aobject[j];

            if (object1 instanceof ItemStack)
            {
                arraylist.add(((ItemStack)object1).copy());
            }
            else if (object1 instanceof Item)
            {
                arraylist.add(new ItemStack((Item)object1));
            }
            else
            {
                if (!(object1 instanceof Block))
                {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                arraylist.add(new ItemStack((Block)object1));
            }
        }

        ExtremeShapelessRecipe recipe = new ExtremeShapelessRecipe(result, arraylist);
        this.recipes.add(recipe);
        return recipe;
    }

    public ExtremeShapelessOreRecipe addShapelessOreRecipe(ItemStack result, Object ... ingredients){
        ExtremeShapelessOreRecipe recipe = new ExtremeShapelessOreRecipe(result, ingredients);
        recipes.add(recipe);
        return recipe;
    }

    public ItemStack findMatchingRecipe(InventoryCrafting matrix, World world)
    {
        int numFound = 0;
        ItemStack firstStackFound = null;
        ItemStack secondStackFound = null;
        int j;

        //Figure out how many items there are in the inventory, Stack 0 is the first item found and stack 1 is the second.
        for (j = 0; j < matrix.getSizeInventory(); ++j)
        {
            ItemStack inSlot = matrix.getStackInSlot(j);

            if (inSlot != null)
            {
                if (numFound == 0)
                {
                    firstStackFound = inSlot;
                }

                if (numFound == 1)
                {
                    secondStackFound = inSlot;
                }

                ++numFound;
            }
        }

        //This seems to be for "Repair" / combining recipes
        if (numFound == 2 && firstStackFound.getItem() == secondStackFound.getItem() && firstStackFound.getCount() == 1 && secondStackFound.getCount() == 1 && firstStackFound.getItem().isRepairable())
        {
            Item item = firstStackFound.getItem();
            int j1 = item.getMaxDamage() - firstStackFound.getItemDamage();
            int k = item.getMaxDamage() - secondStackFound.getItemDamage();
            int l = j1 + k + item.getMaxDamage() * 5 / 100;
            int i1 = item.getMaxDamage() - l;

            if (i1 < 0)
            {
                i1 = 0;
            }

            return new ItemStack(firstStackFound.getItem(), 1, i1);
        }
        else
        {
            for (j = 0; j < this.recipes.size(); ++j)
            {
                IRecipe irecipe = (IRecipe)this.recipes.get(j);

                if (irecipe.matches(matrix, world))
                {
                    return irecipe.getCraftingResult(matrix);
                }
            }

            return null;
        }
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getRemainingItems(craftMatrix);
            }
        }

        NonNullList<ItemStack> aitemstack = NonNullList.<ItemStack>withSize(craftMatrix.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < aitemstack .size(); ++i)
        {
            ItemStack itemstack = craftMatrix.getStackInSlot(i);
            aitemstack.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return aitemstack;
    }

    /**
     * returns the List<> of all recipes
     */
    public List getRecipeList()
    {
        return this.recipes;
    }
}