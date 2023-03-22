package org.nps;

import ij.ImagePlus;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.measure.Measurements;
import ij.plugin.filter.Analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        var start = new Date();

        var files = checkFiles("/Users/matteo/develop/uni/ece-nps-analysis/data");

        for (var file : files)
            analyzeParticle(
                    "/Users/matteo/develop/uni/ece-nps-analysis",
                    Integer.parseInt(file.getName().split("_")[1])
            );

        var end = new Date();
        System.out.println((end.getTime() - start.getTime()));
    }

    public static List<File> checkFiles(String path) {
        System.out.println("Analyzing files...\n");
        System.out.println("Checking file names");

        var totalFiles = new File(path + "/total").listFiles();
        var filteredTotalFiles = checkForWrongFileNames(
                totalFiles,
                "\\bparticle_[0-9]{1,}_total.tif\\b"
        );

        var backgroundFiles = new File(path + "/background").listFiles();
        var filteredBackgroundFiles = checkForWrongFileNames(
                backgroundFiles,
                "\\bparticle_[0-9]{1,}_bck.tif\\b"
        );

        assert backgroundFiles != null;
        assert totalFiles != null;

        System.out.println("Look for missing files");
        checkCorresponding(filteredTotalFiles, filteredBackgroundFiles, "Corresponding background file not found.");
        checkCorresponding(filteredBackgroundFiles, filteredTotalFiles, "Corresponding total file not found.");

        System.out.println();

        return filteredTotalFiles;
    }

    public static List<File> checkForWrongFileNames(File[] files, String regex) {
        return Arrays.stream(files).filter((file -> {

            if (!file.getName().matches(regex)) {
                System.out.println("File " + file.getName() + " - skipped. File name not recognized.");
                return false;
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName() + " - skipped. Directories are not allowed.");
                return false;
            }

            return true;
        })).collect(Collectors.toList());
    }

    public static void checkCorresponding(List<File> first, List<File> second, String error) {
        var fileToRemove = new ArrayList<File>();

        for (File firstFile : first) {
            boolean hasCorresponding = false;
            var t = Integer.parseInt(firstFile.getName().split("_")[1]);
            for (File secondFile : second) {
                var b = Integer.parseInt(secondFile.getName().split("_")[1]);
                if (t == b) {
                    hasCorresponding = true;
                    break;
                }
            }

            if (hasCorresponding) continue;

            System.out.println("File " + firstFile.getName() + " - skipped. " + error);
            fileToRemove.add(firstFile);
        }

        for (var file : fileToRemove)
            first.remove(file);
    }

    public static void analyzeParticle(String path, int particle) {
        System.out.println("Analyzing particle " + particle);

        var total = analyzeStack(path + "/data/total", "particle_" + particle + "_total.tif");
        var background = analyzeStack(path + "/data/background", "particle_" + particle + "_bck.tif");

        total.save(path + "/out/particle_" + particle + "_total.csv");
        background.save(path + "/out/particle_" + particle + "_bck.csv");

        var sub = new ResultsTable();

        var totalMean = total.getColumn("Mean");
        var backMean = background.getColumn("Mean");
        var intDenTot = total.getColumn("IntDen");
        var areaTot = total.getColumn("Area");

        for (int i = 0; i < total.size(); i++) {
            sub.addRow();
            sub.addValue("Mean", totalMean[i] - backMean[i]);
            sub.addValue("IntDen", intDenTot[i] - backMean[i] * areaTot[i]);
        }

        sub.save(path + "/out/particle_" + particle + "_results.csv");
    }

    public static ResultsTable analyzeStack(String path, String fileName) {
        Opener opener = new Opener();
        int measurements = Measurements.AREA |
                Measurements.MEAN |
                Measurements.STD_DEV |
                Measurements.MIN_MAX |
                Measurements.INTEGRATED_DENSITY;

        var rtTotal = new ResultsTable();
        var stackTotal = opener
                .openTiff(path, fileName)
                .getStack();

        for (int i = 0; i < stackTotal.size(); i++) {
            var particleByteProcessor = stackTotal.getProcessor(i + 1).convertToByte(false);
            particleByteProcessor.setThreshold(0, 255);

            var p = new ImagePlus();
            p.setImage(particleByteProcessor.createImage());

            var analyzer = new Analyzer(p, measurements, rtTotal);
            analyzer.measure();
        }

        rtTotal.deleteColumn("RawIntDen"); // TODO: ask what column has to be deleted between IntDen and RawIntDen
        return rtTotal;
    }
}