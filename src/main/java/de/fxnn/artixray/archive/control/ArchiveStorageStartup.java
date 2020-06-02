package de.fxnn.artixray.archive.control;

import io.quarkus.runtime.Startup;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Startup
public class ArchiveStorageStartup {

  @Inject
  ThreadSafeDirectoryArchiveStorage archiveStorage;

  @PostConstruct
  public void postStartup() {
    archiveStorage.initializeStorage();
  }

}
