package inventory;

import img.ImageToolkit;
import img.IntBitmap;

public enum InventoryItem 
{
	ALCHEMY_SHARD("AlchemyShard"),
	ALTERATION_SHARD("AlterationShard"),
	ARMOURERS_SCRAP("ArmourersScrap"),
	BLACKSMITHS_WHETSTONE("BlacksmithsWhetstone"),
	BLESSED_ORB("BlessedOrb"),
	CARTOGRAPHERS_CHISEL("CartographersChisel"),
	CHAOS_ORB("ChaosOrb"),
	CHROMATIC_ORB("ChromaticOrb"),
	DIVINE_ORB("DivineOrb"),
	EXALTED_ORB("ExaltedOrb"),
	GEMCUTTERS_PRISM("GemcuttersPrism"),
	GLASSBLOWERS_BAUBLE("GlassblowersBauble"),
	JEWELLERS_ORB("JewellersOrb"),
	ORB_OF_ALCHEMY("OrbOfAlchemy"),
	ORB_OF_ALTERATION("OrbOfAlteration"),
	ORB_OF_AUGMENTATION("OrbOfAugmentation"),
	ORB_OF_CHANCE("OrbOfChance"),
	ORB_OF_FUSING("OrbOfFusing"),
	ORB_OF_REGRET("OrbOfRegret"),
	ORB_OF_SCOURING("OrbOfScouring"),
	ORB_OF_TRANSMUTATION("OrbOfTransmutation"),
	PORTAL_SCROLL("PortalScroll"),
	REGAL_ORB("RegalOrb"),
	SCROLL_FRAGMENT("ScrollFragment"),
	SCROLL_OF_WISDOM("ScrollOfWisdom"),
	TRANSMUTATION_SHARD("TransmutationShard");
	
	IntBitmap icon_27x27;
	IntBitmap icon_28x27;
	IntBitmap icon_27x28;
	IntBitmap icon_28x28;
	
	private static final String DIR = "imglib/Items/";
	private static final String Ext1 = "27x27";
	private static final String Ext2 = "28x27";
	private static final String Ext3 = "27x28";
	private static final String Ext4 = "28x28";
	
	private String name;
	
	InventoryItem(String name)
	{
		this.name = name;
		if(name != null)
		{
			icon_27x27 = IntBitmap.getInstance(ImageToolkit.loadImage(DIR + Ext1 + "/" + name + Ext1 + ".bmp"));
			icon_28x27 = IntBitmap.getInstance(ImageToolkit.loadImage(DIR + Ext2 + "/" + name + Ext2 + ".bmp"));
			icon_27x28 = IntBitmap.getInstance(ImageToolkit.loadImage(DIR + Ext3 + "/" + name + Ext3 + ".bmp"));
			icon_28x28 = IntBitmap.getInstance(ImageToolkit.loadImage(DIR + Ext4 + "/" + name + Ext4 + ".bmp"));
		}
	}
	
	public IntBitmap getIcon(SizeExtension ext)
	{
		if(name != null)
		{
			if(ext == SizeExtension.EXT_27x27)
			{
				return icon_27x27;
			}
			else if(ext == SizeExtension.EXT_28x27)
			{
				return icon_28x27;
			}
			else if(ext == SizeExtension.EXT_27x28)
			{
				return icon_27x28;
			}
			else if(ext == SizeExtension.EXT_28x28)
			{
				return icon_28x28;
			}
			//System.out.println("Please enter a valid extension.");
			return null;
		}
		return null;
	}
}
