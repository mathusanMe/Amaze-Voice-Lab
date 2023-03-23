package com.gytmy.sound;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;

import com.gytmy.utils.RunSH;

public class ModelManager {

        private ModelManager() {
        }

        public static final String EXE_PATH = "src/main/exe/model/";
        public static final String PARAMETRIZE_SH_PATH = EXE_PATH + "Parametrize.sh";
        public static final String ENERGY_DETECTOR_SH_PATH = EXE_PATH + "EnergyDetector.sh";
        public static final String NORM_FEAT_SH_PATH = EXE_PATH + "NormFeat.sh";
        public static final String TRAIN_WORLD_SH_PATH = EXE_PATH + "TrainWorld.sh";

        public static final String PRM_PATH = EXE_PATH + "prm/";
        public static final String LBL_PATH = EXE_PATH + "lbl/";

        public static final String LST_PATH = "/lst/Liste.lst";

        public static boolean tryToCreateModelDirectory(User user, String word) {
                File userModelDirectory = new File(user.getModelPath());

                if (!userModelDirectory.exists()) {
                        userModelDirectory.mkdir();
                }

                if (!AudioFileManager.doesFileInDirectoryExist(userModelDirectory, user.getModelPath() + word)) {
                        new File(user.getModelPath() + word).mkdir();

                        File userLstDirectory = new File(user.getModelPath() + word + "/lst/");
                        userLstDirectory.mkdir();

                        File userGmmDirectory = new File(user.getModelPath() + word + "/gmm/");
                        userGmmDirectory.mkdir();

                        return true;
                }
                return false;
        }

        public static boolean tryToCreateListFile(User user, String recordedWord) {
                File lstFile = new File(user.getModelPath() + recordedWord + "/lst/List.lst");
                try {
                        return lstFile.createNewFile();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return false;
        }

        public static void createModelOfRecordedWord(User user, String recordedWord) {
                parametrize(user, recordedWord);
                energyDetector(user, recordedWord);
                normFeat(user, recordedWord);
                trainWorld(user, recordedWord);

                resetModeler();
        }

        public static void parametrize(User user, String recordedWord) {
                String[] argsParametrize = {
                                user.getAudioPath() + recordedWord + "/",
                                PRM_PATH };

                RunSH.run(PARAMETRIZE_SH_PATH, argsParametrize);
        }

        public static void energyDetector(User user, String recordedWord) {
                String[] argsEnergyDetector = {
                                user.getModelPath() + recordedWord + LST_PATH
                };

                RunSH.run(ENERGY_DETECTOR_SH_PATH, argsEnergyDetector);
        }

        public static void normFeat(User user, String recordedWord) {
                String[] argsNormFeat = {
                                user.getModelPath() + recordedWord + "/lst/Liste.lst"
                };

                RunSH.run(NORM_FEAT_SH_PATH, argsNormFeat);
        }

        public static void trainWorld(User user, String recordedWord) {

                String[] argsTrainWorld = {
                                user.getModelPath() + recordedWord + "/lst/Liste.lst",
                                user.getModelPath() + recordedWord + "/gmm/"
                };

                RunSH.run(TRAIN_WORLD_SH_PATH, argsTrainWorld);
        }

        public static void resetModeler() {
                clearDirectory(new File(PRM_PATH));
                clearDirectory(new File(LBL_PATH));
        }

        private static void clearDirectory(File directory) {
                for (File file : directory.listFiles()) {
                        if (file.isDirectory())
                                clearDirectory(file);
                        file.delete();
                }
        }
}
