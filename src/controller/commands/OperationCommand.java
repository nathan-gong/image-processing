package controller.commands;

import java.util.Scanner;
import model.ImageUtil;
import model.image.ImageLayerModel;
import model.operation.Operations;

/**
 * Abstract class that facilitates the execution of filter operations such as blur, sharpen, sepia,
 * and monochrome when one of these operations is requested.
 */
abstract class OperationCommand implements Command {

  /**
   * Retrieves the Operation specific to the image processing operation command.
   *
   * @return the Operation enum corresponding to the operation command
   */
  protected abstract Operations getOperationName();

  @Override
  public void execute(Scanner scanner, ImageLayerModel model) throws IllegalArgumentException {
    ImageUtil.requireNonNull(scanner);
    ImageUtil.requireNonNull(model);

    model.applyOperation(model.getCurrentLayer().getImage(), this.getOperationName());
  }
}
