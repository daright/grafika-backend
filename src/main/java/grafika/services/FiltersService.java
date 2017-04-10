package grafika.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import org.springframework.stereotype.Service;

@Service
public class FiltersService {

	public static int[] gaussianBlur = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
	public static int[] boxFilter = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	public static int[] sharpenFilter = { -1, -2, -1, -2, 16, -2, -1, -2, -1 };
	public static int[] laplacianFilter = { 0, -1, 0, -1, 4, -1, 0, -1, 0 };
	public static int[] embossFilter = { 2, 0, -0, 0, -1, 0, 0, 0, -1 };
	public static int[] sobelHorizontalFilter = { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
	public static int[] sobelVerticalFilter = { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
	public static int[] motionBlurFilter = { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
	public static int[] findHorizontalEdges = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	public static int[] findVerticalEdges = { 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 4, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0 };
	public static int[] highPassFilter = { 0, -1, -1, -1, 0, -1, 2, -4, 2, -1, -1, -4, 13, -4, -1, -1, 2, -4, 2, -1, 0, -1, -1, -1, 0 };

	public BufferedImage filter(BufferedImage image, int[] matrix) {

		int factor = calculateFactor(matrix);
		int matrixWidth = (int) Math.sqrt(matrix.length);
		// bitmapa z ktorej odczytujemy piksele
		BufferedImage tempBitmap = deepCopy(image);

		// obliczenie zmiennych pomocniczych
		int pixelPerSide = matrixWidth / 2;
		int imageWidthWithoutPixelPerSide = image.getWidth() - pixelPerSide;
		int imageHeightWithoutPixelPerSide = image.getHeight() - pixelPerSide;

		// wartosci poszczegolnych kolorow piksela
		long rgbValue = 0;
		int redPixel = 0;
		int bluePixel = 0;
		int greenPixel = 0;
		Color color;

		// wspolzedne piksela
		int imageX = 0;
		int imageY = 0;

		// petla po wszystkich pikselach bitmapy
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				// wyzerowanie wartosci pikseli
				rgbValue = 0;
				redPixel = 0;
				bluePixel = 0;
				greenPixel = 0;

				int xWithoutPixelPerSide = x - pixelPerSide;
				int yWithoutPixelPerSide = y - pixelPerSide;

				// petla po masce filtra
				for (int i = 0; i < matrixWidth; ++i) {
					for (int j = 0; j < matrixWidth; ++j) {
						imageX = (xWithoutPixelPerSide + i + image.getWidth()) % image.getWidth();

						imageY = (yWithoutPixelPerSide + j + image.getHeight()) % image.getHeight();

						if ((imageX >= imageWidthWithoutPixelPerSide) && x <= pixelPerSide) {
							imageX = 0;
						}

						if ((imageX <= pixelPerSide) && x >= (imageWidthWithoutPixelPerSide)) {
							imageX = image.getWidth() - 1;
						}

						if ((imageY >= imageHeightWithoutPixelPerSide) && y <= pixelPerSide) {
							imageY = 0;
						}

						if ((imageY <= pixelPerSide) && y >= (imageHeightWithoutPixelPerSide)) {
							imageY = image.getHeight() - 1;
						}

						color = new Color(tempBitmap.getRGB(imageX, imageY));
						redPixel += matrix[i * matrixWidth + j] * color.getRed();
						bluePixel += matrix[i * matrixWidth + j] * color.getBlue();
						greenPixel += matrix[i * matrixWidth + j] * color.getGreen();
					}
				}

				if (factor != 0) {
					redPixel /= factor;
					bluePixel /= factor;
					greenPixel /= factor;
				}
				// image.setRGB(x, y, (int) rgbValue);
				image.setRGB(x, y, new Color(redPixel, greenPixel, bluePixel).getRGB());
			}
		}
		return image;
	}

	private BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	private int calculateFactor(int[] matrix) {
		int factor = 0;
		for (int i = 0; i < matrix.length; i++) {
			factor += matrix[i];
		}
		return factor;
	}
}
