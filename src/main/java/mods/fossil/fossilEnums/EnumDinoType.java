package mods.fossil.fossilEnums;

import mods.fossil.Fossil;
import mods.fossil.entity.mob.*;
import net.minecraft.item.Item;

interface C
{
    public static final int NOTHING = 0;

    public static final int NO_FEEDER = 0 << 0;//Bits 0+1:	Dinos Can't use Feeder at all
    public static final int HERBIVORE = 1 << 0;//Bit 0:		Dino can use Herbivore Part of Feeder
    public static final int CARNIVORE = 2 << 0;//Bit 1: 	Dino can use Carnivore Part of Feeder
    public static final int HERB_CARN = 3 << 0;//Bits 0+1: 	Dinos can use Herbivore and Carnivore Part of Feeder

    public static final int MODEL = 1 << 2; //Bit 2: Dino is Modelable
    public static final int TAME = 1 << 3; //Bit 3: Dino is Tameable
    public static final int RIDE = 1 << 4; //Bit 4: Dino is Rideable
    public static final int CARRY = 1 << 5; //Bit 5: Dino can Carry Items
}
/*
 * Do not change the order of this enum. Only add to the end of it.
 */
public enum EnumDinoType
{
    //													C.MODEL	| C.TAME	| C.RIDE	| C.HERBIVORE/CARNIVORE | C.CARRY
    Triceratops(EntityTriceratops.class, 				C.MODEL | C.TAME  	| C.RIDE 	| C.HERBIVORE),
    Velociraptor(EntityVelociraptor.class,					      C.TAME  				| C.CARNIVORE),
    TRex(EntityTRex.class, 								C.MODEL | C.TAME    | C.RIDE  	| C.CARNIVORE),
    Pterosaur(EntityPterosaur.class, 					C.MODEL | C.TAME  			 	| C.CARNIVORE),
    Nautilus(EntityNautilus.class, 						C.NOTHING),
    Plesiosaur(EntityPlesiosaur.class, 					C.MODEL | C.TAME  	| C.RIDE 	| C.CARNIVORE),
    Mosasaurus(EntityMosasaurus.class, 					C.MODEL |  		  				  C.CARNIVORE),
    Stegosaurus(EntityStegosaurus.class, 				C.MODEL | C.TAME  				| C.HERBIVORE),
    Dilophosaurus(EntityDilophosaurus.class,					  C.TAME  	 			| C.CARNIVORE),
    Brachiosaurus(EntityBrachiosaurus.class,			C.MODEL | C.TAME  	| C.RIDE 	| C.HERBIVORE),
    Spinosaurus(EntitySpinosaurus.class, 				C.MODEL 						| C.CARNIVORE),
    Compsognathus(EntityCompsognathus.class,			C.MODEL | C.TAME				| C.CARNIVORE),
    Ankylosaurus(EntityAnkylosaurus.class,  			C.MODEL | C.TAME  	| C.RIDE  	| C.HERBIVORE),
    Pachycephalosaurus(EntityPachycephalosaurus.class, 	          C.TAME 				| C.HERBIVORE),
    Deinonychus(EntityDeinonychus.class,				C.MODEL | C.TAME  				| C.CARNIVORE),
    Gallimimus(EntityGallimimus.class,						      C.TAME	| C.RIDE	| C.HERB_CARN),
    Liopleurodon(EntityLiopleurodon.class, 				C.MODEL |  		  				  C.CARNIVORE),
    //Gastornis(EntityGastornis.class, 							  C.TAME	| C.RIDE	| C.CARNIVORE),
    ;


    private final Class dinoClass;

    public int Flags = 0;
    public Item OrderItem;
    public Item DropItem;
    public Item DNAItem;
    public Item EggItem;

    //List of the eatable Items with the FoodValue and HealingValue belonging to
    public DinoFoodItemList FoodItemList;

    //List of the eatable Blocks with the FoodValue and HealingValue belonging to
    public DinoFoodBlockList FoodBlockList;

    //List of the eatable Mobs with the FoodValue and HealingValue belonging to
    public DinoFoodMobList FoodMobList;

    //Starting width and increase of the Dino - No longer used
    public float Width0 = 0.5F;
    public float WidthInc = 0.4F;

    //Starting length and increase of the Dino - No longer used
    public float Length0 = 0.5F;
    public float LengthInc = 0.2F;

    //Starting height and increase of the dino - No longer used
    public float Height0 = 0.5F;
    public float HeightInc = 0.2F;

    //Age Limit of The Dino, arbitrary number, dinosaurs stop growing after hitting adult age.
    public int MaxAge = 999;
    //Age When Dino gets adult, starts Breeding, is Ridable...
    public int AdultAge = 6;
    //Age When Dino gets teen..
    public int TeenAge = 3;

    //Health of the Dino when hatched
    public double Health0 = 20;
    public double HealthMax = 20;

    //The attacking strength of the Dino when hatched
    public double Strength0 = 2;
    public double StrengthMax = 2;

    //The speed of the dino when hatched
    public double Speed0 = 0.25D;
    public double SpeedMax = 0.3D;

    //The Breeding time of the dinosaur, standard value 3000 ticks
    public int BreedingTicks = 3000;
    //Ticks the Dino needs for aging, standard 12000
    public int AgingTicks = 12000;

    //Hunger Limit of the Dino, standard is 100
    public int MaxHunger = 100;
    //The Level below which the dino starts looking for food. Standard is 0.8 [times MaxHunger]
    public float HungryLevel = 0.8f;

    //Base Experience Points of the Dino
    public float Exp0 = 1.0f;
    //Experience increase per day for the dino
    public float ExpInc = 0.2f;

    private float DinoSizeMin = 1.0f;
    private float DinoSizeMax = 10.0f;

    /**
     * Params: Class, Flags
     */
    private EnumDinoType(Class class0, int f0)
    {
        this.dinoClass = class0;
        this.Flags = f0;
        this.FoodItemList = new DinoFoodItemList();
        this.FoodBlockList = new DinoFoodBlockList();
        this.FoodMobList = new DinoFoodMobList();
    }

    /**
     * sets the OrderItem,DropItem,DNAItem and EggItem for the Dino
     */
    private void setItems(Item order)//,Item drop,Item dna,Item egg)
    {
        //this.DropItem = drop;
        //this.DNAItem = dna;
        //this.EggItem = egg;
        this.OrderItem = order;
    }

    /**
     * sets the TeenAge, AdultAge and MaxAge for the Dino
     */
    private void setAges(int Teen, int Adult, int Max)
    {
        if (Teen > 0)
        {
            this.TeenAge = Teen;
        }

        if (Adult > 0)
        {
            this.AdultAge = Adult;
        }

        if (Max > 0)
        {
            this.MaxAge = Max;
        }
    }

    private void setDinoSize(float SizeMin, float SizeMax)
    {
        this.DinoSizeMin = SizeMin;
        this.DinoSizeMax = SizeMax;
    }
    /**
     * No longer used.
     * sets the Dimensions for the Dino: starting width,width increase,same for length and height
     */
    private void setDimensions(float W0, float WInc, float L0, float LInc, float H0, float HInc)
    {
        if (W0 > 0)
        {
            this.Width0 = W0;
        }

        if (WInc > 0)
        {
            this.WidthInc = WInc;
        }

        if (L0 > 0)
        {
            this.Length0 = L0;
        }

        if (LInc > 0)
        {
            this.LengthInc = LInc;
        }

        if (H0 > 0)
        {
            this.Height0 = H0;
        }

        if (HInc > 0)
        {
            this.HeightInc = HInc;
        }
    }

    /**
     * sets the min/max values for Health, Attack Damage, Speed, and the Max Hunger Value
     */
    private void setProperties(double H0, double HMax, double Str0, double StrMax, double Sp0, double SpMax, int Hunger)
    {
        if (H0 > 0)
        {
            this.Health0 = H0;
        }

        if (Str0 > 0)
        {
            this.Strength0 = Str0;
        }

        if (Sp0 > 0)
        {
            this.Speed0 = Sp0;
        }
        
        if (HMax > 0) {
        	this.HealthMax = HMax;
        }
        
        if (StrMax > 0) {
        	this.StrengthMax = StrMax;
        }
        
        if (SpMax > 0) {
        	this.SpeedMax = SpMax;
        }

        if (Hunger > 0)
        {
            this.MaxHunger = Hunger;
        }
    }

    /**
     * sets the breeding time, the aging time and the hungry-level
     * Hungry level h:below h*MaxHunger, the dinos starts looking for food, below (1-h)*Maxhunger, the dino is starving
     */
    private void setAddProperties(int Breed, int Age, float HLevel)
    {
        if (Breed > 0)
        {
            this.BreedingTicks = Breed;
        }

        if (Age > 0)
        {
            this.AgingTicks = Age;
        }

        if (HLevel > 0)
        {
            this.HungryLevel = HLevel;
        }
    }

    /**
     * sets the breeding time, the aging time and the hungry-level
     * Hungry level h:below h*MaxHunger, the dinos starts looking for food, below (1-h)*Maxhunger, the dino is starving
     */
    private void setExperience(float E0, float EInc)
    {
        if (E0 > 0)
        {
            this.Exp0 = E0;
        }

        if (EInc > 0)
        {
            this.ExpInc = EInc;
        }
    }

    /**
     * Initializes all individual Dino values
     */
    public static void init()
    {
        Ankylosaurus.setItems(Item.stick);
        Ankylosaurus.setAges(5, 12, -1);
        Ankylosaurus.setProperties(25.0D, 70.0D, 2.0D, 9.0D, 0.25D, -1, 500);
        Ankylosaurus.setExperience(0.5F, 0.2F);
        Ankylosaurus.FoodItemList.addItem(EnumDinoFoodItem.Wheat);
        Ankylosaurus.FoodItemList.addItem(EnumDinoFoodItem.Melon);
        Ankylosaurus.FoodItemList.addItem(EnumDinoFoodItem.Apple);
        Ankylosaurus.FoodItemList.addItem(EnumDinoFoodItem.Bread);
        Ankylosaurus.FoodItemList.addItem(EnumDinoFoodItem.Potato);
        Ankylosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Ferns);
        Ankylosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Leaves);
        Ankylosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Palae);
        Ankylosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.RedFlower);
        Ankylosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.YellowFlower);


        Brachiosaurus.setItems(Item.stick);
        Brachiosaurus.setAges(9, 20, -1);
        Brachiosaurus.setProperties(20.0D, 110.0D, 2.0D, 18.0D, 0.25D, 0.2D, 600);
        Brachiosaurus.setExperience(0.6F, 0.15F);
        Brachiosaurus.FoodItemList.addItem(EnumDinoFoodItem.Sugar);
        Brachiosaurus.FoodItemList.addItem(EnumDinoFoodItem.Cookie);
        Brachiosaurus.FoodItemList.addItem(EnumDinoFoodItem.Apple);
        Brachiosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Ferns);
        Brachiosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Leaves);
        Brachiosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Palae);
        Brachiosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.RedFlower);
        Brachiosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.YellowFlower);
        
        
        Compsognathus.setItems(Item.bone);
        Compsognathus.setAges(1, 3, -1);
        Compsognathus.setProperties(2.0D, 10.0D, 1.0D, 3.0D, 0.25D, 0.30D, 100);
        Compsognathus.setExperience(0.2F, 0.2F);
        //Blocks
        //Items
        EnumDinoFoodItem.carnivoreItemPreset(Compsognathus);
        EnumDinoFoodItem.raptorItemPreset(Compsognathus);
        //Mobs	
        Compsognathus.FoodMobList.addMob(EnumDinoFoodMob.Chicken);
        Compsognathus.FoodMobList.addMob(EnumDinoFoodMob.Dodo);
        
        
        Deinonychus.setItems(Item.bone);
        Deinonychus.setAges(4, 10, -1);
        Deinonychus.setProperties(5.0D, 16.0D, 2.0D, 10.0D, 0.23D, 0.35D, 200);
        Deinonychus.setExperience(0.7F, 0.7F);
        //Blocks
        //Items
        EnumDinoFoodItem.carnivoreItemPreset(Deinonychus);
        EnumDinoFoodItem.raptorItemPreset(Deinonychus);
        //Mobs
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Compsognathus);
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Chicken);
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Dodo);
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Pig);
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Cow);
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Horse);
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Pachycephalosaurus);
        Deinonychus.FoodMobList.addMob(EnumDinoFoodMob.Gallimimus);
        
        
        Dilophosaurus.setItems(Item.bone);
        Dilophosaurus.setAges(4, 8, -1);
        Dilophosaurus.setProperties(4.0D, 20.0D, 1.0D, 8.0D, 0.25D, 0.4D, 400);
        Dilophosaurus.setExperience(1F, 1F);
        //Blocks
        //Items
        EnumDinoFoodItem.carnivoreItemPreset(Dilophosaurus);
        //Mobs
        Dilophosaurus.FoodMobList.addMob(EnumDinoFoodMob.Compsognathus);
        Dilophosaurus.FoodMobList.addMob(EnumDinoFoodMob.Chicken);
        Dilophosaurus.FoodMobList.addMob(EnumDinoFoodMob.Dodo);
        Dilophosaurus.FoodMobList.addMob(EnumDinoFoodMob.Pig);
        Dilophosaurus.FoodMobList.addMob(EnumDinoFoodMob.Cow);
        Dilophosaurus.FoodMobList.addMob(EnumDinoFoodMob.Horse);
        Dilophosaurus.FoodMobList.addMob(EnumDinoFoodMob.Pachycephalosaurus);

        
        Gallimimus.setItems(Item.stick);
        Gallimimus.setAges(4, 10, -1);
        Gallimimus.setProperties(4.0D, 20.0D, 2.0D, 6.0D, 0.3D, 0.65D, 300);
        Gallimimus.setExperience(0.7F, 0.7F);
        //Blocks
        Gallimimus.FoodBlockList.addblock(EnumDinoFoodBlock.Ferns);
        Gallimimus.FoodBlockList.addblock(EnumDinoFoodBlock.Palae);
        Gallimimus.FoodBlockList.addblock(EnumDinoFoodBlock.RedFlower);
        Gallimimus.FoodBlockList.addblock(EnumDinoFoodBlock.YellowFlower);        
        //Items
        EnumDinoFoodItem.carnivoreItemPreset(Gallimimus);
        Gallimimus.FoodItemList.addItem(EnumDinoFoodItem.Bread);
        Gallimimus.FoodItemList.addItem(EnumDinoFoodItem.Potato);
        Gallimimus.FoodItemList.addItem(EnumDinoFoodItem.Seeds);
        Gallimimus.FoodItemList.addItem(EnumDinoFoodItem.melonSeeds);
        
        
        Liopleurodon.setItems(null);
        Liopleurodon.setAges(6, 13, -1);
        Liopleurodon.setProperties(10.0D, 40.0D, 2.0D, 12.0D, 0.3D, 0.4D, 500);
        Liopleurodon.setExperience(1F, 1F);
        EnumDinoFoodItem.carnivoreItemPreset(Liopleurodon);
        Liopleurodon.FoodItemList.addItem(EnumDinoFoodItem.FishCooked);
        Liopleurodon.FoodItemList.addItem(EnumDinoFoodItem.FishRaw);
        Liopleurodon.FoodItemList.addItem(EnumDinoFoodItem.Nautilus);
        Liopleurodon.FoodItemList.addItem(EnumDinoFoodItem.Coelacanth);
        Liopleurodon.FoodMobList.addMob(EnumDinoFoodMob.Squid);
        Liopleurodon.FoodMobList.addMob(EnumDinoFoodMob.Nautilus);
        Liopleurodon.FoodMobList.addMob(EnumDinoFoodMob.Coelacanth);
        Liopleurodon.FoodMobList.addMob(EnumDinoFoodMob.Plesiosaur);
        Liopleurodon.FoodMobList.addMob(EnumDinoFoodMob.Cow);
        
        
        Mosasaurus.setItems(null);
        Mosasaurus.setAges(5, 11, -1);
        Mosasaurus.setProperties(12.0D, 35.0D, 2.0D, 9.0D, 0.3D, 0.35D, 500);
        Mosasaurus.setExperience(1F, 1F);
        EnumDinoFoodItem.carnivoreItemPreset(Mosasaurus);
        Mosasaurus.FoodItemList.addItem(EnumDinoFoodItem.FishCooked);
        Mosasaurus.FoodItemList.addItem(EnumDinoFoodItem.FishRaw);
        Mosasaurus.FoodItemList.addItem(EnumDinoFoodItem.Nautilus);
        Mosasaurus.FoodItemList.addItem(EnumDinoFoodItem.Coelacanth);
        Mosasaurus.FoodMobList.addMob(EnumDinoFoodMob.Squid);
        Mosasaurus.FoodMobList.addMob(EnumDinoFoodMob.Nautilus);
        Mosasaurus.FoodMobList.addMob(EnumDinoFoodMob.Coelacanth);
        Mosasaurus.FoodMobList.addMob(EnumDinoFoodMob.Plesiosaur);
        Mosasaurus.FoodMobList.addMob(EnumDinoFoodMob.Cow);
        
        
        Nautilus.setItems(null);
        Nautilus.setExperience(1.0F, 0F);
        
        
        Pachycephalosaurus.setItems(Item.stick);
        Pachycephalosaurus.setAges(4, 10, -1);
        Pachycephalosaurus.setProperties(4.0D, 20.0D, 2.0D, 7.0D, 0.25D, 0.4D, 300);
        Pachycephalosaurus.setExperience(0.5F, 0.2F);
        Pachycephalosaurus.FoodItemList.addItem(EnumDinoFoodItem.Wheat);
        Pachycephalosaurus.FoodItemList.addItem(EnumDinoFoodItem.Melon);
        Pachycephalosaurus.FoodItemList.addItem(EnumDinoFoodItem.Apple);
        Pachycephalosaurus.FoodItemList.addItem(EnumDinoFoodItem.Bread);
        Pachycephalosaurus.FoodItemList.addItem(EnumDinoFoodItem.Carrot);
        Pachycephalosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Ferns);
        Pachycephalosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Leaves);
        Pachycephalosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Palae);
        Pachycephalosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.RedFlower);
        Pachycephalosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.YellowFlower);
        
        
        Plesiosaur.setItems(Fossil.emptyShell);
        Plesiosaur.setAges(3, 7, -1);
        Plesiosaur.setProperties(10.0D, 20.0D, 1.0D, 5.0D, 0.2D, 0.3D, 500);
        Plesiosaur.setExperience(0.5F, 0.25F);
        Plesiosaur.FoodItemList.addItem(EnumDinoFoodItem.FishRaw);
        Plesiosaur.FoodItemList.addItem(EnumDinoFoodItem.FishCooked);
        Plesiosaur.FoodItemList.addItem(EnumDinoFoodItem.Sjl);
        Plesiosaur.FoodItemList.addItem(EnumDinoFoodItem.ChickenRaw);
        Plesiosaur.FoodMobList.addMob(EnumDinoFoodMob.Nautilus);
        Plesiosaur.FoodMobList.addMob(EnumDinoFoodMob.Chicken);
        Plesiosaur.FoodMobList.addMob(EnumDinoFoodMob.Dodo);   
        
        
        Pterosaur.setItems(Item.arrow);
        Pterosaur.setAges(4, 9, -1);
        Pterosaur.setProperties(6.0D, 24.0D, 1.0D, 6.0D, 0.15D, 0.2D, 200);
        Pterosaur.setExperience(0.3F, 0.3F);
        Pterosaur.FoodItemList.addItem(EnumDinoFoodItem.FishRaw);
        Pterosaur.FoodItemList.addItem(EnumDinoFoodItem.FishCooked);
        Pterosaur.FoodItemList.addItem(EnumDinoFoodItem.Sjl);
        Pterosaur.FoodItemList.addItem(EnumDinoFoodItem.ChickenRaw);
        Pterosaur.FoodMobList.addMob(EnumDinoFoodMob.Chicken);
        

        Spinosaurus.setItems(Fossil.skullStick);
        Spinosaurus.setAges(5, 12, -1);
        Spinosaurus.setProperties(15.0D, 60.0D, 2.0D, 14.0D, 0.25D, 0.30D, 500);
        Spinosaurus.setExperience(0F, 0.9F);
        //Blocks 
        //Items
        EnumDinoFoodItem.carnivoreItemPreset(Spinosaurus);
        Spinosaurus.FoodItemList.addItem(EnumDinoFoodItem.FishCooked);
        Spinosaurus.FoodItemList.addItem(EnumDinoFoodItem.FishRaw);
        //Mobs
        Spinosaurus.FoodMobList.addMob(EnumDinoFoodMob.Compsognathus);
        Spinosaurus.FoodMobList.addMob(EnumDinoFoodMob.Coelacanth);
        Spinosaurus.FoodMobList.addMob(EnumDinoFoodMob.Chicken);
        Spinosaurus.FoodMobList.addMob(EnumDinoFoodMob.Dodo);
        Spinosaurus.FoodMobList.addMob(EnumDinoFoodMob.Pig);
        Spinosaurus.FoodMobList.addMob(EnumDinoFoodMob.Cow);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Horse);
        Spinosaurus.FoodMobList.addMob(EnumDinoFoodMob.Plesiosaur);
        
        
        Stegosaurus.setItems(Item.stick);
        Stegosaurus.setAges(5, 12, -1);
        Stegosaurus.setProperties(12.0D, 35.0D, 2.0D, 9.0D, 0.2D, 0.25D, 500);
        Stegosaurus.setExperience(0.5F, 0.2F);
        Stegosaurus.FoodItemList.addItem(EnumDinoFoodItem.Wheat);
        Stegosaurus.FoodItemList.addItem(EnumDinoFoodItem.Melon);
        Stegosaurus.FoodItemList.addItem(EnumDinoFoodItem.Carrot);
        Stegosaurus.FoodItemList.addItem(EnumDinoFoodItem.Sugar);
        Stegosaurus.FoodItemList.addItem(EnumDinoFoodItem.Cookie);
        Stegosaurus.FoodItemList.addItem(EnumDinoFoodItem.Bread);
        Stegosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Ferns);
        Stegosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Leaves);
        Stegosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.Palae);
        Stegosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.RedFlower);
        Stegosaurus.FoodBlockList.addblock(EnumDinoFoodBlock.YellowFlower);
        

        Triceratops.setItems(Item.stick);
        Triceratops.setAges(5, 12, -1);
        Triceratops.setProperties(12.0D, 32.0D, 1.0D, 9.0D, 0.2D, 0.25D, 500);
        Triceratops.setExperience(0.5F, 0.2F);
        Triceratops.FoodItemList.addItem(EnumDinoFoodItem.Wheat);
        Triceratops.FoodItemList.addItem(EnumDinoFoodItem.Melon);
        Triceratops.FoodItemList.addItem(EnumDinoFoodItem.Apple);
        Triceratops.FoodItemList.addItem(EnumDinoFoodItem.Bread);
        Triceratops.FoodItemList.addItem(EnumDinoFoodItem.Potato);
        Triceratops.FoodBlockList.addblock(EnumDinoFoodBlock.Ferns);
        Triceratops.FoodBlockList.addblock(EnumDinoFoodBlock.Leaves);
        Triceratops.FoodBlockList.addblock(EnumDinoFoodBlock.Palae);
        Triceratops.FoodBlockList.addblock(EnumDinoFoodBlock.RedFlower);
        Triceratops.FoodBlockList.addblock(EnumDinoFoodBlock.YellowFlower);
        

        TRex.setItems(Fossil.skullStick);
        TRex.setAges(5, 12, -1);
        TRex.setProperties(15.0D, 60.0D, 2.0D, 14.0D, 0.25D, 0.45D, 500);
        TRex.setExperience(1F, 1F);
        //Blocks 
        //Items
        EnumDinoFoodItem.carnivoreItemPreset(TRex);
        //Mobs
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Pig);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Cow);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Horse);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Dilophosaurus);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Deinonychus);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Pachycephalosaurus);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Triceratops);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Gallimimus);
        TRex.FoodMobList.addMob(EnumDinoFoodMob.Mammoth);

        
        Velociraptor.setItems(Item.bone);
        Velociraptor.setAges(3, 6, -1);
        Velociraptor.setProperties(3.0D, 12.0D, 2.0D, 7.0D, 0.25D, 0.35D, 150);
        Velociraptor.setExperience(0.7F, 0.7F);
        //Blocks 
        //Items
        EnumDinoFoodItem.carnivoreItemPreset(Velociraptor);
        EnumDinoFoodItem.raptorItemPreset(Velociraptor);
        //Mobs
        Velociraptor.FoodMobList.addMob(EnumDinoFoodMob.Compsognathus);
        Velociraptor.FoodMobList.addMob(EnumDinoFoodMob.Chicken);        
        Velociraptor.FoodMobList.addMob(EnumDinoFoodMob.Dodo);
        Velociraptor.FoodMobList.addMob(EnumDinoFoodMob.Pig);  

        /*
        TerrorBird.setItems(Item.arrow);
        TerrorBird.setAges(4, 10, -1);
        TerrorBird.setProperties(4.0D, 20.0D, 2.0D, 6.0D, 0.3D, 0.65D, 300);
        TerrorBird.setExperience(0.2F, 0.2F);
        TerrorBird.FoodItemList.addItem(EnumDinoFoodItem.BeefRaw);
        TerrorBird.FoodMobList.addMob(EnumDinoFoodMob.Quagga);
        TerrorBird.FoodMobList.addMob(EnumDinoFoodMob.Horse);
        */
    }

    /**
     *      * takes an item and returns if it is a Dino DNA
     */
    public static boolean isDinoDNA(Item i0)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].DNAItem.itemID == i0.itemID)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * takes an Item and returns if it is a dino drop
     */
    public static boolean isDinoDrop(Item i0)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].DropItem.itemID == i0.itemID)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * takes an item, if it is a dinos dropItem or EggItem, it returns the corresponding DNA Item
     */
    public static Item getDNA(Item i0)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].DropItem.itemID == i0.itemID || values()[i].EggItem.itemID == i0.itemID)
            {
                return values()[i].DNAItem;
            }
        }

        return null;
    }

    /**
     * takes an item, if it is a dinos DNAItem or EggItem, it returns the corresponding Drop Item
     */
    public static Item getDrop(Item i0)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].DNAItem.itemID == i0.itemID || values()[i].EggItem.itemID == i0.itemID)
            {
                return values()[i].DropItem;
            }
        }

        return null;
    }

    /**
     * takes an item, if it is a dinos DNAItem or DropItem, it returns the corresponding Egg Item
     */
    public static Item getEgg(Item i0)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].DNAItem.itemID == i0.itemID || values()[i].DropItem.itemID == i0.itemID)
            {
                return values()[i].EggItem;
            }
        }

        return null;
    }
    /**
     * takes an drop-,dna- or eggitem and gives the index, -1 means not found
     */
    public static int getIndex(Item i0)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].DNAItem.itemID == i0.itemID || values()[i].DropItem.itemID == i0.itemID || values()[i].EggItem.itemID == i0.itemID)
            {
                return i;
            }
        }

        return -1;
    }
    public Class getDinoClass()
    {
        return this.dinoClass;
    }
    public boolean isModelable()
    {
        return (this.Flags & C.MODEL) != 0;
    }
    public boolean isTameable()
    {
        return (this.Flags & C.TAME) != 0;
    }
    public boolean isRideable()
    {
        return (this.Flags & C.RIDE) != 0;
    }
    public boolean canCarryItems()
    {
        return (this.Flags & C.CARRY) != 0;
    }
    public boolean useFeeder()
    {
        return (this.Flags & C.HERB_CARN) != 0;
    }
    public boolean isHerbivore()
    {
        return (this.Flags & C.HERBIVORE) != 0;
    }
    public boolean isCarnivore()
    {
        return (this.Flags & C.CARNIVORE) != 0;
    }
}