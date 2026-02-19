// code by jph
package snf.stc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.alpine.bridge.io.ResourceLocator;
import ch.alpine.tensor.ext.HomeDirectory;
import snf.Sinfonietta;

public enum LocalStatic {
  INSTANCE;

  private final Path userdocs = HomeDirectory.Database.resolve(Sinfonietta.class.getSimpleName());
  public final ResourceLocator resourceLocator = new ResourceLocator(userdocs.resolve("properties"));

  private LocalStatic() {
    try {
      Files.createDirectories(userdocs);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Path getBranch(String string) {
    Path path = INSTANCE.userdocs.resolve(string);
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return path;
  }

  public static ResourceLocator resourceLocator() {
    return INSTANCE.resourceLocator;
  }
}
