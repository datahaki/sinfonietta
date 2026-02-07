package snf.api;

import java.util.Optional;

import ch.alpine.bridge.awt.ScalableImage;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import snf.par.TurnParam;

public class PieceSession {
  public static PieceSession create(String piece) {
    return new PieceSession(PieceContainer.get(piece));
  }

  // ---
  private final PieceContainer pieceContainer;
  private final TurnParam turnParam = new TurnParam();
  private int page = 0;

  public PieceSession(PieceContainer pieceContainer) {
    this.pieceContainer = pieceContainer;
  }

  public PieceContainer pieceContainer() {
    return pieceContainer;
  }

  public int getPage() {
    return page;
  }

  public ScalableImage getImage() {
    return pieceContainer.getImage(page);
  }

  public boolean decrPage() {
    return setPage(page - 1, RealScalar.ZERO);
  }

  public boolean incrPage() {
    return setPage(page + 1, RealScalar.ZERO);
  }

  public Optional<ScalableImage> getImageNext() {
    return Optional.ofNullable(page + 1 < pieceContainer.pageCount() //
        ? pieceContainer.getImage(page + 1)
        : null);
  }

  public boolean setPage(int _page, Scalar ratio) {
    if (0 <= _page && _page < pieceContainer.pageCount()) {
      boolean changed = false;
      changed |= page != _page;
      changed |= !turnParam.ratio.equals(ratio);
      page = _page;
      turnParam.ratio = ratio;
      return changed;
    }
    return false;
  }

  public TurnParam turnParam() {
    return turnParam;
  }
}
