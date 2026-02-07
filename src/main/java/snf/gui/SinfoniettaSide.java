package snf.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import snf.stc.GlobalPtr;

public class SinfoniettaSide extends JPanel {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 40);
  public final JButton jButtonMenu = new JButton("\u21c5");
  public final JButton jButtonNavL = new JButton("\u2B89");
  public final JButton jButtonNavH = new JButton("\u2B8B");
  public final JButton jButtonPlay = new JButton("\u23f5");
  public final JButton jButtonPrev = new JButton("\u2397");
  public final JButton jButtonNext = new JButton("\u2398");
  public final JButton jButtonExit = new JButton("\u2716");

  private List<JComponent> allButtons() {
    List<JComponent> list = new ArrayList<>();
    list.addAll(List.of( //
        jButtonMenu, //
        jButtonNavL, //
        jButtonNavH, //
        jButtonPlay, //
        jButtonPrev, //
        jButtonNext));
    if (GlobalPtr.GLOBAL.undecorated)
      list.add(jButtonExit);
    return list;
  }

  public SinfoniettaSide() {
    super(new GridLayout(GlobalPtr.GLOBAL.undecorated ? 7 : 6, 1));
    allButtons().forEach(jComponent -> jComponent.setFont(FONT));
    allButtons().forEach(this::add);
  }
}
