package com.x.base.core.neural.cnn.mnist;

public class MNISTImageTools {

	private MNISTImageTools() {
		// nothing
	}

//	public static void png(String imageFilePrefix, ConvolutionMatrix matrix) throws Exception {
//		Path dir = Paths.get("image", imageFilePrefix);
//		Files.createDirectories(dir);
//		for (int n = 0; n < matrix.number(); n++) {
//			WritableImage image = new WritableImage(matrix.height(), matrix.width());
//			PixelWriter writer = image.getPixelWriter();
//			for (int h = 0; h < matrix.height(); h++) {
//				for (int w = 0; w < matrix.width(); w++) {
//					int c = (int) matrix.get(n, 0, h, w);
//					writer.setColor(h, w, Color.rgb(c, c, c));
//				}
//			}
//			Path path = dir.resolve(String.format("%05d", n) + ".png");
//			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", path.toFile());
//		}
//	}

}
