package snf.api;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ch.alpine.bridge.awt.ScalableImage;
import ch.alpine.bridge.io.ResourceLocator;
import ch.alpine.sonata.enc.ScoreIO;
import ch.alpine.sonata.enc.ly.PagesParam;
import ch.alpine.sonata.scr.Score;
import ch.alpine.tensor.ext.Cache;
import snf.par.PlayalongParam;
import snf.stc.PagesCollection;

/** Image dimensions
 * 1920 x 1048 */
public class PieceContainer {
  private static final Cache<String, PieceContainer> CACHE = Cache.of(PieceContainer::new, 4);

  public static PieceContainer get(String tag) {
    return CACHE.apply(tag);
  }

  // ---
  private final String tag;
  private final ResourceLocator resourceLocator;
  private final List<ScalableImage> images = new ArrayList<>();
  private final Dimension dimension = new Dimension(1920, 1048);
  private final PagesParam pagesParam;
  private final PlayalongParam playalongParam;

  private PieceContainer(String tag) {
    this.tag = tag;
    resourceLocator = new ResourceLocator(PagesCollection.pieceFolder(tag));
    Score score = ScoreIO.read(resourceLocator.resolve("score.nvm"));
    playalongParam = resourceLocator.tryLoad(new PlayalongParam(score.getStaffPartition().length()));
    pagesParam = resourceLocator.tryLoad(new PagesParam());
    int count = 1;
    BufferedImage canvas = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = canvas.createGraphics();
    while (true) {
      Path file = resourceLocator.resolve("score-page" + count + ".png");
      if (Files.isRegularFile(file)) {
        try (InputStream is = Files.newInputStream(file)) {
          BufferedImage bufferedImage = ImageIO.read(is);
          dimension.width = bufferedImage.getWidth();
          dimension.height = bufferedImage.getHeight();
          graphics.drawImage(bufferedImage, 0, 0, null);
          images.add(new ScalableImage(bufferedImage));
        } catch (IOException exception) {
          exception.printStackTrace();
        }
        ++count;
      } else
        break;
    }
    graphics.dispose();
  }

  public Dimension getDimension() {
    return dimension;
  }

  public String tag() {
    return tag;
  }

  public Score score() {
    return ScoreIO.read(resourceLocator.resolve("score.nvm"));
  }

  public PlayalongParam playalongParam() {
    return playalongParam;
  }

  public ScalableImage getImage(int index) {
    return images.get(index);
  }

  public int pageCount() {
    return images.size();
  }

  public PagesParam pagesParam() {
    return pagesParam;
  }

  public void manifestPlayalongParam() {
    resourceLocator.trySave(playalongParam);
  }

  public void manifestPagesParam() {
    resourceLocator.trySave(pagesParam);
  }
}
