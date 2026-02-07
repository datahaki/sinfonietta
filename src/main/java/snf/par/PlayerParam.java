package snf.par;

import java.util.List;

import ch.alpine.bridge.ref.ann.FieldSelectionCallback;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.sonata.Dynamic;

@ReflectionMarker
public class PlayerParam {
  public static final String SILENCE = "Silence";

  public static PlayerParam silence() {
    return new PlayerParam();
  }

  // ---
  @FieldSelectionCallback("titles")
  public String name = SILENCE;
  public Dynamic dynamic = Dynamic.MF;

  public List<String> titles() {
    return SynthParam.LIST.stream() //
        .map(v -> v.name) //
        .toList();
  }

  public SynthParam getSynthParam() {
    return SynthParam.getPlayer(name);
  }

  public boolean isSilence() {
    return name.equals(SILENCE);
  }
}
