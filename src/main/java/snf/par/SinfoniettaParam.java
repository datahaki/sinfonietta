package snf.par;

import java.util.List;

import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.ext.Integers;
import snf.stc.PagesCollection;

@ReflectionMarker
public class SinfoniettaParam {
  public String piece = "Bach_ViolinKeyboardSonatas.bwv1018_3";

  public void incr(int delta) {
    List<String> list = PagesCollection.pieces();
    int indexOf = list.indexOf(piece);
    if (0 <= indexOf) {
      int index = Integers.clip(0, list.size() - 1).applyAsInt(indexOf + delta);
      piece = list.get(index);
    }
  }
}
