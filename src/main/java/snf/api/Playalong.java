package snf.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TimerTask;
import java.util.TreeMap;

import ch.alpine.midkit.DrumKit;
import ch.alpine.sonata.ScoreArray;
import ch.alpine.sonata.Voice;
import ch.alpine.sonata.enc.ly.PagesParam;
import ch.alpine.sonata.jnt.ScoreArrays;
import ch.alpine.sonata.mid.cmp.AudioConfiguration;
import ch.alpine.sonata.mid.cmp.AudioModel;
import ch.alpine.sonata.scr.Score;
import ch.alpine.sonata.seq.MidiSequence;
import ch.alpine.sonata.seq.PostponedSequence;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.io.Primitives;
import snf.par.MetroType;
import snf.par.PlayalongParam;
import snf.par.PlayerParam;
import snf.par.SynthParam;
import snf.stc.GlobalPtr;
import snf.stc.TpfLocal;
import sys.mat.IntegerMath;

public abstract class Playalong {
  public final TimerTask timerTask;
  public final int audio_offset_ticks;

  public Playalong(PieceSession pieceSession) throws Exception {
    PieceContainer pieceContainer = pieceSession.pieceContainer();
    Score score = pieceContainer.score();
    int last = 0;
    {
      ScoreArray scoreArray = ScoreArrays.create(score.voices, score.ticks());
      NavigableSet<Integer> hits = scoreArray.getHits();
      if (!hits.isEmpty())
        last = hits.last();
    }
    PagesParam pagesParam = pieceContainer.pagesParam();
    PlayalongParam playalongParam = pieceContainer.playalongParam();
    score.bpm = playalongParam.tempoParam.bpm();
    Iterator<Voice> iterator = score.voices.iterator();
    Tensor accum = Accumulate.of(Join.of(Tensors.vector(0), score.getStaffPartition()));
    List<Integer> list = Primitives.toListInteger(accum);
    TreeMap<Integer, Integer> naMap = new TreeMap<>();
    for (int index = 0; index < list.size(); ++index)
      naMap.put(list.get(index), index);
    int index = 0;
    while (iterator.hasNext()) {
      Voice voice = iterator.next();
      PlayerParam playerParam = playalongParam.playerParams.get(naMap.floorEntry(index).getValue());
      SynthParam synthParam = playerParam.getSynthParam();
      if (synthParam.humanPlay)
        iterator.remove();
      else {
        voice.midiInstrument = synthParam.midiInstrument;
        voice.press.put(0, playerParam.dynamic);
      }
      ++index;
    }
    AudioConfiguration audioConfiguration = new AudioConfiguration(score.voices, false, false);
    AudioModel audioModel = new AudioModel();
    {
      int page = pieceSession.getPage();
      int beg = pagesParam.measureAt(page) * score.measure();
      audio_offset_ticks = beg * AudioModel.FACTOR;
      audioModel.updateSelection(beg, score.ticks());
    }
    audioModel.figuredbass = false;
    MidiSequence midiSequence = audioModel.getSequence(score, audioConfiguration, PostponedSequence::new);
    {
      NavigableMap<Integer, DrumKit> navigableMap = new TreeMap<>();
      final int metronomeHint = score.getMeter().getMetronomeHint();
      final int delta = IntegerMath.divideExact(score.measure(), metronomeHint);
      if (playalongParam.metroType.equals(MetroType.SELECTED)) {
        {
          for (int ticks = 0; ticks < score.measure(); ticks += delta)
            navigableMap.put(ticks * AudioModel.FACTOR, DrumKit.METRONOME_CLICK);
        }
        ScoreArray scoreArray = ScoreArrays.create(score.voices, score.ticks());
        NavigableSet<Integer> hits = scoreArray.getHits();
        {
          for (int ticks = 0; ticks < last; ticks += delta)
            if (!hits.contains(ticks))
              navigableMap.put(ticks * AudioModel.FACTOR, DrumKit.METRONOME_CLICK);
        }
        for (int ticks = 0; ticks < last; ticks += score.measure())
          if (!hits.contains(ticks))
            navigableMap.put(ticks * AudioModel.FACTOR, DrumKit.METRONOME_BELL);
      } else //
      if (playalongParam.metroType.equals(MetroType.FULLTIME)) {
        for (int ticks = 0; ticks < last; ticks += delta)
          navigableMap.put(ticks * AudioModel.FACTOR, DrumKit.METRONOME_CLICK);
        for (int ticks = 0; ticks < last; ticks += score.measure())
          navigableMap.put(ticks * AudioModel.FACTOR, DrumKit.METRONOME_BELL);
      } else //
      if (playalongParam.metroType.equals(MetroType.HEAVYONE)) {
        for (int ticks = 0; ticks < last; ticks += score.measure())
          navigableMap.put(ticks * AudioModel.FACTOR, DrumKit.METRONOME_BELL);
      }
      NavigableMap<Integer, DrumKit> navigableMap2 = new TreeMap<>();
      for (Entry<Integer, DrumKit> entry : navigableMap.entrySet()) {
        int ticks = entry.getKey() - audio_offset_ticks;
        if (0 <= ticks)
          navigableMap2.put(ticks, entry.getValue());
      }
      midiSequence.setMetronome(navigableMap2);
      midiSequence.metronome_vel = GlobalPtr.GLOBAL.metronomeVelocity;
    }
    TpfLocal.INSTANCE.getMidiPut().startSequence(midiSequence.getSequence());
    timerTask = new TimerTask() {
      @Override
      public void run() {
        Scalar page = pagesParam.pageOf(RationalScalar.of( //
            audio_offset_ticks + TpfLocal.INSTANCE.getMidiPut().getTickPosition(), //
            score.measure() * AudioModel.FACTOR));
        setPage(page);
        if (!TpfLocal.INSTANCE.getMidiPut().isRunning()) {
          stop();
          stopped();
        }
      }
    };
  }

  public abstract void setPage(Scalar page);

  public void stop() {
    timerTask.cancel();
    TpfLocal.INSTANCE.getMidiPut().stopSequencers();
  }

  public abstract void stopped();
}
