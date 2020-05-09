package de.fxnn.artixray.archive.boundary;

public interface ArchiveResolver {

  /**
   * Resolves an archive from the given Maven archive coordinates.
   * <p/>
   * An implementation must provide some mechanism to download the archive, unpack its contents and thus provide files
   * from the archive. However, the exact means of doing so is up to the implementation.
   * @param coordinates
   * @return
   */
  Archive resolve(String coordinates);

}
