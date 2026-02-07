package snf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import ch.alpine.bridge.awt.LazyMouse;
import ch.alpine.bridge.awt.LazyMouseListener;
import ch.alpine.bridge.awt.WindowBounds;
import ch.alpine.bridge.awt.WindowClosed;
import ch.alpine.bridge.ref.FieldsEditorParam;
import ch.alpine.bridge.ref.util.DialogFieldsEditor;
import ch.alpine.bridge.swing.CheckBoxIcon;
import ch.alpine.bridge.swing.UIManagerInt;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Floor;
import snf.api.PieceSession;
import snf.api.Playalong;
import snf.gui.SinfoniettaComponent;
import snf.gui.SinfoniettaPiece;
import snf.gui.SinfoniettaSide;
import snf.par.SinfoniettaParam;
import snf.stc.GlobalPtr;
import snf.stc.LocalStatic;
import snf.stc.TpfLocal;

public class Sinfonietta implements LazyMouseListener {
  private static final int MARGIN_TOP = 32;
  private static final int WIDTH = 1920;
  private static final int HEIGHT = 1080 - MARGIN_TOP;
  // ---
  private final JFrame jFrame = new JFrame();
  private final SinfoniettaParam sinfoniettaParam = LocalStatic.resourceLocator().tryLoad(new SinfoniettaParam());
  private PieceSession pieceSession = PieceSession.create(sinfoniettaParam.piece);
  private final JPanel jContentPane = new JPanel(new BorderLayout());
  private final SinfoniettaComponent sinfoniettaComponent = new SinfoniettaComponent();
  private final Timer timer = new Timer();
  boolean showList = false;
  private final SinfoniettaPiece sinfoniettaPiece = new SinfoniettaPiece(sinfoniettaParam) {
    @Override
    public void setPagesContainer(String string) {
      sinfoniettaParam.piece = string;
      pieceSession = PieceSession.create(string);
      sinfoniettaComponent.set(pieceSession);
    }
  };
  private final SinfoniettaSide sinfoniettaSide = new SinfoniettaSide();

  public Sinfonietta() {
    WindowClosed.runs(jFrame, timer::cancel);
    WindowClosed.runs(jFrame, this::stopPlay);
    WindowClosed.runs(jFrame, TpfLocal.INSTANCE::shutdownMidiPut);
    WindowClosed.runs(jFrame, () -> LocalStatic.resourceLocator().trySave(sinfoniettaParam));
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    sinfoniettaComponent.set(pieceSession);
    new LazyMouse(this).addListenersTo(sinfoniettaComponent);
    jContentPane.add(sinfoniettaPiece.jScrollPane(), BorderLayout.NORTH);
    jContentPane.add(sinfoniettaSide, BorderLayout.WEST);
    jContentPane.add(sinfoniettaComponent, BorderLayout.CENTER);
    {
      sinfoniettaSide.jButtonMenu.addActionListener(_ -> setShowList(!showList));
      sinfoniettaSide.jButtonNavL.addActionListener(_ -> {
        stopPlay();
        sinfoniettaParam.incr(-1);
        sinfoniettaPiece.setPagesContainer(sinfoniettaParam.piece);
      });
      sinfoniettaSide.jButtonNavH.addActionListener(_ -> {
        stopPlay();
        sinfoniettaParam.incr(+1);
        sinfoniettaPiece.setPagesContainer(sinfoniettaParam.piece);
      });
      // sinfoniettaSide.jButtonConf.addActionListener(actionEvent -> configDialog());
      sinfoniettaSide.jButtonPlay.addActionListener(_ -> togglePlay());
      sinfoniettaSide.jButtonExit.addActionListener(_ -> jFrame.dispose());
      sinfoniettaSide.jButtonPrev.addActionListener(_ -> {
        if (pieceSession.decrPage())
          sinfoniettaComponent.repaint();
      });
      sinfoniettaSide.jButtonNext.addActionListener(_ -> {
        if (pieceSession.incrPage())
          sinfoniettaComponent.repaint();
      });
    }
    jFrame.setContentPane(jContentPane);
    setShowList(false);
    jFrame.setUndecorated(GlobalPtr.GLOBAL.undecorated);
    WindowBounds.persistent(jFrame, LocalStatic.resourceLocator().properties(getClass()));
    jFrame.setSize(new Dimension(WIDTH, HEIGHT));
    jFrame.setVisible(true);
  }

  private void setShowList(boolean show) {
    showList = show;
    sinfoniettaPiece.jScrollPane().setVisible(show);
    jContentPane.validate();
  }

  private Playalong playalong = null;

  public void togglePlay() {
    if (Objects.isNull(playalong) || //
        !TpfLocal.INSTANCE.getMidiPut().isRunning())
      try {
        setShowList(false);
        stopPlay();
        // ---
        playalong = new Playalong(pieceSession) {
          @Override
          public void setPage(Scalar _page) {
            Scalar page = Floor.FUNCTION.apply(_page);
            Scalar ratio = _page.subtract(page);
            if (pieceSession.setPage(Scalars.intValueExact(page), ratio))
              sinfoniettaComponent.repaint();
          }

          @Override
          public void stopped() {
            // TODO SNF not clear what caused stop
            // if (pagesContainer.setPage(0, RealScalar.ZERO))
            // sinfoniettaComponent.repaint();
            sinfoniettaSide.setVisible(true);
            playalong = null;
          }
        };
        timer.schedule(playalong.timerTask, 0, 400);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    else
      stopPlay();
    // ---
    sinfoniettaSide.setVisible(Objects.isNull(playalong));
    jContentPane.validate();
  }

  private void stopPlay() {
    if (Objects.nonNull(playalong)) {
      playalong.stop();
      playalong = null;
      sinfoniettaComponent.repaint();
    }
  }

  @Override
  public void lazyClicked(MouseEvent mouseEvent) {
    setShowList(false);
    if (TpfLocal.INSTANCE.getMidiPut().isRunning()) {
      sinfoniettaSide.setVisible(!sinfoniettaSide.isVisible());
    } else {
      sinfoniettaSide.setVisible(true);
    }
  }

  static void main() {
    UIManager.put(UIManagerInt.ScrollBar_width.key(), FieldsEditorParam.GLOBAL.componentMinSize);
    GlobalPtr.GLOBAL.lookAndFeels.updateComponentTreeUI();
    FieldsEditorParam.GLOBAL.textFieldFont = new Font(Font.DIALOG, Font.PLAIN, 28);
    FieldsEditorParam.GLOBAL.textFieldFont_override = true;
    FieldsEditorParam.GLOBAL.componentMinSize_override = true;
    FieldsEditorParam.GLOBAL.componentMinSize = 50;
    FieldsEditorParam.GLOBAL.checkBoxParam.override = false;
    FieldsEditorParam.GLOBAL.checkBoxParam.icon = CheckBoxIcon.METRO;
    FieldsEditorParam.GLOBAL.checkBoxParam.size = 36;
    // MidiPut midiPut = TpfLocal.INSTANCE.getMidiPut();
    Optional<GlobalPtr> optional = DialogFieldsEditor.block(null, GlobalPtr.GLOBAL, "Configuration");
    optional.ifPresent(LocalStatic.resourceLocator()::trySave);
    if (optional.isPresent()) //
    {
      GlobalPtr.GLOBAL.lookAndFeels.updateComponentTreeUI();
      new Sinfonietta();
    }
  }
}
