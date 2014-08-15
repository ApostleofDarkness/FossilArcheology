package mods.fossil.fossilAI;

import mods.fossil.Fossil;
import mods.fossil.entity.mob.EntityDinosaur;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;


public class DinoAIHunt extends EntityAITarget
{
    private EntityDinosaur dinosaur;
    private DinoAINearestAttackableTargetSorter targetSorter;
    private final Class targetClass;
    
	World theWorld;
    
    /**
     * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
     * restrictions)
     */

    private EntityLivingBase targetEntity;
	private int attackCountdown;
	private int SEARCH_RANGE;

    public DinoAIHunt(EntityDinosaur dinosaur, Class _class, int range, boolean par4)
    {
        super(dinosaur, par4);
        this.theWorld = dinosaur.worldObj;
        this.dinosaur = dinosaur;
        this.targetClass = _class;
        this.SEARCH_RANGE = range;
        this.targetSorter = new DinoAINearestAttackableTargetSorter(this, this.dinosaur);
    }

    @Override
    public boolean shouldExecute()
    {
    	
        if(!theWorld.isRemote)
        {
	        if (!Fossil.FossilOptions.Dinos_Starve)
	        	return false;
        }
        
        if ((this.dinosaur.IsHungry() || this.dinosaur.IsDeadlyHungry()) &&  !this.dinosaur.SelfType.FoodMobList.IsEmpty())
        {
        double d0 = this.getTargetDistance();
        
        if (this.getTargetDistance() > SEARCH_RANGE)
        	return false;
        
        List list = this.dinosaur.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.dinosaur.boundingBox.expand(d0, 2.0D, d0));
        Collections.sort(list, this.targetSorter);
        Iterator iterator = list.iterator();

        while (iterator.hasNext())
        {
            EntityLiving entity = (EntityLiving)iterator.next();

            if (this.dinosaur.SelfType.FoodMobList.CheckMobByClass(entity.getClass()))
            {//It's food
                if(!(entity instanceof EntityDinosaur) || (entity instanceof EntityDinosaur && ((EntityDinosaur) entity).isModelized()==false))
                {//No modelized Dinos for Lunch!
                    this.targetEntity = entity;
                	//this.dinosaur.setAttackTarget(entity);
                	return true;
                }
            }
        }
        }
        //this.targetEntity = null;
        return false;
    }

    

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.targetEntity.isEntityAlive() ? false : (this.dinosaur.getDistanceSqToEntity(this.targetEntity) > 225.0D ? false : !this.dinosaur.getNavigator().noPath() || this.shouldExecute());
    }
    
    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.targetEntity = null;
        this.dinosaur.getNavigator().clearPathEntity();
    }
    
    /**
     * Updates the task
     */
    public void updateTask()
    {

        this.dinosaur.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);
        double d0 = (double)(this.dinosaur.width * 2.0F * this.dinosaur.width * 2.0F);
        double d1 = this.dinosaur.getDistanceSq(this.targetEntity.posX, this.targetEntity.boundingBox.minY, this.targetEntity.posZ);
        double d2 = 1.8D;

        if (d1 > d0 && d1 < 16.0D)
        {
            d2 = 1.0D;
        }
        else if (d1 < 225.0D)
        {
            d2 = 1.3D;
        }

        this.dinosaur.getNavigator().tryMoveToEntityLiving(this.targetEntity, d2);
        this.attackCountdown = Math.max(this.attackCountdown - 1, 0);

        if (d1 <= d0)
        {
            if (this.attackCountdown <= 0)
            {
                this.attackCountdown = 20;
                this.dinosaur.attackEntityAsMob(this.targetEntity);
            }
        }
    }
    
}
