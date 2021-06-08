import java.util.ArrayList;
import java.util.List;

/**
 * Represents filtering image operations, which are a subset of all image operations that involve
 * the use of a kernel to filter each of the pixels in an image.
 */
abstract class FilterOperation implements ImageOperation {

  protected final double[][] kernel;

  /**
   * Create a filter operation containing a kernel to be used for its specific filter operation.
   *
   * @throws IllegalStateException if the kernel does not have odd dimensions
   */
  FilterOperation() throws IllegalStateException {
    this.kernel = this.getKernel();

    if (this.kernel.length % 2 == 0 || this.kernel[0].length % 2 == 0) {
      throw new IllegalStateException("Invalid kernel implementation");
    }
  }

  @Override
  public void apply(Image img) throws IllegalArgumentException {
    Image imgCopy = this.getImageCopy(ImageUtil.requireNonNull(img));
    for (int i = 0; i < img.getWidth(); i++) {
      for (int j = 0; j < img.getHeight(); j++) {
        this.filterPixel(imgCopy, i, j, this.getImageSection(img, i, j, this.kernel.length));
      }
    }
    this.replaceImage(img, imgCopy);
  }

  /**
   * Generate the specific kernel for its corresponding filter operation.
   *
   * @return the kernel to be used to filter an image
   */
  protected abstract double[][] getKernel();

  /**
   * Create a deep copy of the given image.
   *
   * @param img the image to be copied
   * @return a deep copy of the given image
   * @throws IllegalArgumentException if the given image is null
   */
  private Image getImageCopy(Image img) throws IllegalArgumentException {
    ImageUtil.requireNonNull(img);
    List<List<Pixel>> copy = new ArrayList<>();
    for (int i = 0; i < img.getWidth(); i++) {
      List<Pixel> rowCopy = new ArrayList<>();
      for (int j = 0; j < img.getHeight(); j++) {
        Pixel pixel = img.getPixelAt(i, j);
        rowCopy.add(new PixelImpl(pixel.getRed(), pixel.getGreen(), pixel.getBlue()));
      }
      copy.add(rowCopy);
    }
    return new ImageImpl(copy);
  }

  /**
   * Replaces the pixels of the original image with the pixels of the copy image.
   *
   * @param img     the image whose pixels are to be replaced
   * @param imgCopy the image whose pixels are to be copied
   * @throws IllegalArgumentException if either of the given images are null
   */
  private void replaceImage(Image img, Image imgCopy) throws IllegalArgumentException {
    ImageUtil.requireNonNull(img);
    ImageUtil.requireNonNull(imgCopy);
    for (int i = 0; i < img.getWidth(); i++) {
      for (int j = 0; j < img.getHeight(); j++) {
        img.replacePixel(i, j, imgCopy.getPixelAt(i, j));
      }
    }
  }

  /**
   * Grabs the section of pixels from an image surrounding the pixel at the given coordinates. The
   * obtained section matches the specified size of the filter kernel.
   *
   * @param img        the image from which the section is obtained
   * @param x          the x coordinate of the location of the desired pixel
   * @param y          the y coordinate of the location of the desired pixel
   * @param kernelSize the dimensions of the kernel for the specified filter
   * @return a section of pixels surrounding the specified pixel matching the size of the kernel
   * @throws IllegalArgumentException if the given image is null
   */
  private Pixel[][] getImageSection(Image img, int x, int y, int kernelSize)
      throws IllegalArgumentException {
    ImageUtil.requireNonNull(img);
    Pixel[][] section = new Pixel[kernelSize][kernelSize];
    int sectionX = 0;
    for (int i = x; i < x + kernelSize; i++) {
      int sectionY = 0;
      for (int j = y; j < y + kernelSize; j++) {
        Pixel pixel;
        try {
          pixel = img.getPixelAt(i - (kernelSize / 2), j - (kernelSize / 2));
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
          pixel = new PixelImpl(0, 0, 0);
        }
        section[sectionX][sectionY] = pixel;
        sectionY++;
      }
      sectionX++;
    }
    return section;
  }

  /**
   * Applies the filtering operation to each of the color values of the pixel at the given
   * coordinates using the specified kernel and the corresponding section of the image.
   * <p>The filtering operation is calculated by finding the product of each number in the kernel
   * with the corresponding RGB value in the image section, then calculating the total sum of these
   * values.</p>
   *
   * @param img     the image from which the section is obtained
   * @param x       the x coordinate of the location of the desired pixel
   * @param y       the y coordinate of the location of the desired pixel
   * @param section section of pixels surrounding the specified pixel
   * @throws IllegalArgumentException if the given image or section are null
   */
  private void filterPixel(Image img, int x, int y, Pixel[][] section)
      throws IllegalArgumentException {
    ImageUtil.requireNonNull(img);
    ImageUtil.requireNonNull(section);
    double red = 0;
    double green = 0;
    double blue = 0;
    for (int i = 0; i < this.kernel.length; i++) {
      for (int j = 0; j < this.kernel.length; j++) {
        red += this.kernel[i][j] * section[i][j].getRed();
        green += this.kernel[i][j] * section[i][j].getGreen();
        blue += this.kernel[i][j] * section[i][j].getBlue();
      }
    }
    img.replacePixel(x, y, new PixelImpl((int) red, (int) green, (int) blue));
  }
}