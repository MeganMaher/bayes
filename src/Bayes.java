import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Bayes {

    private static final String TRAINING_PATH = "/home/maherH/IdeaProjects/Bayes/proj1train.jpeg";
    private static final String TEST_PATH = "/home/maherH/IdeaProjects/Bayes/proj1test.jpeg";

    ArrayList<Patient> patientList;

    double withVirus;
    double withoutVirus;

    int numWithVirus;
    int numWithoutVirus;

    double malesWithVirus;
    double malesWithoutVirus;
    double femaleWithVirus;
    double femaleWithoutVirus;

    double positiveWithVirus;
    double negativeWithVirus;
    double positiveWithoutVirus;
    double negativeWithoutVirus;

    double fatWithVirus;
    double thinWithVirus;
    double fatWithoutVirus;
    double thinWithoutVirus;

    int NN, NY, YN, YY;

    double virusMean;
    double virusSD;
    double noVirusMean;
    double noVirusSD;

    ArrayList<Double> withVirusWeights;
    ArrayList<Double> withoutVirusWeights;

    public Bayes(String filePath) {
        patientList = parseFile(filePath);
        priorVirus();
        genderVirus();
        bloodTypeVirus();
        weightVirus();
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("File? (training or test)?");

        String fileChoice = scan.nextLine();

        String path = "";

        if (fileChoice.toLowerCase().startsWith("tr")) {
            path = TRAINING_PATH;
        } else if (fileChoice.toLowerCase().startsWith("te")) {
            path = TEST_PATH;
        } else {
            System.err.println("BAD FILE NAME");
            return;
        }

        Bayes model = new Bayes(path);

        System.out.println("prior for not virus: " + model.withoutVirus);
        System.out.println("prior for with virus: " + (model.withVirus));

        System.out.println("likelihood for females given not virus: " + (model.femaleWithoutVirus));
        System.out.println("likelihood for females given virus: " + model.femaleWithVirus);

        System.out.println("likelihood for males given not virus: " + model.malesWithoutVirus);
        System.out.println("likelihood for males given virus: " + model.malesWithVirus);

        System.out.println("likelihood for blood positive given not virus: " + model.positiveWithoutVirus);
        System.out.println("likelihood for blood positive given virus: " + model.positiveWithVirus);

        System.out.println("likelihood for blood not positive given not virus: " + model.negativeWithoutVirus);
        System.out.println("likelihood for blood not positive given virus: " + model.negativeWithVirus);

        System.out.println("likelihood for weight > 170 given not virus: " + model.fatWithoutVirus);
        System.out.println("likelihood for weight > 170 given virus: " + model.fatWithVirus);

        System.out.println("likelihood for weight <= 170 given not virus: " + model.thinWithoutVirus);
        System.out.println("likelihood for weight <= 170 given virus: " + model.thinWithVirus);

        System.out.println();
        model.printPrediction();

        System.out.println();
        model.printConfusionMatrix();

        System.out.println("\n-- Press Enter for #4 --\n");
        scan.nextLine();

        model.doNumber4Please();
    }

    private void doNumber4Please() {
        Pair<Double, Double> virusPair = meanAndSD(withVirusWeights);
        virusMean = virusPair.getKey();
        virusSD = virusPair.getValue();

        Pair<Double, Double> noVirusPair = meanAndSD(withoutVirusWeights);
        noVirusMean = noVirusPair.getKey();
        noVirusSD = noVirusPair.getValue();

        printPrediction2();
        printConfusionMatrix();
    }

    private void priorVirus() {
        numWithVirus = 0;
        numWithoutVirus = 0;

        for (Patient p : patientList) {
            if (p.hasVirus()) {
                numWithVirus   ++;
            }else {
                numWithoutVirus  ++;
            }
        }

        withVirus = (double) numWithVirus / (double) patientList.size();
        withoutVirus = (double) numWithoutVirus / (double) patientList.size();
    }

    private void genderVirus() {
        int numFemaleWithVirus = 0;
        int numMalesWithVirus = 0;
        int numFemaleWithoutVirus = 0;
        int numMalesWithoutVirus = 0;

        for (Patient p : patientList) {
            if (p.getGender() == Patient.FEMALE) {
                if (p.hasVirus()) {
                    numFemaleWithVirus++;
                } else {
                    numFemaleWithoutVirus++;
                }
            } else {
                if (p.hasVirus()) {
                    numMalesWithVirus ++;
                } else {
                    numMalesWithoutVirus ++;
                }
            }
        }

        femaleWithVirus = (double) numFemaleWithVirus / (double) numWithVirus;
        malesWithVirus = (double) numMalesWithVirus / (double) numWithVirus;

        femaleWithoutVirus = (double) numFemaleWithoutVirus / (double) numWithoutVirus;
        malesWithoutVirus = (double) numMalesWithoutVirus / (double) numWithoutVirus;
    }

    private void bloodTypeVirus() {
        int numPosWithVirus = 0;
        int numNegWithVirus = 0;
        int numPosWithoutVirus = 0;
        int numNegWithoutVirus = 0;

        for (Patient p : patientList) {
            if (p.getBloodType().endsWith("+")) {
                if (p.hasVirus()) {
                    numPosWithVirus++;
                } else {
                    numPosWithoutVirus++;
                }
            } else {
                if (p.hasVirus()) {
                    numNegWithVirus++;
                } else {
                    numNegWithoutVirus++;
                }
            }
        }

        positiveWithVirus = (double) numPosWithVirus / (double) numWithVirus;
        negativeWithVirus = (double) numNegWithVirus / (double) numWithVirus;

        positiveWithoutVirus = (double) numPosWithoutVirus / (double) numWithoutVirus;
        negativeWithoutVirus = (double) numNegWithoutVirus / (double) numWithoutVirus;
    }

    private void weightVirus() {
        int numFatWithVirus = 0;
        int numLightWithVirus = 0;
        int numFatWithoutVirus = 0;
        int numLightWithoutVirus = 0;

        withVirusWeights = new ArrayList<>();
        withoutVirusWeights = new ArrayList<>();

        for (Patient p : patientList) {

            if (p.hasVirus()) {
                withVirusWeights.add(p.getWeight());
            } else {
                withoutVirusWeights.add(p.getWeight());
            }

            if (p.getWeight() > 170) {
                if (p.hasVirus()) {
                    numFatWithVirus++;
                } else {
                    numFatWithoutVirus++;
                }
            } else {
                if (p.hasVirus()) {
                    numLightWithVirus++;
                } else {
                    numLightWithoutVirus++;
                }
            }
        }

        fatWithVirus = (double) numFatWithVirus / (double) numWithVirus;
        thinWithVirus = (double) numLightWithVirus / (double) numWithVirus;

        fatWithoutVirus = (double) numFatWithoutVirus / (double) numWithoutVirus;
        thinWithoutVirus = (double) numLightWithoutVirus / (double) numWithoutVirus;
    }

    public void printPrediction() {
        int count = 1;

        NN = NY = YN = YY = 0;

        for (Patient p : patientList) {
            double discNoVirus = withoutVirus;
            double discWithVirus = withVirus;

            if (p.getGender() == Patient.MALE) {
                discNoVirus *= malesWithoutVirus;
                discWithVirus *= malesWithVirus;
            } else {
                discNoVirus *= femaleWithoutVirus;
                discWithVirus *= femaleWithVirus;
            }

            if (p.getBloodType().endsWith("+")) {
                discNoVirus *= positiveWithoutVirus;
                discWithVirus *= positiveWithVirus;
            } else {
                discNoVirus *= negativeWithoutVirus;
                discWithVirus *= negativeWithVirus;
            }

            if (p.getWeight() > 170) {
                discNoVirus *= fatWithoutVirus;
                discWithVirus *= fatWithVirus;
            } else {
                discNoVirus *= thinWithoutVirus;
                discWithVirus *= thinWithVirus;
            }

            boolean predictVirus = discWithVirus >= discNoVirus;

            if (p.hasVirus()) {
                if (predictVirus) {
                    YY ++;
                } else {
                    YN ++;
                }
            } else {
                if (predictVirus) {
                    NY++;
                } else {
                    NN++;
                }
            }

            String actualClass = p.hasVirus() ? "Y" : "N";
            String predictClass = predictVirus ? "Y" : "N";

            System.out.println((count++) + " " + actualClass + " " + predictClass);
        }
    }

    public void printConfusionMatrix() {
        System.out.println("\nConfusion Matrix");
        System.out.println("           Predicted Yes  Predicted No");
        System.out.println("Actual Yes      " + YY + "            " + YN);
        System.out.println("Actual No       " + NY + "            " + NN);
    }

    public Pair<Double, Double> meanAndSD(ArrayList<Double> list) {
        double mean = 0;

        for (Double d : list) {
            mean += d;
        }

        mean /= list.size();

        double sd = 0;

        for (Double d : list) {
            sd += Math.pow((d - mean), 2);
        }

        sd /= list.size();

        return new Pair<>(mean, sd);
    }

    /**
     * Prediction for number 4
     */
    public void printPrediction2() {
        int count = 1;

        NN = NY = YN = YY = 0;

        for (Patient p : patientList) {
            double discNoVirus = withoutVirus;
            double discWithVirus = withVirus;

            if (p.getGender() == Patient.MALE) {
                discNoVirus *= malesWithoutVirus;
                discWithVirus *= malesWithVirus;
            } else {
                discNoVirus *= femaleWithoutVirus;
                discWithVirus *= femaleWithVirus;
            }

            if (p.getBloodType().endsWith("+")) {
                discNoVirus *= positiveWithoutVirus;
                discWithVirus *= positiveWithVirus;
            } else {
                discNoVirus *= negativeWithoutVirus;
                discWithVirus *= negativeWithVirus;
            }

            double virusWeight = (1/(Math.sqrt(2*Math.PI * Math.pow(virusSD, 2))));
            virusWeight *= Math.pow(Math.E, ((Math.pow((virusMean - withVirus), 2))/(2 * Math.pow(virusSD, 2))));

            double noVirusWeight = (1/(Math.sqrt(2*Math.PI * Math.pow(noVirusSD, 2))));
            noVirusWeight *= Math.pow(Math.E, ((Math.pow((noVirusMean - withoutVirus), 2))/(2 * Math.pow(noVirusSD, 2))));

            discWithVirus *= virusWeight;
            discNoVirus *= noVirusWeight;

            boolean predictVirus = discWithVirus >= discNoVirus;

            if (p.hasVirus()) {
                if (predictVirus) {
                    YY ++;
                } else {
                    YN ++;
                }
            } else {
                if (predictVirus) {
                    NY++;
                } else {
                    NN++;
                }
            }

            String actualClass = p.hasVirus() ? "Y" : "N";
            String predictClass = predictVirus ? "Y" : "N";

            System.out.println((count++) + " " + actualClass + " " + predictClass);
        }
    }

    private ArrayList<Patient> parseFile(String filePath) {

        ArrayList<Patient> patientList = new ArrayList<>();

        /* Parse file into patient List */
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] str = line.split(",");

                int id = Integer.parseInt(str[0]);
                int gender = Patient.MALE;

                if (str[1].toLowerCase().startsWith("f")) {
                    gender = Patient.FEMALE;
                }

                String bloodType = str[2];
                double weight = Double.parseDouble(str[3]);

                boolean hasVirus = str[4].toLowerCase().startsWith("y");

                patientList.add(new Patient(id, gender, bloodType, weight, hasVirus));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return patientList;
    }
}
