package grafika.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import org.springframework.stereotype.Service;

import grafika.model.FilterSelection;

@Service
public class FiltersService {

	public static int[] boxFilter = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	public static int[] gaussianBlur = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
	public static int[] sharpenFilter = { -1, -2, -1, -2, 16, -2, -1, -2, -1 };
	public static int[] laplacianFilter = { 0, -1, 0, -1, 4, -1, 0, -1, 0 };
	public static int[] embossFilter = { 2, 0, -0, 0, -1, 0, 0, 0, -1 };
	public static int[] sobelHorizontalFilter = { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
	public static int[] sobelVerticalFilter = { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
	public static int[] motionBlurFilter = { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
	public static int[] findHorizontalEdges = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	public static int[] findVerticalEdges = { 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 4, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0 };
	public static int[] highPassFilter = { 0, -1, -1, -1, 0, -1, 2, -4, 2, -1, -1, -4, 13, -4, -1, -1, 2, -4, 2, -1, 0, -1, -1, -1, 0 };

	public BufferedImage filter(BufferedImage image, int[] matrix, FilterSelection filterSelection) {

		int startX = filterSelection.getStartX();
		int startY = filterSelection.getStartY();
		int width = filterSelection.getWidth() != 0 ? filterSelection.getWidth() : image.getWidth();
		int height = filterSelection.getHeight() != 0 ? filterSelection.getHeight() : image.getHeight();

		int factor = calculateFactor(matrix);
		int matrixWidth = (int) Math.sqrt(matrix.length);
		// bitmapa z ktorej odczytujemy piksele
		BufferedImage tempBitmap = deepCopy(image);

		// obliczenie zmiennych pomocniczych
		int pixelPerSide = matrixWidth / 2;
		int imageWidthWithoutPixelPerSide = image.getWidth() - pixelPerSide;
		int imageHeightWithoutPixelPerSide = image.getHeight() - pixelPerSide;

		// wartosci poszczegolnych kolorow piksela
		int redPixel = 0;
		int bluePixel = 0;
		int greenPixel = 0;
		Color color;

		// wspolzedne piksela
		int imageX = 0;
		int imageY = 0;

		// petla po wszystkich pikselach bitmapy
		for (int y = startY; y < height; y++) {
			for (int x = startX; x < width; x++) {
				// wyzerowanie wartosci pikseli
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
				redPixel = redPixel > 255 ? 255 : redPixel;
				bluePixel = bluePixel > 255 ? 255 : bluePixel;
				greenPixel = greenPixel > 255 ? 255 : greenPixel;
				redPixel = redPixel < 0 ? 0 : redPixel;
				bluePixel = bluePixel < 0 ? 0 : bluePixel;
				greenPixel = greenPixel < 0 ? 0 : greenPixel;
				image.setRGB(x, y, new Color(redPixel, greenPixel, bluePixel).getRGB());
			}
		}
		return image;
	}

	public BufferedImage blend(BufferedImage original, BufferedImage filtered, int percent) {

		BufferedImage tempBitmap = deepCopy(original);
		int startX = 0;
		int startY = 0;
		int width = original.getWidth();
		int height = original.getHeight();

		// wartosci poszczegolnych kolorow piksela
		int redPixel = 0;
		int bluePixel = 0;
		int greenPixel = 0;
		Color originalColor;
		Color filteredColor;

		// petla po wszystkich pikselach bitmapy
		for (int y = startY; y < height; y++) {
			for (int x = startX; x < width; x++) {
				// wyzerowanie wartosci pikseli
				redPixel = 0;
				bluePixel = 0;
				greenPixel = 0;

				originalColor = new Color(original.getRGB(x, y));
				filteredColor = new Color(filtered.getRGB(x, y));
				redPixel += (percent * filteredColor.getRed() + ((100 - percent) * originalColor.getRed()))/100;
				bluePixel += (percent * filteredColor.getBlue() + ((100 - percent) * originalColor.getBlue()))/100;
				greenPixel += (percent * filteredColor.getGreen() + ((100 - percent) * originalColor.getGreen()))/100;
				
				redPixel = redPixel > 255 ? 255 : redPixel;
				bluePixel = bluePixel > 255 ? 255 : bluePixel;
				greenPixel = greenPixel > 255 ? 255 : greenPixel;
				redPixel = redPixel < 0 ? 0 : redPixel;
				bluePixel = bluePixel < 0 ? 0 : bluePixel;
				greenPixel = greenPixel < 0 ? 0 : greenPixel;
				tempBitmap.setRGB(x, y, new Color(redPixel, greenPixel, bluePixel).getRGB());
			}
		}
		return tempBitmap;
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
