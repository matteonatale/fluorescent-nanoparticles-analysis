package org.nps.errors;

public class CouldNotFindBackgroundFilesException extends Exception {
    public CouldNotFindBackgroundFilesException() {
        super("could not find background files or directory");
    }
}
