package org.nps;

import ij.ImagePlus;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.measure.Measurements;
import ij.plugin.filter.Analyzer;

import java.io.IOException;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {
        var start = new Date();
        analyze_with_thresh();
        var end = new Date();
        System.out.println((end.getTime() - start.getTime()));
    }

    public static void first() {
        //        Opener opener = new Opener();

//        new ImageJ();
//        var particle = IJ.openImage("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/src/main/java/org/nps/test/320-1.tif");
//        var particle_bg = IJ.openImage("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/src/main/java/org/nps/test/320-2.tif");
//
//        IJ.run(particle, "Set Measurements...", "area mean min max integrated");
//        IJ.run(particle,"Measure", "");
//        IJ.run(particle_bg, "Set Measurements...", "area mean min max integrated");
//        IJ.run(particle_bg,"Measure", "");
//
//        particle.show();
//        particle_bg.show();
        //
//        var table = (ResultsTable) ResultsTable.getActiveTable().clone();
//        var arr = new Variable[2];
//        arr[0] = new Variable("First image");
//        arr[1] = new Variable("Second image");
//        table.setColumn("Image name", arr);
//        table.show("My custom results");

//        var particle = opener.openImage("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/src/main/java/org/nps/test/320-1.tif");
//        var particle_bg = IJ.openImage("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/src/main/java/org/nps/test/320-2.tif");
//
//        var rt = new ResultsTable();
//        var p_analyzer = new Analyzer(particle, rt);
//        var p_bg_analyzer = new Analyzer(particle_bg, rt);
//
//        p_analyzer.measure();
//        p_bg_analyzer.measure();
//
//        rt.save("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/src/main/java/org/nps/test/res.csv");
//
//        particle.close();
//        particle_bg.close();
    }

    public static void analyze() {
        Opener opener = new Opener();

        var rt = new ResultsTable();
        for (int i = 0; i < 36; i++) {
            var particle = opener.openImage("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/data/" + i * 10 + ".bmp");
            var analyzer = new Analyzer(particle, rt);
            analyzer.measure();
            particle.close();
        }

        rt.save("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/data/res.csv");
    }

    public static void analyze_with_thresh() throws IOException {
        Opener opener = new Opener();

        int measurements = Measurements.AREA |
                Measurements.MEAN |
                Measurements.STD_DEV |
                Measurements.MIN_MAX |
                Measurements.INTEGRATED_DENSITY;

        var rt = new ResultsTable();
        for (int i = 0; i <= 36; i++) {

            var particle = opener.openImage("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/data/" + i * 10 + ".bmp");

            // After the conversion the image is an 8-bit grayscale image
            var particleByteProcessor = particle.getProcessor().convertToByte(false);
            particleByteProcessor.setThreshold(0, 255);
            particle.close();

            var p = new ImagePlus();
            p.setImage(particleByteProcessor.createImage());

            var analyzer = new Analyzer(p, measurements, rt);
            analyzer.measure();
            particle.close();
        }

        rt.deleteColumn("RawIntDen"); // TODO: ask what column has to be deleted between IntDen and RawIntDen
        rt.save("/Users/matteo/develop/uni/ece-nps-java/ece-nps-java/out/res.csv");
    }
}