package zmaster587.libVulpes.util;

public class MathVulpes {
	public static double log2 = Math.log(2);
	
	public static double log2(double in) {return Math.log(in)/log2;}
	
	public static int greatestCommonFactor(int num[]) {
		int largestGCF = greatestCommonFactor(num[0], num[1]);
		
		for(int i = 2; i < num.length; i++){
			int gcf = greatestCommonFactor(num[i], largestGCF);
			if(largestGCF < gcf)
				largestGCF = gcf;
		}
		
		return largestGCF;
	}
	
	public static int greatestCommonFactor(int numA, int numB) {
		if(numB == 0) return numA;
		return greatestCommonFactor(numB, numA % numB);
	}
}
