package mods.fossil.guiBlocks;

import java.util.Random;

import mods.fossil.Fossil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFeeder extends BlockContainer
{
    private Random furnaceRand = new Random();
    private Icon Top1;//None
    private Icon Top2;//Herb
    private Icon Top3;//Carn
    private Icon Top4;//Both
    private Icon Front1;
    private Icon Front2;
    private Icon Front3;
    private Icon Front4;
    private Icon Bottom;
    
    private static final int NO_BIT = 0;
    private static final int HERB_BIT = 4;
    private static final int CARN_BIT = 8;
    private static final int BOTH_BITS = 12;
    
    private static final int DIRECTION_BITS = 3;

    public BlockFeeder(int id)
    {
        super(id, Material.iron);
    }

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    @SideOnly(Side.CLIENT)
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return Fossil.feederActive.blockID;
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public int idDropped(int var1, Random var2, int var3)
    {
        return Fossil.feederActive.blockID;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        this.setDefaultDirection(world, x, y, z);
    }
    
    public int getRenderType() {
        return Fossil.feederRenderID;
       }

    /**
     * set a blocks direction
     */
    private void setDefaultDirection(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
        	int block = world.getBlockId(x, y, z - 1);
        	int block1 = world.getBlockId(x, y, z + 1);
        	int block2 = world.getBlockId(x - 1, y, z);
        	int block3 = world.getBlockId(x + 1, y, z);
            
            byte b0 = 3;

            if (Block.opaqueCubeLookup[block] && !Block.opaqueCubeLookup[block1])
            {
                b0 = 3;
            }

            if (Block.opaqueCubeLookup[block1] && !Block.opaqueCubeLookup[block])
            {
                b0 = 2;
            }

            if (Block.opaqueCubeLookup[block2] && !Block.opaqueCubeLookup[block3])
            {
                b0 = 5;
            }

            if (Block.opaqueCubeLookup[block3] && !Block.opaqueCubeLookup[block2])
            {
                b0 = 4;
            }

            world.setBlockMetadataWithNotify(x, y, z, b0, 2);
        }
    }

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("fossil:Feeder_Sides");
        this.Bottom = par1IconRegister.registerIcon("fossil:Feeder_Bottom");
        this.Top1 = par1IconRegister.registerIcon("fossil:Feeder_Top1");
        this.Top2 = par1IconRegister.registerIcon("fossil:Feeder_Top2");
        this.Top3 = par1IconRegister.registerIcon("fossil:Feeder_Top3");
        this.Top4 = par1IconRegister.registerIcon("fossil:Feeder_Top4");
        this.Front1 = par1IconRegister.registerIcon("fossil:Feeder_Front1");
        this.Front2 = par1IconRegister.registerIcon("fossil:Feeder_Front2");
        this.Front3 = par1IconRegister.registerIcon("fossil:Feeder_Front3");
        this.Front4 = par1IconRegister.registerIcon("fossil:Feeder_Front4");
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int side, int meta)
    {
    	if(side == 0)
    	{
    		return this.Bottom;
    	}
    	else if(side != 1 && ((meta & DIRECTION_BITS) + 2) != side)
    	{
    		return this.blockIcon;
    	}
    	else
    	{
    		if (side == 1)
    		{
    			switch (meta & BOTH_BITS)
                {
                    case NO_BIT:
                        return this.Top1;//no food

                    case HERB_BIT:
                        return this.Top2;//herbivore

                    case CARN_BIT:
                        return this.Top3;//carnivore

                    case BOTH_BITS:
                        return this.Top4;//both
                }
            }
            else//Front
            {
                switch (meta & BOTH_BITS)
                {
                    case NO_BIT:
                        return this.Front1;//no food

                    case HERB_BIT:
                        return this.Front2;//herbivore

                    case CARN_BIT:
                        return this.Front3;//carnivore

                    case BOTH_BITS:
                        return this.Front4;//both
                }
            }
    		
    	}
    	return this.blockIcon;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int var6, float var7, float var8, float var9)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityFeeder tileentity = (TileEntityFeeder)world.getBlockTileEntity(x, y, z);

            if (tileentity != null)
            {
                player.openGui(Fossil.instance, 2, world, x, y, z);
            }

            return true;
        }
    }

    public static void updateFurnaceBlockState(boolean herb, boolean carn, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity tileentity = world.getBlockTileEntity(x, y, z);

    	world.setBlock(x, y, z,Fossil.feederActiveID);

    	if(herb) //If there's VEGGIES
    		meta |= HERB_BIT;
        else //If there's NO VEGGIES
        	meta &= ~HERB_BIT;
        if(carn) //If there's MEAT
        	meta |= CARN_BIT;
        else //If there's NO MEAT
        	meta &= ~CARN_BIT;
                
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);

        if (tileentity != null)
        {
            tileentity.validate();
            world.setBlockTileEntity(x, y, z, tileentity);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityFeeder();
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemstack)
    {
        int entityDirection = MathHelper.floor_double((double)(entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (entityDirection == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }

        if (entityDirection == 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }

        if (entityDirection == 2)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }

        if (entityDirection == 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (itemstack.hasDisplayName())
        {
            ((TileEntityFeeder)world.getBlockTileEntity(x, y, z)).setGuiDisplayName(itemstack.getDisplayName());
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World world, int x, int y, int z, int block, int var6)
    {
            TileEntityFeeder tileentity = (TileEntityFeeder)world.getBlockTileEntity(x, y, z);

            if (tileentity != null)
            {
                for (int i = 0; i < tileentity.getSizeInventory(); ++i)
                {
                    ItemStack itemstack = tileentity.getStackInSlot(i);

                    if (itemstack != null)
                    {
                        float xOffset = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                        float yOffset = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                        float zOffset = this.furnaceRand.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.stackSize > 0)
                        {
                            int rand = this.furnaceRand.nextInt(21) + 10;

                            if (rand > itemstack.stackSize)
                            {
                            	rand = itemstack.stackSize;
                            }

                            itemstack.stackSize -= rand;
                            EntityItem entityItem = new EntityItem(world, (double)((float)x + xOffset), (double)((float)y + yOffset), (double)((float)z + zOffset), new ItemStack(itemstack.getItem(), rand, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                            {
                            	entityItem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                            }

                            float offset = 0.05F;
                            entityItem.motionX = (double)((float)this.furnaceRand.nextGaussian() * offset);
                            entityItem.motionY = (double)((float)this.furnaceRand.nextGaussian() * offset + 0.2F);
                            entityItem.motionZ = (double)((float)this.furnaceRand.nextGaussian() * offset);
                            world.spawnEntityInWorld(entityItem);
                        }
                    }
                }
            }
        super.breakBlock(world, x, y, z, block, var6);
    }
    
    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
        return Container.calcRedstoneFromInventory((IInventory)par1World.getBlockTileEntity(par2, par3, par4));
    }
}
