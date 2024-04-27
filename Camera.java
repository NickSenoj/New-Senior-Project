public class Camera {
	@SuppressWarnings("deprecation")
	public static void captureImage(String string) {
		try {
			Process process = Runtime.getRuntime().exec("fswebcam -r 1280x720 --no-banner test.jpg");
			process.waitFor();
			System.out.println("Picture taken successfully!");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}
}
