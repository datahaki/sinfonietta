package snf.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JPanel;

import ch.alpine.bridge.awt.LazyMouse;
import ch.alpine.bridge.awt.LazyMouseListener;
import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.awt.ScalableImage;
import ch.alpine.bridge.fig.CbbFit;
import ch.alpine.bridge.gfx.AffineTransforms;
import ch.alpine.bridge.ref.FieldsEditorParam;
import ch.alpine.bridge.ref.util.DialogFieldsEditor;
import ch.alpine.bridge.swing.SpinnerMenu;
import ch.alpine.sonata.Dynamic;
import ch.alpine.sonata.enc.ly.PagesParam;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.sca.Round;
import snf.api.PieceContainer;
import snf.api.PieceSession;
import snf.par.MetroType;
import snf.par.PlayalongParam;
import snf.par.PlayerParam;
import snf.par.SynthParam;
import snf.par.TempoParam;
import snf.par.TurnParam;
import snf.stc.GlobalPtr;
import snf.stc.TpfLocal;
import sys.gui.Shape2DCollection;
import sys.gui.Shape2DObject;

public class SinfoniettaComponent extends JPanel implements LazyMouseListener {
  public static record SO_Synth(int index) {
  }

  public static record SO_Dynamic(int index) {
  }

  public static record SO_Tempo() {
    // ---
  }

  public static record SO_Metro() {
    // ---
  }

  public static record SO_Pages() {
    // ---
  }

  private PieceSession pieceSession = null;
  private final Shape2DCollection shape2dCollection = new Shape2DCollection();
  private boolean bckgnd = false;
  private int repaint_count = 0;

  public SinfoniettaComponent() {
    // setBackground(GlobalPtr.GLOBAL.panelBackground);
    setOpaque(true); // for all look and feels
    new LazyMouse(this).addListenersTo(this);
  }

  Tensor matrix = null;

  @Override
  protected synchronized void paintComponent(Graphics graphics) {
    Graphics2D g2d = (Graphics2D) graphics;
    shape2dCollection.clear();
    Timing timing = Timing.started();
    // System.out.println("paintComp "+System.currentTimeMillis());
    super.paintComponent(graphics);
    List<String> list = new LinkedList<>();
    // ---
    Dimension dimPanel = getSize();
    if (Objects.nonNull(pieceSession)) {
      PieceContainer pagesContainer = pieceSession.pieceContainer();
      Dimension dimImage = pagesContainer.getDimension();
      final int w = dimImage.width;
      final int h = dimImage.height;
      Tensor a = Tensors.vector(w, h);
      Tensor b = Tensors.vector(dimPanel.width, dimPanel.height);
      Optional<Tensor> optional = CbbFit.inside(a, b);
      if (optional.isPresent()) {
        Tensor c = optional.orElseThrow();
        int n_w = Round.intValueExact(c.Get(0));
        int n_h = Round.intValueExact(c.Get(1));
        Rectangle rectangle = new Rectangle( //
            (dimPanel.width - n_w) / 2, //
            (dimPanel.height - n_h) / 2, n_w, n_h);
        // AffLin affLinY = new AffLin(rectangle.y, n_h, h);
        matrix = Array.zeros(3, 3);
        matrix.set(RationalScalar.of(n_w, w), 0, 0);
        matrix.set(RationalScalar.of(n_h, h), 1, 1);
        matrix.set(RealScalar.of(rectangle.x), 0, 2);
        matrix.set(RealScalar.of(rectangle.y), 1, 2);
        matrix.set(RealScalar.ONE, 2, 2);
        // System.out.println(affLinY.approx(100));
        // rectangle = new Rectangle(0, 0, w, h);
        // ---
        Optional<ScalableImage> optional2 = pieceSession.getImageNext();
        if (optional2.isPresent()) {
          list.add(rectangle.width + " x " + rectangle.height);
          graphics.drawImage(optional2.orElseThrow().getScaledInstance(rectangle.width, rectangle.height), rectangle.x, rectangle.y, null);
          TurnParam turnParam = pieceSession.turnParam();
          int ext = (int) (rectangle.width * turnParam.ex.number().floatValue());
          int x = (int) ((rectangle.width + 2 * ext) * turnParam.ratio.number().floatValue()) - ext;
          int _x = Math.max(0, x);
          graphics.setClip(rectangle.x + _x, rectangle.y, rectangle.width - _x, rectangle.height);
          graphics.drawImage(pieceSession.getImage().getScaledInstance(rectangle.width, rectangle.height), rectangle.x, rectangle.y, null);
          if (pieceSession.getPage() == 0) {
            Graphics2D _g = (Graphics2D) g2d.create();
            _g.transform(AffineTransforms.of(matrix));
            PagesParam pagesParam = pagesContainer.pagesParam();
            _g.setFont(FieldsEditorParam.GLOBAL.textFieldFont);
            FontMetrics fontMetrics = _g.getFontMetrics();
            final int ascent = fontMetrics.getAscent();
            final int height = fontMetrics.getHeight();
            RenderQuality.setQuality(_g);
            PlayalongParam playalongParam = pagesContainer.playalongParam();
            bckgnd = !TpfLocal.INSTANCE.getMidiPut().isRunning();
            {
              int piy = pagesParam.piy - 60;
              {
                int pix = 120;
                Rectangle r1 = new Rectangle(pix, piy - ascent, 150, height);
                suggestArea(_g, r1, new SO_Metro());
                _g.setColor(Color.DARK_GRAY);
                _g.setFont(FieldsEditorParam.GLOBAL.textFieldFont);
                _g.drawString(playalongParam.metroType.toString().toLowerCase(), pix, piy);
              }
              {
                int pix = 320;
                Rectangle r1 = new Rectangle(pix, piy - ascent, 90, height);
                suggestArea(_g, r1, new SO_Tempo());
                _g.setColor(Color.DARK_GRAY);
                _g.setFont(FieldsEditorParam.GLOBAL.textFieldFont);
                _g.drawString("" + playalongParam.tempoParam.bpm, pix, piy);
              }
            }
            List<PlayerParam> playerParams = playalongParam.playerParams;
            int piy = pagesParam.piy;
            for (int index = 0; index < playerParams.size(); ++index) {
              if (GlobalPtr.GLOBAL.debugPrint) {
                _g.setColor(Color.DARK_GRAY);
                _g.drawLine(20, piy, 400, piy);
              }
              boolean isSilence = playerParams.get(index).isSilence();
              {
                int pix = 20;
                Rectangle r1 = new Rectangle(pix, piy - ascent, 220, height);
                suggestArea(_g, r1, new SO_Synth(index));
                _g.setColor(isSilence ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                _g.setFont(FieldsEditorParam.GLOBAL.textFieldFont);
                _g.drawString(playerParams.get(index).name, pix, piy);
              }
              if (!isSilence) {
                int pix = 250;
                Rectangle r1 = new Rectangle(pix, piy - ascent, 60, height);
                suggestArea(_g, r1, new SO_Dynamic(index));
                _g.setColor(Color.DARK_GRAY);
                _g.setFont(FieldsEditorParam.GLOBAL.textFieldFont.deriveFont(Font.ITALIC));
                _g.drawString(playerParams.get(index).dynamic.name().toLowerCase(), pix, piy);
              }
              piy += pagesParam.dy;
            }
            _g.dispose();
          }
          graphics.setClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
          Color color_lo = new Color(255, 255, 255, 0);
          Color color_hi = GlobalPtr.GLOBAL.pageShadow;
          Paint paint = new LinearGradientPaint(rectangle.x + x - ext, 0, rectangle.x + x + ext, 0, new float[] { 0f, 0.5f, 1f },
              new Color[] { color_lo, color_hi, color_lo });
          ((Graphics2D) graphics).setPaint(paint);
          graphics.fillRect(rectangle.x + x - ext, rectangle.y, 2 * ext, rectangle.height);
          graphics.setClip(null);
        } else {
          graphics.drawImage(pieceSession.getImage().getScaledInstance(rectangle.width, rectangle.height), rectangle.x, rectangle.y, null);
        }
      }
    }
    if (GlobalPtr.GLOBAL.debugPrint) {
      graphics.setColor(Color.RED);
      Rectangle r1 = new Rectangle(0, 0, 50, 50);
      g2d.draw(r1);
      shape2dCollection.add(new Shape2DObject(r1, new SO_Pages()));
    }
    if (GlobalPtr.GLOBAL.debugPrint && false) {
      RenderQuality.setQuality(g2d);
      graphics.setColor(Color.DARK_GRAY);
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
      list.add(Integer.toString(++repaint_count));
      list.add(String.format("paintComp %6.3f[s]", timing.seconds()));
      int piy = 0;
      for (String line : list)
        graphics.drawString(line, 2, piy += 15);
    }
  }

  private void suggestArea(Graphics2D _g, Rectangle r1, Object object) {
    if (bckgnd) {
      _g.setColor(GlobalPtr.GLOBAL.labelBackground);
      _g.fill(r1);
      shape2dCollection.add(new Shape2DObject(r1, object));
    }
  }

  public void set(PieceSession pieceSession) {
    this.pieceSession = pieceSession;
    repaint();
  }

  @Override
  public void lazyClicked(MouseEvent mouseEvent) {
    if (Objects.isNull(matrix))
      return;
    Point point = mouseEvent.getPoint();
    Tensor tensor = LinearSolve.of(matrix, Tensors.vector(point.x, point.y, 1));
    Optional<Shape2DObject> optional = shape2dCollection.findFirst(new Point2D.Double( //
        tensor.Get(0).number().doubleValue(), //
        tensor.Get(1).number().doubleValue()));
    if (optional.isPresent()) {
      Shape2DObject shape2dObject = optional.orElseThrow();
      Object object = shape2dObject.object();
      if (object instanceof SO_Metro _) {
        PlayalongParam playalongParam = pieceSession.pieceContainer().playalongParam();
        // playalongParam.metroType;
        SpinnerMenu<MetroType> spinnerMenu = new SpinnerMenu<>( //
            Arrays.asList(MetroType.values()), playalongParam.metroType, s -> s.toString().toLowerCase(), FieldsEditorParam.GLOBAL.textFieldFont, true);
        spinnerMenu.addSpinnerListener(name -> {
          playalongParam.metroType = name;
          pieceSession.pieceContainer().manifestPlayalongParam();
          repaint();
        });
        spinnerMenu.showRight(this, shape2dObject.shape().getBounds());
      }
      if (object instanceof SO_Tempo)
        editTempo();
      if (object instanceof SO_Synth so_Synth) {
        int index = so_Synth.index();
        List<PlayerParam> list = pieceSession.pieceContainer().playalongParam().playerParams;
        SpinnerMenu<String> spinnerMenu = new SpinnerMenu<>(SynthParam.NAMES, list.get(index).name, s -> s, FieldsEditorParam.GLOBAL.textFieldFont, true);
        spinnerMenu.addSpinnerListener(name -> {
          list.get(index).name = name;
          pieceSession.pieceContainer().manifestPlayalongParam();
          repaint();
        });
        spinnerMenu.showRight(this, shape2dObject.shape().getBounds());
      }
      if (object instanceof SO_Dynamic so_Dynamic) {
        int index = so_Dynamic.index();
        List<PlayerParam> list = pieceSession.pieceContainer().playalongParam().playerParams;
        SpinnerMenu<Dynamic> spinnerMenu = new SpinnerMenu<>(Arrays.asList(Dynamic.values()), list.get(index).dynamic, s -> s.name().toLowerCase(),
            FieldsEditorParam.GLOBAL.textFieldFont, true);
        spinnerMenu.addSpinnerListener(name -> {
          list.get(index).dynamic = name;
          pieceSession.pieceContainer().manifestPlayalongParam();
          repaint();
        });
        spinnerMenu.showRight(this, shape2dObject.shape().getBounds());
      }
      if (object instanceof SO_Pages)
        editPagesParam();
    }
  }

  private void editTempo() {
    PieceContainer pieceContainer = pieceSession.pieceContainer();
    PlayalongParam playalongParam = pieceContainer.playalongParam();
    Optional<TempoParam> optional = DialogFieldsEditor.block(this, playalongParam.tempoParam, pieceContainer.tag());
    optional.ifPresent(_ -> pieceContainer.manifestPlayalongParam());
    optional.ifPresent(_ -> repaint());
  }

  private void editPagesParam() {
    PieceContainer pieceContainer = pieceSession.pieceContainer();
    Optional<PagesParam> optional = DialogFieldsEditor.block(this, pieceContainer.pagesParam(), pieceContainer.tag());
    optional.ifPresent(_ -> pieceContainer.manifestPagesParam());
    optional.ifPresent(_ -> repaint());
  }
}
