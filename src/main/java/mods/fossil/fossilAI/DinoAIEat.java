package mods.fossil.fossilAI;

import mods.fossil.Fossil;
import mods.fossil.entity.mob.EntityDinosaur;
import mods.fossil.guiBlocks.TileEntityFeeder;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DinoAIEat extends EntityAIBase
{
    private static final Logger Log = Fossil.Log;
    
    private EntityDinosaur dinosaur;
    private double destX;
    private double destY;
    private double destZ;
    
    protected int entityPosX;
    protected int entityPosY;
    protected int entityPosZ;

    protected EntityCreature taskOwner;
    
    private static final int NO_TARGET = -1;
    private static final int ITEM = 1;
    private static final int BLOCK = 2;
    private static final int MOB = 3;
    private static final int FEEDER = 4;
    private int typeofTarget = NO_TARGET;
    private int TimeAtThisTarget = 0;

    // the range in which the dino is able to look for items
    private final int SEARCH_RANGE;

    // the range the dino is able to get the item when in
    private final int USE_RANGE = 3;

    // The item the dino is going to take
    private TileEntityFeeder targetFeeder;
    private EntityItem targetItem;
    private EntityLiving targetMob;
    private Vec3 targetBlock;
    private EntityLivingBase targetEntity;
    private DinoAINearestAttackableTargetSorter targetSorter;

    private World theWorld;

    /**
     * Creates The AI, Input: Dino, Speed, searching range
     */
    public DinoAIEat(EntityDinosaur Dino0, int Range0)
    {
    	this.theWorld = Dino0.worldObj;
        this.targetMob = null;
        this.targetFeeder = null;	
        this.dinosaur = Dino0;
        this.setMutexBits(1);
        this.SEARCH_RANGE = Range0;
        this.targetSorter = new DinoAINearestAttackableTargetSorter(this, this.dinosaur);
        this.TimeAtThisTarget = 0;
    }

    /**
     * Determine if this AI Task is interruptible by a higher (= lower value) priority task.
     */
    public boolean isInterruptible()
    {
        return true;
    }
    
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {

        if(!theWorld.isRemote)
        {
	        if (!Fossil.FossilOptions.Dinos_Starve)
	        	return false;
        }
        
        if (this.dinosaur.IsHungry())
        {
            int Range = this.SEARCH_RANGE;// Current Searching range

            if(this.dinosaur.IsDeadlyHungry())
                Range*=2;
	
	        //Feeder has priority over other food sources.
	        if (this.dinosaur.SelfType.useFeeder())
	        {
	        	PathNavigate pathnavigate = this.dinosaur.getNavigator();
	            PathEntity pathentity = pathnavigate.getPath();

	            if (pathentity != null && !pathentity.isFinished())
	            {
	                    PathPoint pathpoint = pathentity.getFinalPathPoint();
	                    this.entityPosX = pathpoint.xCoord;
	                    this.entityPosY = pathpoint.yCoord + 1;
	                    this.entityPosZ = pathpoint.zCoord;

	                    if (this.dinosaur.getDistanceSq((double)this.entityPosX, this.dinosaur.posY, (double)this.entityPosZ) <= 2.25D)
	                    {
	                    	if(Fossil.DebugMode())
	                    	{
	                    		this.dinosaur.worldObj.setBlock(this.entityPosX, this.entityPosY, this.entityPosZ, Block.tallGrass.blockID);
	                    	}
	                    	targetFeeder = this.dinosaur.GetNearestFeeder(Range/2);
	                    }
	            }

		        if(this.targetFeeder != null)
		        {
	            this.destX = this.targetFeeder.xCoord;
	            this.destY = this.targetFeeder.yCoord;
	            this.destZ = this.targetFeeder.zCoord;
	            this.typeofTarget = FEEDER;
	            return targetFeeder != null;
		        }
	        }
	        //After Feeder, check if there are items, THEN blocks on the ground to eat.
	        else if (!this.dinosaur.SelfType.FoodItemList.IsEmpty() || !this.dinosaur.SelfType.FoodBlockList.IsEmpty())
	        {
	
	            this.targetItem = this.getNearestItem2(Range/2);
	            if( this.targetItem != null) {
	                this.destX = targetItem.posX;
	                this.destY = targetItem.posY;
	                this.destZ = targetItem.posZ;
	            	this.typeofTarget = ITEM;
	            	return true;
	            }
	            /*
	            if (targetItem != null)//Found Item, go there and eat it
	            {
	                this.destX = targetItem.xCoord;
	                this.destY = targetItem.yCoord;
	                this.destZ = targetItem.zCoord;
	                this.typeofTarget = ITEM;
	                Log.log(Level.FINEST, "ITEM FOUND!");
	                return targetItem != null;
	            }
	            */
	            
	            if(!this.dinosaur.SelfType.FoodBlockList.IsEmpty())//Hasn't found anything and has blocks it can look for
	            {
	                Vec3 targetBlock = this.dinosaur.getBlockToEat(Range/2);
	                
	                if (targetBlock != null)//Found Item, go there and eat it
	                {
	                    this.destX = targetBlock.xCoord;
	                    this.destY = targetBlock.yCoord;
	                    this.destZ = targetBlock.zCoord;
	                    this.typeofTarget=BLOCK;
	                    //System.out.println("BLOCK FOUND!");
	                    return true;
	                }
	            }
	        }
        }
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting()
    {
    	
    	if( !this.dinosaur.IsHungry()){
    		return false;
    	}
    	else
    	{
    		switch(this.typeofTarget) {
	    		case NO_TARGET:
	    			break;
	    		case ITEM:
	    			return targetItem.isEntityAlive();
	    		case BLOCK:
	    			return this.dinosaur.SelfType.FoodBlockList.CheckBlockById(this.dinosaur.worldObj.getBlockId((int)destX, (int)destY, (int)destZ));
	    		case MOB:
	    			return targetMob != null;
	    		case FEEDER:
	    			return targetFeeder.isInvalid();
	    		default:
	    			break;
    		}	
    	}
    	return false;
        //return ((this.dinosaur.IsHungry() || this.dinosaur.IsDeadlyHungry()) && (this.typeofTarget != -1));
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask()
    {
        this.dinosaur.setSitting(false);
        double Distance = Math.sqrt(Math.pow(this.dinosaur.posX - this.destX, 2.0D) + Math.pow(this.dinosaur.posZ - this.destZ, 2.0D));

        /*
        if (Distance > Range)
        {
            endTask();
        }
        */

        if (this.typeofTarget == FEEDER){

        	if(this.targetFeeder == null)
        		endTask();
        	
        	if(Distance < SEARCH_RANGE) {
        		this.dinosaur.getNavigator().tryMoveToXYZ(this.destX, this.destY, this.destZ, 1.0D);
        	
        		if (Distance < 4.5D){
        			if (this.targetFeeder != null) {
		                int healval = MathHelper.floor_double(this.targetFeeder.Feed(this.dinosaur, this.dinosaur.SelfType) / 15D);
		                this.dinosaur.heal(healval);
		                this.TimeAtThisTarget++;
		
		                if (this.TimeAtThisTarget == 100){
		                    endTask();
		                }
        			}
        		}
        	}
        	else {
        		endTask();
        	}

        }

        if (this.typeofTarget == ITEM){
        	
        	if(this.targetItem == null)
        		endTask();
        	if(Distance < SEARCH_RANGE) {
	        	this.dinosaur.getNavigator().tryMoveToXYZ(this.destX, this.destY, this.destZ, 1.0D);
	            if (Distance < 2.5){
	
	                if (this.targetItem != null && this.targetItem.isEntityAlive()){
	                    int i = this.dinosaur.PickUpItem(this.targetItem.getEntityItem());
	
	                    if (i > 0){
	                        this.targetItem.getEntityItem().stackSize = i;
	                        endTask();
	                    }
	                    else{
	                        this.targetItem.setDead();
	                        endTask();
	                    }
	                }
	            }
        	}
        	else {
        		endTask();
        	}
        }

        if (this.typeofTarget == BLOCK) {
        	if(!this.dinosaur.SelfType.FoodBlockList.CheckBlockById(this.dinosaur.worldObj.getBlockId((int)destX, (int)destY, (int)destZ)))
        		endTask();
        	if(Distance < SEARCH_RANGE) {
        		this.dinosaur.getNavigator().tryMoveToXYZ(this.destX, this.destY, this.destZ, 1.0D);
		            if (Distance < 2.5){
		            	if(this.dinosaur.SelfType.FoodBlockList.CheckBlockById(this.dinosaur.worldObj.getBlockId((int)destX, (int)destY, (int)destZ))) {
			                this.dinosaur.heal(this.dinosaur.SelfType.FoodBlockList.getBlockHeal(this.dinosaur.worldObj.getBlockId((int)destX, (int)destY, (int)destZ)));
			                this.dinosaur.increaseHunger(this.dinosaur.SelfType.FoodBlockList.getBlockFood(this.dinosaur.worldObj.getBlockId((int)destX, (int)destY, (int)destZ)));
			                this.dinosaur.worldObj.setBlock((int)destX, (int)destY, (int)destZ, 0);
			                
			                this.TimeAtThisTarget++;
			                if (this.TimeAtThisTarget == 20)
			                {
			                    endTask();
			                }
		            	}
		            }
        	}
        	else{
        		endTask();
        	}
        }
    }

    public void endTask()
    {
//        this.dinosaur.getNavigator().clearPathEntity();
        this.TimeAtThisTarget = 0;
        targetItem = null;
        targetBlock = null;
        targetFeeder = null;
        this.targetEntity = null;
        this.typeofTarget = NO_TARGET;
    }
    
    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.TimeAtThisTarget = 0;
        targetItem = null;
        targetBlock = null;
        targetFeeder = null;
        this.targetEntity = null;
        this.typeofTarget = NO_TARGET;
    }

    private TileEntityFeeder getNearbyFeeder()
    {
        double range = 36;
        List<TileEntity> nearbyEntities = theWorld.getEntitiesWithinAABB(TileEntityFeeder.class, this.dinosaur.boundingBox.expand(range, range, range));

        for (TileEntity entityFeeder : nearbyEntities)
        {
            TileEntityFeeder nearbyFeeder = (TileEntityFeeder) entityFeeder;

            if (this.dinosaur.SelfType.useFeeder())
            {
                return nearbyFeeder;
            }
        }

        return null;
    }

    private EntityItem getNearestItem2(int SEARCH_RANGE)
    {
        List nearbyItems = this.dinosaur.worldObj.getEntitiesWithinAABB(EntityItem.class, this.dinosaur.boundingBox.expand(SEARCH_RANGE, SEARCH_RANGE, SEARCH_RANGE));
        Collections.sort(nearbyItems, this.targetSorter);
        Iterator iterateNearbyItems = nearbyItems.iterator();
        EntityItem entityItem = null;

        while (iterateNearbyItems.hasNext())
        {
        	
            EntityItem entityItem1 = (EntityItem) iterateNearbyItems.next();

            if ((this.dinosaur.SelfType.FoodItemList.CheckItemById(entityItem1.getEntityItem().itemID) 
            		|| this.dinosaur.SelfType.FoodBlockList.CheckBlockById(entityItem1.getEntityItem().itemID))
            		&& this.dinosaur.getDistanceSqToEntity(entityItem1) < SEARCH_RANGE)
            {        
            	entityItem = entityItem1; 
            	//SEARCH_RANGE = (int) this.dinosaur.getDistanceSqToEntity(entityItem);
            }
        }
        return entityItem;
    }
    
    /*
    private Vec3 getNearestItem(int SEARCH_RANGE)
    {
        List nearbyItems = this.dinosaur.worldObj.getEntitiesWithinAABB(EntityItem.class, this.dinosaur.boundingBox.expand(SEARCH_RANGE, SEARCH_RANGE, SEARCH_RANGE));
        Collections.sort(nearbyItems, this.targetSorter);
        Iterator iterateNearbyItems = nearbyItems.iterator();
        Vec3 itemlocation = null;

        while (iterateNearbyItems.hasNext())
        {
            EntityItem entityItem = (EntityItem) iterateNearbyItems.next();

            if (this.dinosaur.SelfType.FoodItemList.CheckItemById(entityItem.getEntityItem().itemID) || this.dinosaur.SelfType.FoodBlockList.CheckBlockById(entityItem.getEntityItem().itemID))
            {
            	Fossil.Console("targetItem: "+ entityItem);
            		this.targetItem = entityItem;            
            	
                itemlocation = Vec3.createVectorHelper(entityItem.posX, entityItem.posY, entityItem.posZ);
                break;
            }
        }

        return itemlocation;
    }
    */

}