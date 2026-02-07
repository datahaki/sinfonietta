// code by jph
package snf.stc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.alpine.bridge.io.ResourceLocator;
import ch.alpine.tensor.ext.HomeDirectory;
import snf.Sinfonietta;

public enum LocalStatic {
  ;
  private static enum LazyHolder {
    INSTANCE;

    private final Path userdocs = HomeDirectory.Documents.resolve(Sinfonietta.class.getSimpleName());
    public final ResourceLocator resourceLocator = new ResourceLocator(userdocs.resolve("properties"));

    private LazyHolder() {
      try {
        Files.createDirectories(userdocs);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static Path getBranch(String string) {
    Path file = LazyHolder.INSTANCE.userdocs.resolve(string);
    try {
      Files.createDirectories(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }

  public static ResourceLocator resourceLocator() {
    return LazyHolder.INSTANCE.resourceLocator;
  }
}
