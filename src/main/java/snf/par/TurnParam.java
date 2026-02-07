package snf.par;

import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

@ReflectionMarker
public class TurnParam {
  @FieldClip(min = "0.01", max = "0.1")
  @FieldSlider
  public Scalar ex = RealScalar.of(0.02);
  @FieldClip(min = "0", max = "1")
  @FieldSlider
  public Scalar ratio = RealScalar.ZERO;
}
