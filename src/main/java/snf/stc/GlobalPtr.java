// code by jph
package snf.stc;

import java.awt.Color;

import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.bridge.swing.LookAndFeels;
import ch.alpine.sonata.utl.MidiDevSelection;

// TODO class name!
@ReflectionMarker
public class GlobalPtr {
  public static final GlobalPtr GLOBAL = new GlobalPtr();

  public GlobalPtr() {
    IO.println("******************* CONTRUCTOR GLOBAL PTR");
// FIXME
    midiPutSelection.name = "eX [hw:3,0,0]";
  }

  // ---
  public String here = "abc";
  public LookAndFeels lookAndFeels = LookAndFeels.DARK;
  public final MidiDevSelection midiPutSelection = new MidiDevSelection(true);
  @FieldSlider(showValue = true)
  @FieldClip(min = "0", max = "127")
  public Integer metronomeVelocity = 32;
  public Boolean undecorated = true;
  public Color panelBackground = Color.WHITE;
  public Color labelBackground = new Color(255, 255, 0, 64);
  public Color pageShadow = new Color(0, 0, 0, 192);
  // ---
  public Boolean debugPrint = false;
}
