package snf.gui;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.alpine.bridge.ref.FieldsEditorParam;
import snf.par.SinfoniettaParam;
import snf.stc.LocalStatic;
import snf.stc.PagesCollection;

public abstract class SinfoniettaPiece {
  private final JScrollPane jScrollPane;
  private String previous_piece = "";

  public SinfoniettaPiece(SinfoniettaParam sinfoniettaParam) {
    String[] strings = PagesCollection.pieces().toArray(String[]::new);
    JList<String> jList = new JList<>(strings);
    FieldsEditorParam.GLOBAL.setFont(jList);
    jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionListener listSelectionListener = new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        int index = jList.getSelectedIndex();
        if (0 <= index) {
          sinfoniettaParam.piece = strings[index];
          LocalStatic.resourceLocator().trySave(sinfoniettaParam);
          if (!sinfoniettaParam.piece.equals(previous_piece)) {
            previous_piece = sinfoniettaParam.piece;
            setPagesContainer(sinfoniettaParam.piece);
          }
        }
      }
    };
    jList.addListSelectionListener(listSelectionListener);
    jScrollPane = new JScrollPane(jList);
  }

  public abstract void setPagesContainer(String piece);

  public JScrollPane jScrollPane() {
    return jScrollPane;
  }
}
