package snf.par;

import java.util.List;
import java.util.stream.Stream;

import ch.alpine.bridge.ref.ann.ReflectionMarker;

@ReflectionMarker
public class PlayalongParam {
  public final TempoParam tempoParam = new TempoParam();
  public MetroType metroType = MetroType.SELECTED;
  public final List<PlayerParam> playerParams;

  public PlayalongParam(int staffs) {
    playerParams = Stream.generate(() -> PlayerParam.silence()) //
        .limit(staffs) //
        .toList();
  }
}
