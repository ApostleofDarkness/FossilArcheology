package mods.fossil.entity.mob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;

import mods.fossil.Fossil;
import mods.fossil.client.DinoSound;
import mods.fossil.client.LocalizationStrings;
import mods.fossil.client.gui.GuiPedia;
import mods.fossil.fossilAI.EntityAIBegSC;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
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

public class EntitySmilodon extends EntityPrehistoric
{
    private float field_70926_e;
    private float field_70924_f;

    /** true is the Smilodon is wet else false */
    private boolean isShaking;
    private boolean field_70928_h;

    /**
     * This time increases while Smilodon is shaking and emitting water particles.
     */
    private float timeSmilodonIsShaking;
    private float prevTimeSmilodonIsShaking;

    public EntitySmilodon(World var1)
    {
        super(var1);
        this.setSize(1.5F, 1.0F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this,  1.0D, true));
        this.tasks.addTask(5, new EntityAIFollowOwner(this,  1.0D, 10.0F, 2.0F));
        this.tasks.addTask(6, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this,  1.0D));
        this.tasks.addTask(8, new EntityAIBegSC(this, 8.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntitySheep.class, 200, false));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityDodo.class, 200, false));
        this.targetTasks.addTask(5, new EntityAITargetNonTamed(this, EntityPig.class, 200, false));
        this.targetTasks.addTask(6, new EntityAITargetNonTamed(this, EntityCow.class, 200, false));
        this.targetTasks.addTask(7, new EntityAITargetNonTamed(this, EntityChicken.class, 200, false));
        this.targetTasks.addTask(8, new EntityAITargetNonTamed(this, EntityVillager.class, 200, false));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityTerrorBird.class, 200, false));
        
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityTRex.class, 16.0F, 0.8D, 1.33D));
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntitySpinosaurus.class, 16.0F, 0.8D, 1.33D));
        this.experienceValue = 5;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.30000001192092896D);

        if (this.isTamed())
        {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(14.0D);
        }
    }

    @Override
    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return DinoSound.smilodon_living;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound()
    {
        return DinoSound.smilodon_hurt;
    }
    @Override
    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return DinoSound.smilodon_death;
    }
    
    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }
    private void setPedia()
    {
        Fossil.ToPedia = (Object)this;
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(EntityLivingBase par1EntityLivingBase)
    {
        super.setAttackTarget(par1EntityLivingBase);

        if (par1EntityLivingBase == null)
        {
            this.setAngry(false);
        }
        else if (!this.isTamed())
        {
            this.setAngry(true);
        }
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick()
    {
        this.dataWatcher.updateObject(18, Float.valueOf(this.getHealth()));
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(18, Float.valueOf(this.getHealth()));
        this.dataWatcher.addObject(19, new Byte((byte)0));
        this.dataWatcher.addObject(20, new Byte((byte)BlockColored.getBlockFromDye(1)));
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        this.playSound("mob.Smilodon.step", 0.15F, 1.0F);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound var1)
    {
        super.writeEntityToNBT(var1);
        var1.setBoolean("Angry", this.isAngry());
        var1.setByte("CollarColor", (byte)this.getCollarColor());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound var1)
    {
        super.readEntityFromNBT(var1);
        this.setAngry(var1.getBoolean("Angry"));
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
    	return false;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return -1;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote && this.isShaking && !this.field_70928_h && !this.hasPath() && this.onGround)
        {
            this.field_70928_h = true;
            this.timeSmilodonIsShaking = 0.0F;
            this.prevTimeSmilodonIsShaking = 0.0F;
            this.worldObj.setEntityState(this, (byte)8);
        }
    }

    private boolean isInterest(int var1)
    {
        return this.isChild() ? var1 == Item.bucketMilk.itemID : var1 == Item.beefRaw.itemID || var1 == Item.porkRaw.itemID || var1 == Item.chickenRaw.itemID;// || var1 == Fossil.rawDinoMeat.itemID;
    }

    private boolean TamedInterest(int var1)
    {
        return !this.isTamed() ? this.isInterest(var1) : var1 == Item.beefRaw.itemID || var1 == Item.porkRaw.itemID || var1 == Item.chickenRaw.itemID;// || var1 == Fossil.rawDinoMeat.itemID;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        this.field_70924_f = this.field_70926_e;

        if (this.lookswithinterest())
        {
            this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
        }
        else
        {
            this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
        }

        if (this.lookswithinterest())
        {
            this.numTicksToChaseTarget = 10;
        }

        if (this.isWet())
        {
            this.isShaking = true;
            this.field_70928_h = false;
            this.timeSmilodonIsShaking = 0.0F;
            this.prevTimeSmilodonIsShaking = 0.0F;
        }
        else if ((this.isShaking || this.field_70928_h) && this.field_70928_h)
        {
            if (this.timeSmilodonIsShaking == 0.0F)
            {
                this.worldObj.playSoundAtEntity(this, "mob.Smilodon.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.prevTimeSmilodonIsShaking = this.timeSmilodonIsShaking;
            this.timeSmilodonIsShaking += 0.05F;

            if (this.prevTimeSmilodonIsShaking >= 2.0F)
            {
                this.isShaking = false;
                this.field_70928_h = false;
                this.prevTimeSmilodonIsShaking = 0.0F;
                this.timeSmilodonIsShaking = 0.0F;
            }

            if (this.timeSmilodonIsShaking > 0.4F)
            {
                float var1 = (float)this.boundingBox.minY;
                int var2 = (int)(MathHelper.sin((this.timeSmilodonIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

                for (int var3 = 0; var3 < var2; ++var3)
                {
                    float var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    this.worldObj.spawnParticle("splash", this.posX + (double)var4, (double)(var1 + 0.8F), this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
                }
            }
        }
    }

    public boolean getSmilodonShaking()
    {
        return this.isShaking;
    }

    public float getShadingWhileShaking(float var1)
    {
        return 0.75F + (this.prevTimeSmilodonIsShaking + (this.timeSmilodonIsShaking - this.prevTimeSmilodonIsShaking) * var1) / 2.0F * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float par1, float par2)
    {
        float f2 = (this.prevTimeSmilodonIsShaking + (this.timeSmilodonIsShaking - this.prevTimeSmilodonIsShaking) * par1 + par2) / 1.8F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }
        else if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        return MathHelper.sin(f2 * (float)Math.PI) * MathHelper.sin(f2 * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float var1)
    {
        return (this.field_70924_f + (this.field_70926_e - this.field_70924_f) * var1) * 0.15F * (float)Math.PI;
    }

    public float getEyeHeight()
    {
        return this.height * 0.8F;
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    private void getPathOrWalkableBlock(Entity var1, float var2)
    {
        PathEntity var3 = this.worldObj.getPathEntityToEntity(this, var1, 16.0F, true, false, true, false);

        if (var3 == null && var2 > 12.0F)
        {
            int var4 = MathHelper.floor_double(var1.posX) - 2;
            int var5 = MathHelper.floor_double(var1.posZ) - 2;
            int var6 = MathHelper.floor_double(var1.boundingBox.minY);

            for (int var7 = 0; var7 <= 4; ++var7)
            {
                for (int var8 = 0; var8 <= 4; ++var8)
                {
                    if ((var7 < 1 || var8 < 1 || var7 > 3 || var8 > 3) && this.worldObj.isBlockNormalCube(var4 + var7, var6 - 1, var5 + var8) && !this.worldObj.isBlockNormalCube(var4 + var7, var6, var5 + var8) && !this.worldObj.isBlockNormalCube(var4 + var7, var6 + 1, var5 + var8))
                    {
                        this.setLocationAndAngles((double)((float)(var4 + var7) + 0.5F), (double)var6, (double)((float)(var5 + var8) + 0.5F), this.rotationYaw, this.rotationPitch);
                        return;
                    }
                }
            }
        }
        else
        {
            this.setPathToEntity(var3);
        }
    }

    /**
     * Disables a mob's ability to move on its own while true.
     */
    protected boolean isMovementCeased()
    {
        return this.isSitting() || this.field_70928_h;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource var1, float var2)
    {
        Entity var3 = var1.getEntity();
        this.aiSit.setSitting(false);

        if (var3 != null && !(var3 instanceof EntityPlayer) && !(var3 instanceof EntityArrow))
        {
            var2 = (var2 + 1) / 2;
        }

        return super.attackEntityFrom(var1, var2);
    }

    public boolean attackEntityAsMob(Entity var1)
    {
        int var2 = this.isTamed() ? 4 : 2;
        return var1.attackEntityFrom(DamageSource.causeMobDamage(this), var2);
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
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLiving var1)
    {
        if (var1 instanceof EntityAnimal)
        {
            int var2 = this.getGrowingAge();

            if (var2 < 0)
            {
                var2 += 3000;

                if (var2 > 0)
                {
                    var2 = 0;
                }

                this.setGrowingAge(var2);
            }
        }

        super.onKillEntity(var1);
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity var1, float var2)
    {
        if (var2 > 2.0F && var2 < 6.0F && this.rand.nextInt(10) == 0)
        {
            if (this.onGround)
            {
                double var8 = var1.posX - this.posX;
                double var5 = var1.posZ - this.posZ;
                float var7 = MathHelper.sqrt_double(var8 * var8 + var5 * var5);
                this.motionX = var8 / (double)var7 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                this.motionZ = var5 / (double)var7 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                this.motionY = 0.4000000059604645D;
            }
        }
        else if ((double)var2 < 1.5D && var1.boundingBox.maxY > this.boundingBox.minY && var1.boundingBox.minY < this.boundingBox.maxY)
        {
            this.attackTime = 20;
            byte var3 = 4;

            if (!this.isChild())
            {
                var3 = 8;
            }

            var1.attackEntityFrom(DamageSource.causeMobDamage(this), var3);
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer var1)
    {
        ItemStack var2 = var1.inventory.getCurrentItem();

        if (var2 != null && FMLCommonHandler.instance().getSide().isClient() && var2.getItem().itemID == Fossil.dinoPedia.itemID)
        {
            this.setPedia();
            var1.openGui(Fossil.instance, 4, this.worldObj, (int)this.posX, (int)this.posY, (int)this.posZ);
            return true;
        }

        if (!this.isTamed())
        {
            if (var2 != null && this.isInterest(var2.itemID) && !this.isAngry())
            {
                --var2.stackSize;

                if (var2.stackSize <= 0)
                {
                    if (var2.getItem().hasContainerItem())
                    {
                        var1.inventory.setInventorySlotContents(var1.inventory.currentItem, new ItemStack(var2.getItem().getContainerItem()));
                    }
                    else
                    {
                        var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
                    }
                }

                if (!this.worldObj.isRemote)
                {
                    if (this.rand.nextInt(this.isChild() ? 1 : 3) == 0)
                    {
                        this.setOwner(var1.username);
                        this.setTamed(true);
                        this.setIsTamed(true);
                        this.setPathToEntity((PathEntity)null);
                        this.aiSit.setSitting(true);
                        this.setHealth(20);
                        this.setOwner(var1.username);
                        this.showHeartsOrSmokeFX(true);
                        this.worldObj.setEntityState(this, (byte)7);
                    }
                    else
                    {
                        this.showHeartsOrSmokeFX(false);
                        this.worldObj.setEntityState(this, (byte)6);
                    }
                }

                return true;
            }
            
            if (var1.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isBreedingItem(var2))
            {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity((PathEntity)null);
                this.setTarget((Entity)null);
                this.setAttackTarget((EntityLivingBase)null);
            }
            
        }
        else
        {
            if (var2 != null && Item.itemsList[var2.itemID] instanceof ItemFood)
            {
                ItemFood var3 = (ItemFood)Item.itemsList[var2.itemID];

                if (this.TamedInterest(var2.itemID) && this.getHealth() < 20.0F)
                {
                    --var2.stackSize;
                    this.heal(var3.getHealAmount());

                    if (var2.stackSize <= 0)
                    {
                        var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
                    }

                    return true;
                }

                if (var3 == Item.rottenFlesh && this.getGrowingAge() < 0)
                {
                    int var4 = this.getGrowingAge();
                    var4 -= 3000;
                    this.setGrowingAge(var4);
                    return true;
                }
            }

            if (var1.username.equalsIgnoreCase(this.getOwnerName()))
            {
                if (!this.worldObj.isRemote)
                {
                    this.aiSit.setSitting(!this.isSitting());
                    this.isJumping = false;
                    this.setPathToEntity((PathEntity)null);
                }

                return true;
            }
        }

        return super.interact(var1);
    }

    void showHeartsOrSmokeFX(boolean var1)
    {
        String var2 = "heart";

        if (!var1)
        {
            var2 = "smoke";
        }

        for (int var3 = 0; var3 < 7; ++var3)
        {
            double var4 = this.rand.nextGaussian() * 0.02D;
            double var6 = this.rand.nextGaussian() * 0.02D;
            double var8 = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(var2, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var4, var6, var8);
        }
    }

    public void handleHealthUpdate(byte var1)
    {
        if (var1 == 8)
        {
            this.field_70928_h = true;
            this.timeSmilodonIsShaking = 0.0F;
            this.prevTimeSmilodonIsShaking = 0.0F;
        }
        else
        {
            super.handleHealthUpdate(var1);
        }
    }

    @SideOnly(Side.CLIENT)
    public float getTailRotation()
    {
        return this.isAngry() ? 1.5393804F : (this.isTamed() ? (0.55F - (20.0F - this.getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F));
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    public boolean isWheat(ItemStack var1)
    {
        return var1 == null ? false : (!(Item.itemsList[var1.itemID] instanceof ItemFood) ? false : ((ItemFood)Item.itemsList[var1.itemID]).isWolfsFavoriteMeat());
    }

    @SideOnly(Side.CLIENT)
    public void ShowPedia(GuiPedia p0)
    {
    
    	
        p0.reset();
        p0.PrintPictXY(new ResourceLocation(Fossil.modid + ":" + "textures/items/" + "Smilodon" + "_DNA.png"), ((p0.xGui/2) + (p0.xGui/4)), 7, 16, 16); //185

        
        /* LEFT PAGE
         * 
         * OWNER:
         * (+2) OWNER NAME 
         * RIDEABLE
         * ORDER
         * ABLE TO FLY
         * ABLE TO CHEST
         * DANGEROUS
         * 
         * 
         */
        
        /* RIGHT PAGE
         * 
         * CUSTOM NAME
         * DINOSAUR NAME
         * DINO AGE
         * HEALTH
         * HUNGER
         * 
         */
        if (this.hasCustomNameTag())
        {
            p0.PrintStringXY(this.getCustomNameTag(), p0.rightIndent, 24, 40, 90, 245);
        }

        p0.PrintStringXY(StatCollector.translateToLocal(LocalizationStrings.ANIMAL_SMILODON), p0.rightIndent, 34, 0, 0, 0);
        //p0.PrintPictXY(pediaclock, p0.rightIndent, 46, 8, 8);
        p0.PrintPictXY(pediaheart, p0.rightIndent, 58, 9, 9);
        //p0.PrintPictXY(pediafood, p0.rightIndent, 70, 9, 9);

        //Print "Day" after age
        /*
        if (this.getDinoAge() == 1)
        {
            p0.PrintStringXY(String.valueOf(this.getDinoAge()) + " " + StatCollector.translateToLocal(LocalizationStrings.PEDIA_EGG_DAY), p0.rightIndent+12, 46);
        }
        else
        {
            p0.PrintStringXY(String.valueOf(this.getDinoAge()) + " " + StatCollector.translateToLocal(LocalizationStrings.PEDIA_EGG_DAYS), p0.rightIndent+12, 46);
        }
        */

        //Display Health
        p0.PrintStringXY(String.valueOf(this.getHealth()) + '/' + this.getMaxHealth(), p0.rightIndent+12, 58);
        //Display Hunger
        //p0.PrintStringXY(String.valueOf(this.getHunger()) + '/' + this.getMaxHunger(), p0.rightIndent+12, 70);

        //Display owner name
        if (this.isTamed())
        {
            p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_TEXT_OWNER), true);
            String s0 = this.getOwnerName();

            if (s0.length() > 11)
            {
                s0 = this.getOwnerName().substring(0, 11);
            }

            p0.AddStringLR(s0, true);
        }

        //Display if Rideable
        /*
        if (this.isRideable() && this.isAdult())
            p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_TEXT_RIDEABLE), true);

        if (this.SelfType.OrderItem != null)
        p0.AddStringLR(StatCollector.translateToLocal("Order: " + this.SelfType.OrderItem.getStatName()), true);

        
        for (int i = 0; i < this.SelfType.FoodItemList.index; i++)
        {
            if (this.SelfType.FoodItemList.getItem(i) != null)
            {
                p0.AddMiniItem(this.SelfType.FoodItemList.getItem(i));
            }
        }
        */

        //TODO show all blocks the dino can eat
    }
    
    
    @SideOnly(Side.CLIENT)
    public void ShowPedia2(GuiPedia p0)
    {
    	super.ShowPedia2(p0, "Smilodon");
    }

    public boolean isAngry()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    public void setAngry(boolean var1)
    {
        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (var1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 2)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -3)));
        }
    }

    /**
     * Return this Smilodon's collar color.
     */
    public int getCollarColor()
    {
        return this.dataWatcher.getWatchableObjectByte(20) & 15;
    }

    /**
     * Set this Smilodon's collar color.
     */
    public void setCollarColor(int par1)
    {
        this.dataWatcher.updateObject(20, Byte.valueOf((byte)(par1 & 15)));
    }

    public boolean isTamed()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
    }

    public void setIsTamed(boolean var1)
    {
//        byte var2 = this.dataWatcher.getWatchableObjectByte(16);
        if (var1)
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)1));
        }
        else
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)0));
        }
    }

    public EntityAnimal spawnBabyAnimal(EntityAnimal var1)
    {
        EntitySmilodon var2 = new EntitySmilodon(this.worldObj);

        if (this.isTamed())
        {
            var2.setOwner(this.getOwnerName());
            var2.setTamed(true);
        }

        return var2;
    }
    
    public EntitySmilodon Imprinting(double var1, double var3, double var5)
    {
        EntityPlayer var7 = this.worldObj.getClosestPlayer(var1, var3, var5, 50.0D);

        if (var7 == null)
        {
            return this;
        }
        else
        {
            this.setOwner(var7.username);
            this.setTamed(true);
            return this;
        }
    }

    public void func_70918_i(boolean par1)
    {
        if (par1)
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)1));
        }
        else
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)0));
        }
    }

    public boolean lookswithinterest()
    {
        return this.dataWatcher.getWatchableObjectByte(19) == 1;
    }

    public boolean func_48135_b(EntityAnimal var1)
    {
        if (var1 == this)
        {
            return false;
        }
        else if (!this.isTamed())
        {
            return false;
        }
        else if (!(var1 instanceof EntitySmilodon))
        {
            return false;
        }
        else
        {
            EntitySmilodon var2 = (EntitySmilodon)var1;
            return !var2.isTamed() ? false : (var2.isSitting() ? false : this.isInLove() && var2.isInLove());
        }
    }

    /*public EntityAgeable func_90011_a(EntityAgeable var1)
    {
        return null;
    }*/

    @Override
    public EntityAgeable createChild(EntityAgeable var1)
    {
        EntityAgeable var2 = (new EntitySmilodon(this.worldObj)).Imprinting(this.posX, this.posY, this.posZ);
        var2.setGrowingAge(-24000);
        var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        return var2;
    }
}
