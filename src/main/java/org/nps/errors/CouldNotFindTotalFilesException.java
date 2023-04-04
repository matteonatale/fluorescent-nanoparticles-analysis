package org.nps.errors;

public class CouldNotFindTotalFilesException extends Exception {
    public CouldNotFindTotalFilesException() {
        super("could not find total files or directory");
    }
}
