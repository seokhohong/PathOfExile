package math;

public class Complex 
{
	private double real;
	private double imaginary;
	private double modulus;
	private double angle;
	
	/**
	 * Represents a complex number in the complex plane. A basis of representation is chosen (either a, b or r, theta),
	 * and then values of number in non-specified coordinates are automatically calculated and stored. This allows
	 * transformation between coordinates without designated methods.
	 * 
	 * @param arg1 RECT mode: real part; POLAR mode: modulus
	 * @param arg2 RECT mode: imaginary part; POLAR mode: angle
	 * @param basis Enum of basis: Either Basis.RECT or BASIS.POLAR
	 * 
	 * @author Jamison
	 */
	public Complex(double arg1, double arg2)
	{
		real = arg1;
		imaginary = arg2;
		calculateAngle();
	}
	public Complex(double arg1, double arg2, Basis basis)
	{
		if(basis == Basis.RECT)
		{
			real = arg1;
			imaginary = arg2;
			calculateAngle();
		}
		if(basis == Basis.POLAR)
		{
			modulus = arg1;
			angle = arg2;
			calculateReal();
			calculateImaginary();
		}
	}
	
	//Used to calculate non-given values upon instantiation of class Complex
	private double calculateAngle()			{return Math.atan2(imaginary, real);}
	private double calculateReal()			{return modulus*Math.cos(angle);}
	private double calculateImaginary()		{return modulus*Math.sin(angle);}

	//Getters
	public double getReal()					{return real;}
	public double getImaginary()			{return imaginary;}
	public double getAngle()				{return angle;}
	public double getModulus()				{return Math.sqrt(real*real + imaginary*imaginary);}
	
	//All complex arithmetic operations return a new complex number rather than modify the current number. This forces
	//new complex numbers to be created, rather than allow for confusion regarding the state of a changing Complex class.
	
	public Complex conjugate()
	{
		return new Complex(real, -imaginary, Basis.RECT);
	}
	public static Complex add(Complex u, Complex v)
	{
		return new Complex(u.getReal() + v.getReal(), u.getImaginary() + v.getImaginary(), Basis.RECT);
	}
	public static Complex multiply(Complex u, Complex v)
	{
		double newReal = u.getReal()*v.getReal() - u.getImaginary()*v.getImaginary();
		double newImaginary = u.getReal()*v.getImaginary() + u.getImaginary()*v.getReal();
		return new Complex(newReal, newImaginary, Basis.RECT);
	}
	public Complex normalize()
	{
		return new Complex(real / getModulus(), imaginary / getModulus());
	}
	@Override
	public String toString()
	{
		if(imaginary >= 0)
		{
			return real + " + " + imaginary + "i";
		}
		if(imaginary < 0)
		{
			return real + " - " + Math.abs(imaginary) + "i";
		}
		return "There was a problem finding real and imaginary parts.";
	}
}