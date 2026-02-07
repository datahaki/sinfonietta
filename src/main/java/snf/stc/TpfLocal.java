package snf.stc;

import java.util.Optional;
import java.util.function.Predicate;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;

import ch.alpine.midkit.MidiDevices;
import ch.alpine.midkit.put.DirectMidiPut;
import ch.alpine.midkit.put.EmptyMidiPut;
import ch.alpine.midkit.put.MidiPut;
import ch.alpine.midkit.put.MidiPutPredicate;

public enum TpfLocal {
  INSTANCE;

  private MidiPut midiPut;

  private TpfLocal() {
    Predicate<Info> predicate = GlobalPtr.GLOBAL.midiPutSelection.getPredicate();
    Optional<Info> optional = MidiDevices.getList(MidiPutPredicate.INSTANCE, 100).stream().filter(predicate).findFirst();
    if (optional.isPresent())
      try {
        MidiDevice midiDevice = MidiSystem.getMidiDevice(optional.orElseThrow());
        midiPut = DirectMidiPut.of(midiDevice);
      } catch (Exception e) {
        System.err.println("fail midi put");
        e.printStackTrace();
        midiPut = EmptyMidiPut.INSTANCE;
      }
    else
      midiPut = EmptyMidiPut.INSTANCE;
  }

  public void shutdownMidiPut() {
    try {
      midiPut.stopSequencers();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    try {
      midiPut.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    midiPut = EmptyMidiPut.INSTANCE;
  }

  public MidiPut getMidiPut() {
    return midiPut;
  }
}
