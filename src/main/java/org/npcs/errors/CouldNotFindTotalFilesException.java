package org.npcs.errors;

public class CouldNotFindTotalFilesException extends Exception {
    public CouldNotFindTotalFilesException() {
        super("could not find total files or directory");
    }
}
