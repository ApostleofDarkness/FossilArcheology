package mods.fossil.enchantments;

import mods.fossil.Fossil;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EnchantmentPaleontology  extends Enchantment {
	
	private final int weight;
	
	public EnchantmentPaleontology(int effectID, int rarity, EnumEnchantmentType enchantmentType) {
		super(effectID, rarity, enchantmentType);
		this.setName("paleontology");
		this.weight = 2;
		this.type = enchantmentType;
	}

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int par1)
    {
        return 5 + (par1 - 1) * 10;
    }

    public int getWeight()
    {
        return this.weight;
    }
    
    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 3;
    }
    
    //Allow clients to toggle whether or not they want to allow this enchantment on an enchantment table
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
    		if(Fossil.FossilOptions.AllowTableEnchantments)
    		{
        		return canApply(stack);
    		}
    		else
    		{
    			return false;
    		}
    }
    
    //Allow clients to toggle whether or not they want to allow this enchantment on books on an enchantment table
    @Override
    public boolean isAllowedOnBooks()
    {
    		if(Fossil.FossilOptions.AllowBookEnchantments)
    		{
        		return true;
    		}
    		else
    		{
    			return false;
    		}
    }
    
    public boolean canApply(ItemStack itemStack)
    {
    	if(itemStack.isItemStackDamageable())
    	{
    		if(itemStack.getItem() instanceof ItemPickaxe)
    		{
    			return super.canApply(itemStack);
    		}
    	}
    	return false;
    }
    
    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    
    public boolean canApplyTogether(Enchantment enchantment)
    {
    //    return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != archeology.effectId;
        return super.canApplyTogether(enchantment) && enchantment.effectId != silkTouch.effectId && enchantment.effectId != Fossil.archeology.effectId;
    }
    
    
    
}
