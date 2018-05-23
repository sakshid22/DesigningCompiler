package cop5556sp18;

public class RuntimeFunctions {

	
		
		public static String className = "cop5556sp18/RuntimeFunctions";
		
		// sin function
		public static String sinSignature = "(F)F";
		
		public static float sin(float arg) {
			return (float) Math.sin((double) arg);
		}
		
		// cos function
		public static String cosSignature = "(F)F";
		
		public static float cos(float arg) {
			return (float) Math.cos((double) arg);
		}
		
		// atan function
		public static String atanSignature = "(F)F";
		
		public static float atan(float arg) {
			return (float) Math.atan((double) arg);
		}
		
		// log function
		public static String logSignature = "(F)F";
		
		public static float log(float arg) {
			return (float) Math.log((double) arg);
		}
		
		// abs function	- Integer
		public static String absISignature = "(I)I";
		
		public static int absI(int arg) {
			return Math.abs(arg);
		}
		
		// abs function	- Float
		public static String absFSignature = "(F)F";
			
		public static float absF(float arg) {
			return Math.abs(arg);
		}
		
		// power function
		public static String powSignature = "(FF)F";
		
		public static float pow(float arg0, float arg1) {
			return (float)Math.pow((double)arg0, (double)arg1);
		}
		
		// sleep function
		public static String sleepSignature = "(L)V";
		
		public static void sleep(long duration) {
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Added Functions
		public static String polar_aSignature = "(II)F";
		public static float polar_a(int x, int y) 
		{
			double  a =  Math.atan2(y, x);
			return (float) a;
		}
		
		public static String polar_rSignature = "(II)F";
		public static float polar_r(int x, int y)
		{
			double  r = Math.hypot(x,y);
			return (float)r;
		}
		
		public static String cart_xSignature = "(FF)I";
		public static int cart_x(float r, float theta)
		{
		     double y = r * Math.cos(theta);
		     return (int) y;
		}	
		public static String cart_ySignature = "(FF)I";
		public static int cart_y(float r, float theta) 
		{
			double y =  r * Math.sin(theta);
			return (int) y;
		}	
	}
	
	

