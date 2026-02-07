package snf.stc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public enum PagesCollection {
  ;
  public static Path pages_root() {
    return LocalStatic.getBranch("pages");
  }

  public static Path pieceFolder(String piece) {
    return pages_root().resolve(piece);
  }

  public static List<String> pieces() {
    return Stream.of(pages_root().toFile().listFiles()) //
        .filter(f -> PagesCollection.isValid(f.toPath())) //
        .map(File::getName) //
        .sorted() //
        .toList();
  }

  private static boolean isValid(Path folder) {
    return Files.isDirectory(folder) //
        && Files.isRegularFile(folder.resolve("score.nvm")) //
        && Files.isRegularFile(folder.resolve("score-page1.png")) //
        && Files.isRegularFile(folder.resolve("PagesParam.properties")) //
    ;
  }
}
