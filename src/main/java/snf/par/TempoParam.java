package snf.par;

import ch.alpine.bridge.lang.SI;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.Scalar;

@ReflectionMarker
public class TempoParam {
  @FieldSlider(showValue = true)
  @FieldClip(min = "20", max = "160")
  public Integer bpm = 80;

  public Scalar bpm() {
    return SI.PER_MINUTE.quantity(bpm);
  }
}
