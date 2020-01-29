public class hw0 {
	public static int max(int[] a){
		int ans = a[0];
		for (int i=0;i < a.length; i+=1 ) {
			if (a[i]> ans) {
				ans = a[i];
			}
		}
		return ans;
	}
	
	public static boolean threesum(int[] a){
		for (int i=0;i < a.length ;i+=1 ) {
			for (int j=0;j< a.length ; j+=1) {
				for (int k=0;k< a.length ;k+=1 ) {
					if (a[i]+a[j]+a[k] == 0){
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean threeSumDistinct(int [] a){
		for (int i=0;i < a.length ;i+=1 ) {
			for (int j=0;j< a.length ; j+=1) {
				for (int k=0;k< a.length ;k+=1 ) {
					if (i == j || j == k || i == k){
						;
					}
					else if (a[i]+a[j]+a[k] == 0){
						return true;
					}
				}
			}
		}
		return false;
	}
	public static void main(String[] args){
		int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};
		int[] trueorfalse = new int[]{5, 1, 0, 3, 6};
    	
    	System.out.println(max(numbers));
    	System.out.println(threesum(trueorfalse));
    	System.out.println(threeSumDistinct(trueorfalse));

	}
}