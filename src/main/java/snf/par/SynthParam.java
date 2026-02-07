package snf.par;

import java.util.LinkedList;
import java.util.List;

import ch.alpine.bridge.lang.FriendlyFormat;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.midkit.MidiInstrument;
import ch.alpine.tensor.ext.ArgMin;
import ch.alpine.tensor.ext.EditDistance;

@ReflectionMarker
public class SynthParam {
  public static final List<SynthParam> LIST = new LinkedList<>();
  static {
    {
      LIST.add(silence());
      LIST.add(new SynthParam(MidiInstrument.GRAND_PIANO));
      LIST.add(new SynthParam(MidiInstrument.HAPSICHORD));
      LIST.add(new SynthParam(MidiInstrument.VIBRAPHONE));
      LIST.add(new SynthParam(MidiInstrument.MARIMBA));
      LIST.add(new SynthParam(MidiInstrument.CHURCH_ORGAN));
      LIST.add(new SynthParam(MidiInstrument.NYLON_GUITAR));
      LIST.add(new SynthParam(MidiInstrument.JAZZ_GUITAR));
      LIST.add(new SynthParam(MidiInstrument.FINGER_BASS));
      LIST.add(new SynthParam(MidiInstrument.VIOLIN));
      LIST.add(new SynthParam(MidiInstrument.ORCHESTRAL_HARP));
      LIST.add(new SynthParam(MidiInstrument.CHOIR_AAHS));
      LIST.add(new SynthParam(MidiInstrument.TROMBONE));
      LIST.add(new SynthParam(MidiInstrument.FRENCH_HORN));
      LIST.add(new SynthParam(MidiInstrument.OBOE));
      LIST.add(new SynthParam(MidiInstrument.ENGLISH_HORN));
      LIST.add(new SynthParam(MidiInstrument.BASSOON));
      LIST.add(new SynthParam(MidiInstrument.CLARINET));
      LIST.add(new SynthParam(MidiInstrument.FLUTE));
      LIST.add(new SynthParam(MidiInstrument.PAN_FLUTE));
    }
  }

  private static SynthParam silence() {
    return new SynthParam(PlayerParam.SILENCE);
  }

  public static final List<String> NAMES = LIST.stream().map(sp -> sp.name).toList();
  // ---
  public String name;
  public Boolean humanPlay = false;
  public MidiInstrument midiInstrument;

  public SynthParam(MidiInstrument midiInstrument) {
    this.name = FriendlyFormat.toCamelCase(midiInstrument.name());
    this.midiInstrument = midiInstrument;
  }

  private SynthParam(String name) {
    this.name = name;
    humanPlay = true;
    midiInstrument = null;
  }

  public static SynthParam getPlayer(String name) {
    int index = ArgMin.of(LIST.stream() //
        .map(v -> v.name) //
        .map(EditDistance.function(name)) //
        .toList());
    return LIST.get(index);
  }
}
