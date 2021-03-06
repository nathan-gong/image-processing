package model.image;

import java.util.HashMap;
import java.util.Map;
import model.image.programmatic.ProgrammaticCreator;
import model.operation.BlurOperation;
import model.operation.ImageOperation;
import model.ImageUtil;
import model.operation.MonochromeOperation;
import model.operation.Operations;
import model.operation.SepiaOperation;
import model.operation.SharpenOperation;

/**
 * Represents the model of an Image modification program. The model processes Images provided by the
 * user and modifies it with operations such as filters chosen by the user.
 */
public class ImageProcessingModelImpl implements ImageProcessingModel {

  private final Map<Operations, ImageOperation> operationsMap;

  /**
   * Creates an image processing model that is able to handle image files and perform operations on
   * Image objects.
   */
  public ImageProcessingModelImpl() {
    this.operationsMap = this.getOperations();
  }

  @Override
  public Image applyOperation(Image img, Operations o) throws IllegalArgumentException {
    ImageUtil.requireNonNull(o);
    ImageUtil.requireNonNull(img);
    ImageOperation operation = ImageUtil.requireNonNull(this.operationsMap.getOrDefault(o, null));
    return operation.apply(img);
  }

  @Override
  public Image createProgrammaticImage(ProgrammaticCreator creator)
      throws IllegalArgumentException {
    ImageUtil.requireNonNull(creator);
    return creator.create();
  }

  /**
   * Produces a Map of the operations on Images that are usable in this model implementation. The
   * keys are the names of the operations and the values are the corresponding function objects to
   * complete the operations.
   *
   * @return a map of the operations on Images that this model implementation can complete
   */
  private Map<Operations, ImageOperation> getOperations() {
    Map<Operations, ImageOperation> operations = new HashMap<>();
    operations.put(Operations.SEPIA, new SepiaOperation());
    operations.put(Operations.MONOCHROME, new MonochromeOperation());
    operations.put(Operations.SHARPEN, new SharpenOperation());
    operations.put(Operations.BLUR, new BlurOperation());
    return operations;
  }
}
