package mods.fossil.entity.mob;

import mods.fossil.Fossil;
import mods.fossil.client.LocalizationStrings;
import mods.fossil.client.gui.GuiPedia;
import mods.fossil.fossilAI.DinoAINearestAttackableTarget;
import mods.fossil.fossilAI.EntityChildAIPanic;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTerrorBird extends EntityTameable
{
    protected static final ResourceLocation pediaheart = new ResourceLocation("fossil:textures/gui/PediaHeart.png");
    public static final String[] names = new String[] {"gastornis", "phorusrhacos", "titanis", "kelenken"};
    private ResourceLocation dodoeggicon = new ResourceLocation(Fossil.modid+":"+"textures/items/TerrorBird/" + "Egg_Cultivated_"+names[this.getSkin()]+".png");
    public float field_70886_e = 0.0F;
    public float destPos = 0.0F;
    public float field_70884_g;
    public float field_70888_h;
    public float field_70889_i = 1.0F;

    /** The time until the next egg is spawned. */
    public int timeUntilNextEgg;

    EntityPrehistoric entityPrehistoricClass = new EntityPrehistoric(worldObj);
    
    public EntityAITempt aiTemptSeeds = new EntityAITempt(this, 1.0D, Item.melonSeeds.itemID, false); //3
    public EntityAITempt aiTemptMeat = new EntityAITempt(this, 1.0D, Item.beefRaw.itemID, false);
    public DinoAINearestAttackableTarget aiAttackNearestQuagga = new DinoAINearestAttackableTarget(this, EntityQuagga.class, 40, true, true, false);
    public DinoAINearestAttackableTarget aiAttackNearestHorse = new DinoAINearestAttackableTarget(this, EntityHorse.class, 50, true, true, false);
	private int rotationAngle;
	private int prevRotationAngle;
	private int nextTimeUntilNextEgg;

    
    public EntityTerrorBird(World par1World)
    {
        super(par1World);
        this.setSize(1.5F, 2.5F);
        this.timeUntilNextEgg = this.rand.nextInt(6000) + 9000;
        this.nextTimeUntilNextEgg = this.rand.nextInt(6000) + 9000;
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityChildAIPanic(this, 1.4D)); //Only panic if a child.
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(2, this.aiSit);
        
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        
        if (par1World != null && !par1World.isRemote)
        {
            this.setAI(this.getSkin());
        }
        
        this.setTamed(false);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }
    
    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(EntityLivingBase entityLivingBase)
    {
        super.setAttackTarget(entityLivingBase);

        if (entityLivingBase == null)
        {
            this.setAngry(false);
        }
        else if (!this.isTamed())
        {
            this.setAngry(true);
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.attackDamage);
        
    	double attackValue = 3;
    	double movementValue = 0.3D;
    	double healthValue = 16;
    	
    	switch(this.getSkin())
    	{
    	case 0:
    		attackValue = 3;
    		movementValue = 0.3D;
    		healthValue = 14;
            break;
    	case 1:
    		attackValue = 4;
    		movementValue = 0.34D;
    		healthValue = 16;
            break;
    	case 2:
    		attackValue = 6;
    		movementValue = 0.37D;
    		healthValue = 20;
            break;
    	case 3:
    		attackValue = 8;
    		movementValue = 0.45D;
    		healthValue = 28;
            break;
    	}
    	
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(healthValue);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(movementValue);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(attackValue);
    }
    
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(18, Byte.valueOf((byte)0)); // For skin
        this.dataWatcher.addObject(19, new Byte((byte)0)); //Bit used for animations.
    	this.setSkin(this.worldObj.rand.nextInt(4));
    }
    
    private void setAI(int type)
    {
    	this.tasks.removeTask(this.aiTemptSeeds);
		this.targetTasks.removeTask(this.aiAttackNearestQuagga);
		this.targetTasks.removeTask(this.aiAttackNearestHorse);
		this.tasks.removeTask(this.aiTemptMeat);
		
    	switch(type)
    	{
    	case 0: default: this.tasks.addTask(3, this.aiTemptSeeds); break;
    	case 1: case 2: case 3: 
    		this.targetTasks.addTask(4, this.aiAttackNearestQuagga);
    		this.targetTasks.addTask(4, this.aiAttackNearestHorse);
    		this.tasks.addTask(3, this.aiTemptMeat);
    		break;
    	}
    }
    
    public int getSkin()
    {
        return this.dataWatcher.getWatchableObjectByte(18);
    }

    public void setSkin(int par1)
    {
        this.dataWatcher.updateObject(18, Byte.valueOf((byte)par1));
    }

    private void setPedia()
    {
        Fossil.ToPedia = (Object)this;
    }
    
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Angry", this.isAngry());
        compound.setInteger("TerrorBirdType", this.getSkin());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setAngry(compound.getBoolean("Angry"));
        this.setSkin(compound.getInteger("TerrorBirdType"));
        
        this.setAI(this.getSkin());
    }
    
    @Override
    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
       return this.isAngry() ? Fossil.modid + ":" + "terror_bird_attack" : Fossil.modid + ":" + "terror_bird_living";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound()
    {
    	return Fossil.modid + ":" + "terror_bird_hurt";
    }
    @Override
    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
    	return Fossil.modid + ":" + "terror_bird_death";
    }
    

    
    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        this.field_70888_h = this.field_70886_e;
        this.field_70884_g = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);

        if (this.destPos < 0.0F)
        {
            this.destPos = 0.0F;
        }

        if (this.destPos > 1.0F)
        {
            this.destPos = 1.0F;
        }

        if (!this.onGround && this.field_70889_i < 1.0F)
        {
            this.field_70889_i = 1.0F;
        }

        this.field_70889_i = (float)((double)this.field_70889_i * 0.9D);

        this.field_70886_e += this.field_70889_i * 2.0F;

        if (!this.isChild() && !this.worldObj.isRemote && --this.timeUntilNextEgg <= 0)
        {
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.entityDropItem( new ItemStack(Fossil.terrorBirdEgg, 1, this.getSkin()), 1 );
            this.timeUntilNextEgg = this.nextTimeUntilNextEgg;
        }
    }
    
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        this.rotationAngle = this.prevRotationAngle;

        if (this.chaseTarget())
        {
            this.prevRotationAngle += (1.0F - this.prevRotationAngle) * 0.4F;
        }
        else
        {
            this.prevRotationAngle += (0.0F - this.prevRotationAngle) * 0.4F;
        }

        if (this.chaseTarget())
        {
            this.numTicksToChaseTarget = 5;
        }
    }
    
    public boolean chaseTarget()
    {
        return this.dataWatcher.getWatchableObjectByte(19) == 1;
    }
    
    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float angle)
    {
        return (this.rotationAngle + (this.prevRotationAngle - this.rotationAngle) * angle) * 0.15F * (float)Math.PI;
    }
    
    @SideOnly(Side.CLIENT)
    public float getWingRotation()
    {
    	if(this.isAngry())
    	{
        return (float) Math.toRadians(45F);
    	}

    	return 0;
    }
    
    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }
    
    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.2F;
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    @Override
    protected int getDropItemId()
    {
        return Item.feather.itemID;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int j = this.rand.nextInt(3) + this.rand.nextInt(1 + par2);

        for (int k = 0; k < j; ++k)
        {
            this.dropItem(Item.feather.itemID, 1);
        }

        if (this.isBurning())
        {
            this.dropItem(Fossil.terrorBirdMeatCooked.itemID, 1);
        }
        else
        {
            this.dropItem(Fossil.terrorBirdMeat.itemID, 1);
        }
    }
    
    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource damageSource, float damageAmount)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
        	
            Entity entity = damageSource.getEntity();
            this.aiSit.setSitting(false);

            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                damageAmount = (damageAmount + 1.0F) / 2.0F;
            }

            return super.attackEntityFrom(damageSource, damageAmount);
        }
    }
    
    @Override
    public boolean attackEntityAsMob(Entity par1Entity)
    {
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int i = 0;

        if (par1Entity instanceof EntityLivingBase)
        {
            f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)par1Entity);
            i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)par1Entity);
        }

        boolean flag = par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag)
        {
            if (i > 0)
            {
                par1Entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0)
            {
                par1Entity.setFire(j * 4);
            }

            if (par1Entity instanceof EntityLivingBase)
            {
                EnchantmentThorns.func_92096_a(this, (EntityLivingBase)par1Entity, this.rand);
            }
        }

        return flag;
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    @Override
    protected void attackEntity(Entity entity, float float1)
    {
        if (this.attackTime <= 0 && float1 < 2.0F && entity.boundingBox.maxY > this.boundingBox.minY && entity.boundingBox.minY < this.boundingBox.maxY)
        {
            this.attackTime = 20;
            this.attackEntityAsMob(entity);
        }
    }
    
    public void setTamed(boolean tamed)
    {
        super.setTamed(tamed);
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer player)
    {
        ItemStack itemstack = player.inventory.getCurrentItem();

        if (itemstack != null && itemstack.itemID == Item.monsterPlacer.itemID)
        {
            if (!this.worldObj.isRemote)
            {
                Class oclass = EntityList.getClassFromID(itemstack.getItemDamage());

                if (oclass != null && oclass.isAssignableFrom(this.getClass()))
                {
                    EntityAgeable entityageable = this.createChild(this);

                    if (entityageable != null)
                    {
                    	((EntityTerrorBird)entityageable).setSkin(this.getSkin());
                        entityageable.setGrowingAge(-24000);
                        entityageable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                        this.worldObj.spawnEntityInWorld(entityageable);

                        if (itemstack.hasDisplayName())
                        {
                            entityageable.setCustomNameTag(itemstack.getDisplayName());
                        }

                        if (!player.capabilities.isCreativeMode)
                        {
                            --itemstack.stackSize;

                            if (itemstack.stackSize <= 0)
                            {
                            	player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                            }
                        }
                    }
                }
            }

            return true;
        }
        
        if (itemstack != null && FMLCommonHandler.instance().getSide().isClient() && itemstack.getItem() == Fossil.dinoPedia)
        {
            this.setPedia();
            player.openGui(Fossil.instance, 4, this.worldObj, (int)this.posX, (int)this.posY, (int)this.posZ);
            return true;
        }
        
        if (this.isTamed())
        {
            if (itemstack != null)
            {
                if (itemstack.getItem() instanceof ItemFood)
                {
                    ItemFood itemfood = (ItemFood)itemstack.getItem();
                }
            }

            if (player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isBreedingItem(itemstack))
            {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity((PathEntity)null);
                this.setTarget((Entity)null);
                this.setAttackTarget((EntityLivingBase)null);
            }
        }
        else if (itemstack != null)
        {
	        	if((this.getSkin() == 0 && itemstack.itemID == Block.pumpkin.blockID)
	        			|| (this.getSkin() != 0 && itemstack.itemID  == Fossil.quaggaMeat.itemID))
	        	{
		            if (!player.capabilities.isCreativeMode)
		            {
		                --itemstack.stackSize;
		            }
		
		            if (itemstack.stackSize <= 0)
		            {
		                player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
		            }
		
		            if (!this.worldObj.isRemote)
		            {
		            	if(!this.isAngry())
		            	{
			                if (this.rand.nextInt(3) == 0)
			                {
			                    this.setTamed(true);
			                    this.setPathToEntity((PathEntity)null);
			                    this.setAttackTarget((EntityLivingBase)null);
			                    this.aiSit.setSitting(true);
			                    this.setHealth(20.0F);
			                    this.setOwner(player.getCommandSenderName());
			                    this.playTameEffect(true);
			                    this.worldObj.setEntityState(this, (byte)7);
			                }
			                else
			                {
			                    this.playTameEffect(false);
			                    this.worldObj.setEntityState(this, (byte)6);
			                }
		            	}
		            	else //if Angry
		            	{
			                if (this.rand.nextInt(3) == 0)
			                {
			                    this.setAngry(false);
			                    this.setPathToEntity((PathEntity)null);
			                    this.setAttackTarget((EntityLivingBase)null);
			                    this.worldObj.setEntityState(this, (byte)6);
			                }
			                else
			                {
			                	this.findPlayerToAttack();
			                    this.playTameEffect(false);
			                    this.worldObj.setEntityState(this, (byte)6);
			                }
		            	}
		            }
		
		            return true;
	        	}
        }

        return super.interact(player);
    }
    
    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public EntityTerrorBird spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    {
    	EntityTerrorBird entity = new EntityTerrorBird(this.worldObj);
        String s = this.getOwnerName();

        if (s != null && s.trim().length() > 0)
        {
        	entity.setOwner(s);
        	entity.setTamed(true);
        	entity.setSkin(this.getSkin());
        }

        return entity;
    }

    public float getEyeHeight()
    {
        return this.height * 0.8F;
    }
    
    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(ItemStack itemstack)
    {
    	if(this.getSkin() == 0)
        return itemstack != null && itemstack.itemID == Block.pumpkin.blockID;
    	
    	return itemstack != null && itemstack.getItem() == Fossil.quaggaMeat;
    }

    public EntityAgeable createChild(EntityAgeable par1EntityAgeable)
    {
        return this.spawnBabyAnimal(par1EntityAgeable);
    }

    public Object Imprinting(double posX, double posY, double posZ)
    {
        return this;
    }
    
    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return false;
    }
    
    /**
     * Determines whether this wolf is angry or not.
     */
    public boolean isAngry()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    /**
     * Sets whether this wolf is angry or not.
     */
    public void setAngry(boolean isAngry)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (isAngry)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 2)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -3)));
        }
    }


    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean canMateWith(EntityAnimal entityAnimal)
    {
        if (entityAnimal == this)
        {
            return false;
        }
        else if (!this.isTamed())
        {
            return false;
        }
        else if (!(entityAnimal instanceof EntityTerrorBird))
        {
            return false;
        }
        else
        {
            EntityTerrorBird entity = (EntityTerrorBird)entityAnimal;
            if(this.getSkin() != entity.getSkin())
            {
            	return false;
            }
            return !entity.isTamed() ? false : (entity.isSitting() ? false : this.isInLove() && entity.isInLove());
        }
    }


    @SideOnly(Side.CLIENT)
    public void ShowPedia(GuiPedia p0)
    {
        p0.reset();
        
        if (this.hasCustomNameTag())
        {
            p0.PrintStringXY(this.getCustomNameTag(), p0.rightIndent, 24, 40, 90, 245);
        }

        p0.PrintStringXY(Character.toString(this.names[this.getSkin()].charAt(0)).toUpperCase()+this.names[this.getSkin()].substring(1), p0.rightIndent, 34, 0, 0, 0);
        p0.PrintPictXY(new ResourceLocation(Fossil.modid+":"+"textures/items/TerrorBird/" + "Egg_Cultivated_"+names[this.getSkin()]+".png"), ((p0.xGui/2) + (p0.xGui/4)), 7, 16, 16);

        p0.PrintPictXY(pediaheart, p0.rightIndent, 58, 9, 9);

        //Display Health
        p0.PrintStringXY(String.valueOf(this.getHealth()) + '/' + this.getMaxHealth(), p0.rightIndent+12, 58);
        
        if (this.hasCustomNameTag())
        {
            p0.AddStringLR("No Despawn", true);
        }
        
        if (this.isAngry())
        {
            p0.AddStringLR("Is Angry", true);
        }
        
        //Display owner name
        if (this.isTamed() && this.getOwner() != null)
        {
	        p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_TEXT_OWNER), true);
	        if (this.isTamed())
	        {
	            String s0 = this.getOwnerName();
	            if (s0.length() > 11)
	            {
	            	s0 = this.getOwnerName().substring(0, 11);
	            }
	            
	            p0.AddStringLR(s0, true);
	        }
        }
    }
    
    
    @SideOnly(Side.CLIENT)
    public void ShowPedia2(GuiPedia p0)
    {
    	entityPrehistoricClass.ShowPedia2(p0, this.names[this.getSkin()]);
    }

	@Override
	public Entity getOwner() {
		return this.worldObj.getPlayerEntityByName(this.getOwnerName());
	}
}
