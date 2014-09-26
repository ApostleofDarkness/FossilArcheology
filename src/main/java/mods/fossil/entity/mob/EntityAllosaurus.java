package mods.fossil.entity.mob;

import java.util.Random;
import java.util.Vector;

import mods.fossil.Fossil;
import mods.fossil.client.LocalizationStrings;
import mods.fossil.client.gui.GuiPedia;
import mods.fossil.fossilAI.DinoAIAttackOnCollide;
import mods.fossil.fossilAI.DinoAIEat;
import mods.fossil.fossilAI.DinoAIFollowOwner;
import mods.fossil.fossilAI.DinoAIHunt;
import mods.fossil.fossilAI.DinoAIRideGround;
import mods.fossil.fossilAI.DinoAITargetNonTamedExceptSelfClass;
import mods.fossil.fossilAI.DinoAIWander;
import mods.fossil.fossilEnums.EnumDinoType;
import mods.fossil.fossilEnums.EnumOrderType;
import mods.fossil.fossilEnums.EnumSituation;
import mods.fossil.handler.FossilAchievementHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityAllosaurus extends EntityDinosaur
{
    private boolean looksWithInterest;//IS THAT WORKING?

    //public final float HuntLimit = (float)(this.getHungerLimit() * 4 / 5);
    //private float field_25048_b;
    //private float field_25054_c;//HAS something todo with look with interest
    //private boolean field_25052_g;

    //public ItemStack ItemInMouth = null;

    public boolean PreyChecked = false;
    public boolean SupportChecked = false;
    public Vector MemberList = new Vector();//ARE THEESE 5 DOING ANYTHING AND WORKING CORRECTLY?
    public float SwingAngle = -1000.0F;
    public int FleeingTick = 0;

	private final String texturePath;
    
    public static final double baseHealth = EnumDinoType.Allosaurus.Health0;
    public static final double baseDamage = EnumDinoType.Allosaurus.Strength0;
    public static final double baseSpeed = EnumDinoType.Allosaurus.Speed0;
    
    public static final double maxHealth = EnumDinoType.Allosaurus.HealthMax;
    public static final double maxDamage = EnumDinoType.Allosaurus.StrengthMax;
    public static final double maxSpeed = EnumDinoType.Allosaurus.SpeedMax;
    

    public EntityAllosaurus(World var1)
    {
        super(var1, EnumDinoType.Allosaurus);
        this.looksWithInterest = false;
        this.updateSize();
        /*
         * EDIT VARIABLES PER DINOSAUR TYPE
         */
        this.adultAge = EnumDinoType.Allosaurus.AdultAge;
        // Set initial size for hitbox. (length/width, height)
        this.setSize(1.4F, 1.3F);
        // Size of dinosaur at day 0.
        this.minSize = 0.55F;
        // Size of dinosaur at age Adult.
        this.maxSize = 3.1F;
        
        texturePath = Fossil.modid + ":textures/mob/" + this.SelfType.toString() + "/";

        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(3, new DinoAIAttackOnCollide(this, 1.2D, true));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new DinoAIFollowOwner(this, 1.0F, 5.0F, 2.0F));
        this.tasks.addTask(7, new DinoAIWander(this, 1.0D));
        this.tasks.addTask(7, new DinoAIEat(this, 48));
        this.targetTasks.addTask(7, new DinoAIHunt(this, EntityLiving.class, 500, false));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(4, new DinoAITargetNonTamedExceptSelfClass(this, EntityLiving.class, 750, false));
        
        tasks.addTask(1, new DinoAIRideGround(this, 1.1)); // mutex all
        
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityTRex.class, 16.0F, 0.8D, 1.33D));
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntitySpinosaurus.class, 16.0F, 0.8D, 1.33D));
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityBrachiosaurus.class, 16.0F, 0.8D, 1.33D));
    }
    
    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    public boolean isAIEnabled()
    {
        return !this.isModelized() && !this.isWeak();
    }

    public boolean attackEntityAsMob(Entity var1)
    {
        return super.attackEntityAsMob(var1);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(baseSpeed);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(baseHealth);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(baseDamage);
    }

    /**
     * Returns the texture's file path as a String.
     */
    @Override
    public String getTexture()
    {
        if (this.isModelized())
        {
            return super.getTexture();
        }
        
        if (this.isWeak())
        {
            switch (this.getSubSpecies())
            {
            case 1: default:
            	return texturePath + "Allosaurus_Black_Weak.png";
            case 2: 
            	return texturePath + "Allosaurus_Red_Weak.png";
            case 3: 
            	return texturePath + "Allosaurus_Green_Weak.png";
            }

        }

        switch (this.getSubSpecies())
        {
        default: case 1:
                return this.isChild() ? texturePath + "Allosaurus_Black_Baby.png" : this.isTamed() ? texturePath + "Allosaurus_Black_Tame.png" : texturePath + "Allosaurus_Black.png";
        case 2: 
        	return this.isChild() ? texturePath + "Allosaurus_Red_Baby.png" : this.isTamed() ? texturePath + "Allosaurus_Red_Tame.png" : texturePath + "Allosaurus_Red.png";
        case 3: 
        	return this.isChild() ? texturePath + "Allosaurus_Green_Baby.png" : this.isTamed() ? texturePath + "Allosaurus_Green_Tame.png" : texturePath + "Allosaurus_Green.png";
        }
    }


    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
    protected String getLivingSound()
    {
    	if(this.isModelized())
    		return null;
        return this.isTamed() ? Fossil.modid + ":" + "allosaurus_living_tamed" : Fossil.modid + ":" + "allosaurus_living_untamed";
    }
    
    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return (!this.isAdult() && !this.isTeen()) ? 0.3F : 0.7F;
    }
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound var1)
    {
        super.writeEntityToNBT(var1);
        //var1.setInteger("LearningChestTick", this.LearningChestTick);
        //var1.setBoolean("Angry", this.isSelfAngry());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound var1)
    {
        super.readEntityFromNBT(var1);
        //this.setSelfAngry(var1.getBoolean("Angry"));
        //this.LearningChestTick = var1.getInteger("LearningChestTick");
        //this.setSelfSitting(var1.getBoolean("Sitting"));
        //this.InitSize();
        //this.OrderStatus = EnumOrderType.values()[var1.getByte("OrderStatus")];
        /*String var2 = var1.getString("Owner");

        if (var2.length() > 0)
        {
            this.setOwner(var2);
            this.setTamed(true);
        }*/
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    public void moveEntityWithHeading(float par1, float par2)
    {
    	super.moveEntityWithHeading(par1, par2);
    	if(this.isWeak()) {
            this.motionX *= 0.0D;
            this.motionZ *= 0.0D;
            this.rotationPitch = this.rotationYaw = 0;
    	}
    }
    
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
    }

    public float getEyeHeight()
    {
        return this.height;
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
     * Disables a mob's ability to move on its own while true.
     */
    protected boolean isMovementCeased()
    {
        return this.isSitting() || this.isWeak();// || this.field_25052_g;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource damagesource, float damageamount)
    {
        Entity entity = damagesource.getEntity();
        boolean var4 = false;
        this.setSitting(false);

        if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
        {
            damageamount = (damageamount + 1) / 2;
        }

        if (super.attackEntityFrom(damagesource, damageamount))
        {
            if (!this.isAngry())
            {
                if (entity instanceof EntityArrow && ((EntityArrow)entity).shootingEntity != null)
                {
                    entity = ((EntityArrow)entity).shootingEntity;
                }

                if (entity instanceof EntityLiving)
                {
                    this.setTarget((EntityLiving)entity);
                }

                if (entity instanceof EntityPlayer && this.isTamed() && ((EntityPlayer)entity) == this.getOwner() && this.getRNG().nextInt(6) == 0)
                {
                    //Hit by the owner->untame
                    this.SendStatusMessage(EnumSituation.Betrayed);
                    this.setTamed(false);
                    this.setOwner("");
                    this.ItemInMouth = null;
                    this.setAngry(true);
                    this.setTarget(entity);
                    this.PreyChecked = true;
                    var4 = true;
                }
            }
            else if (entity != this && entity != null)
            {
                this.entityToAttack = entity;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        return this.isAngry() ? this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D) : null;
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity var1, float var2)
    {
        if (var1.isDead)
        {
            this.setTarget((Entity)null);
        }

        if (var2 > 2.0F && var2 < 5.0F && this.rand.nextInt(10) == 0)
        {
            if (this.onGround)
            {
                double var3 = var1.posX - this.posX;
                double var5 = var1.posZ - this.posZ;
                float var7 = MathHelper.sqrt_double(var3 * var3 + var5 * var5);
                this.motionX = var3 / (double)var7 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                this.motionZ = var5 / (double)var7 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                this.worldObj.playSoundAtEntity(this, "Raptor_attack", this.getSoundVolume() * 2.0F, 1.0F);
                this.jump();
            }
        }
        else if ((double)var2 < 1.899999976158142D && var1.boundingBox.maxY > this.boundingBox.minY && var1.boundingBox.minY < this.boundingBox.maxY)
        {
            this.attackTime = 20;
            var1.attackEntityFrom(DamageSource.causeMobDamage(this), 2 + this.getDinoAge());
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer player)
    {
        ItemStack itemStack = player.inventory.getCurrentItem();

        if (itemStack != null)
        {
            if (itemStack.getItem() == Fossil.gem)
            {
                if (this.isWeak() && !this.isTamed())
                {
                    this.heal(200);
                    this.increaseHunger(500);
                    this.setTamed(true);
                    setPathToEntity(null);
                    setAttackTarget(null);
                    this.setOwner(player.username);
                    --itemStack.stackSize;

                    if (itemStack.stackSize <= 0)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                    }

                    return true;
                }
                else
                {
                    if (!this.isWeak())
                    {
                        if (!this.worldObj.isRemote)
                        {
                            Fossil.ShowMessage(StatCollector.translateToLocal(LocalizationStrings.STATUS_GEM_ERROR_HEALTH), player);
                        }
                    }

                    if (!this.isAdult())
                    {
                        if (!this.worldObj.isRemote)
                        {
                            Fossil.ShowMessage(StatCollector.translateToLocal(LocalizationStrings.STATUS_GEM_ERROR_YOUNG), player);
                        }
                    }

                    return false;
                }
            }
            
            if (this.SelfType.FoodItemList.CheckItemById(itemStack.itemID) || this.SelfType.FoodBlockList.CheckBlockById(itemStack.itemID))
            {
            	return false;
            }

            if (!Fossil.DebugMode())
            {
                if (itemStack.getItem() == Fossil.chickenEss)
                {
                    if (!this.worldObj.isRemote)
                    {
                        Fossil.ShowMessage(StatCollector.translateToLocal(LocalizationStrings.STATUS_ESSENCE_FAIL), player);
                        return true;
                    }
                }
            }
        }

        return super.interact(player);
    }

    /**
     * Check if the dinosaur is in a weakened state.
     * @return
     */
    public boolean isWeak()
    {
        return (this.getHealth() < 8) && (this.getDinoAge() >= this.adultAge) && !this.isTamed();
    }
    
    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 10;
    }

    /**
     * Time remaining during which the Animal is sped up and flees.
     */
    protected void updateWanderPath()
    {
        boolean var1 = false;
        int var2 = -1;
        int var3 = -1;
        int var4 = -1;
        float var5 = -99999.0F;
        EnumOrderType var10001 = this.OrderStatus;

        if (this.OrderStatus == EnumOrderType.FreeMove || !this.isTamed())
        {
            for (int var6 = 0; var6 < 10; ++var6)
            {
                int var7 = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0D);
                int var8 = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0D);
                int var9 = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0D);
                float var10 = this.getBlockPathWeight(var7, var8, var9);

                if (var10 > var5)
                {
                    var5 = var10;
                    var2 = var7;
                    var3 = var8;
                    var4 = var9;
                    var1 = true;
                }
            }

            if (var1)
            {
                this.setPathToEntity(this.worldObj.getEntityPathToXYZ(this, var2, var3, var4, 10.0F, true, false, true, false));
            }
        }
    }
    
    public float getMountHeight()
    {
        return this.height*0.60F;
    }
    
    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
        	 this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountHeight() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    public boolean CheckSpace()
    {
        return !this.isEntityInsideOpaqueBlock();
    }

    public EntityAllosaurus spawnBabyAnimal(EntityAgeable var1)
    {
        return new EntityAllosaurus(this.worldObj);
    }

    /**
     * Sets the entity which is to be attacked.
     */
    public void setTarget(Entity var1)
    {
        super.setTarget(var1);
    }

    public EnumOrderType getOrderType()
    {
        return this.OrderStatus;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable var1)
    {
    	EntityAllosaurus baby = new EntityAllosaurus(this.worldObj);
    	baby.setSubSpecies(this.getSubSpecies());
    	return baby;
    }
    
    /**
     * This gets called when a dinosaur grows naturally or through Chicken Essence.
     */
    @Override
    public void updateSize()
    {
        double healthStep;
        double attackStep;
        double speedStep;
        healthStep = (this.maxHealth - this.baseHealth) / (this.adultAge + 1);
        attackStep = (this.maxDamage - this.baseDamage) / (this.adultAge + 1);
        speedStep = (this.maxSpeed - this.baseSpeed) / (this.adultAge + 1);
        
        
    	if(this.getDinoAge() <= this.adultAge){
	        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(Math.round(this.baseHealth + (healthStep * this.getDinoAge())));
	        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.round(this.baseDamage + (attackStep * this.getDinoAge())));
	        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(this.baseSpeed + (speedStep * this.getDinoAge()));
	
	        if (this.isTeen()) {
	        	this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(0.5D);
	        }
	        else if (this.isAdult()){
	            this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(2.0D);
	        }
	        else {
	            this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(0.0D);
	        }
    	}
    }
	
    public void ShowPedia(GuiPedia p0)
    {
        super.ShowPedia(p0);

        if (this.isWeak())
        {
            p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_TEXT_WEAK), true, 255, 40, 90);
        }

        if (!this.isWeak() && !this.isTamed()  && this.isAdult())
        {
            p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_TEXT_CAUTION), true, 255, 40, 90);
        }
    }
	
    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData)
    {
        par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
        Random random = new Random();
        this.setSubSpecies(random.nextInt(3) + 1);
        this.setDinoAge(this.SelfType.AdultAge);
        this.updateSize();
        this.heal(200);
        return par1EntityLivingData;
    }
}
