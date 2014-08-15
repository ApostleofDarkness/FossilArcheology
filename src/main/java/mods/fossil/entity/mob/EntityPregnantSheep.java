package mods.fossil.entity.mob;

import java.util.Random;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.fossil.Fossil;
import mods.fossil.client.LocalizationStrings;
import mods.fossil.client.gui.GuiPedia;
import mods.fossil.fossilEnums.EnumAnimalType;
import mods.fossil.fossilInterface.IViviparous;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntityPregnantSheep implements IViviparous, IExtendedEntityProperties
{
	public final static String PREGNANT_SHEEP_PROP = "EntityPregnantSheep";
	private final EntitySheep sheep;
	
    public int EmbryoProgress;
    public EnumAnimalType Embryo;
	private World worldObj;
	

    public EntityPregnantSheep(EntitySheep sheep)
    {
    	this.sheep = sheep;
    	this.EmbryoProgress = 0;
    	this.Embryo = null;
    }
    
    //Register properties.
    public static final void register(EntitySheep entity)
    {
    entity.registerExtendedProperties(EntityPregnantSheep.PREGNANT_SHEEP_PROP, new EntityPregnantSheep(entity));
    }
    
    //Return EntityPregnant* properties for Vanilla entity mobs.
    public static final EntityPregnantSheep get(EntitySheep entity)
    {
    return (EntityPregnantSheep) entity.getExtendedProperties(PREGNANT_SHEEP_PROP);
    }
    
 // Save any custom data that needs saving here
    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
    // We need to create a new tag compound that will save everything for our Extended Properties
    NBTTagCompound properties = new NBTTagCompound();

    // We only have 2 variables currently; save them both to the new tag
    properties.setInteger("EmbryoProgress", this.EmbryoProgress);
    if(this.Embryo != null)
    properties.setByte("Inside", (byte)this.Embryo.ordinal());

    /*
    Now add our custom tag to the player's tag with a unique name (our property's name). This will allow you to save multiple types of properties and distinguish between them. If you only have one type, it isn't as important, but it will still avoid conflicts between your tag names and vanilla tag names. For instance, if you add some "Items" tag, that will conflict with vanilla. Not good. So just use a unique tag name.
    */
    compound.setTag(PREGNANT_SHEEP_PROP, properties);
    }
    
 // Load whatever data you saved
    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
	    // Here we fetch the unique tag compound we set for this class of Extended Properties
	    NBTTagCompound properties = (NBTTagCompound) compound.getTag(PREGNANT_SHEEP_PROP);
	    // Get our data from the custom tag compound
	    
	    if (compound.hasKey("EmbryoProgress"))
	    {
	    	this.EmbryoProgress = properties.getInteger("EmbryoProgress");
	    }
	    
	    if (compound.hasKey("Inside"))
	    {
	        this.Embryo = EnumAnimalType.values()[properties.getByte("Inside")];
	    }
    }

	@Override
	public void init(Entity entity, World world) {}

	public void SetEmbryo(EnumAnimalType animalType)
    {
        this.Embryo = animalType;
    }
	
	public void setPedia()
    {
        Fossil.ToPedia = (Object)this;
    }
	
	@Override
	public void ShowPedia(GuiPedia p0) {
		if(this.Embryo != null) {
	        int quot = (int)Math.floor(((float)this.EmbryoProgress / (float)this.Embryo.GrowTime * 100.0F));
	
	        p0.reset();
	        p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_EMBRYO_INSIDE), false);
	        p0.AddStringLR(StatCollector.translateToLocal("pedia.embryo." + this.Embryo.toString()), false, 40, 90, 245);
	        p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_EMBRYO_GROWING), false);
	        p0.AddStringLR(String.valueOf(quot) + "/100", false);
		}
		else
		{
	        p0.reset();
	        p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_EMBRYO_INSIDE), false);
	        p0.AddStringLR(StatCollector.translateToLocal(LocalizationStrings.PEDIA_EMBRYO_GROWING), false);
		}
	}

}
