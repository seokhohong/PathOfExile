package inventory;

public enum SizeExtension 
{
	EXT_27x27,
	EXT_28x27,
	EXT_27x28,
	EXT_28x28;

	public static SizeExtension getExt(int pixelWidth, int pixelHeight)
	{
		if(pixelWidth == 27 && pixelHeight == 27)
		{
			return SizeExtension.EXT_27x27;
		}
		else if(pixelWidth == 28 && pixelHeight == 27)
		{
			return SizeExtension.EXT_28x27;
		}
		else if(pixelWidth == 27 && pixelHeight == 28)
		{
			return SizeExtension.EXT_27x28;
		}
		else if(pixelWidth == 28 && pixelHeight == 28)
		{
			return SizeExtension.EXT_28x28;
		}
		return null;
	}
}
