package com.gytmy.sound.whisper;

import com.gytmy.sound.User;
import com.gytmy.sound.YamlReader;
import com.gytmy.utils.JsonParser;
import com.gytmy.utils.RunSH;
import com.gytmy.utils.ThreadedQueue;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Whisper {

    public enum Model {
        TINY_EN, BASE_EN, SMALL_EN, MEDIUM_EN;

        public String getModelName() {
            switch (this) {
                case TINY_EN:
                    return "tiny.en";
                case BASE_EN:
                    return "base.en";
                case SMALL_EN:
                    return "small.en";
                case MEDIUM_EN:
                    return "medium.en";
                default:
                    return "tiny.en";
            }
        }
    }

    public static final String WHISPER_PATH = "src/main/exe/whisper/whisper.sh";

    private JsonParser<WhisperResult> whisperJsonParser = new JsonParser<>();

    private Model model; // The whisper model to use

    public Whisper(Model model) {
        this.model = model;
    }

    /**
     * Asks the user for a command and returns it as a future
     */
    public CompletableFuture<String> ask(String audioPath, String fileName, String outputPath) {
        CompletableFuture<String> futureCommand = new CompletableFuture<>();

        ThreadedQueue.executeTask(() -> {
            try {
                String recognizedCommand = "";

                int exitCode = run(audioPath, outputPath);

                if (exitCode == 0) {
                    recognizedCommand = parseJson(outputPath, fileName);
                    recognizedCommand = formatCommand(recognizedCommand);
                    futureCommand.complete(recognizedCommand);
                } else {
                    futureCommand.completeExceptionally(new Exception("Whisper failed to recognize command"));
                }

            } catch (Exception e) {
                futureCommand.completeExceptionally(e);
            }
        });

        return futureCommand;
    }

    /**
     * Runs the whisper command
     * 
     * @return the exit code of the command
     */
    private int run(String filePathWithFileName, String outputPath) {
        String[] args = { "-m", model.getModelName(), "-a", filePathWithFileName, "-o", outputPath };

        int exitCode = RunSH.run(WHISPER_PATH, args);

        return exitCode;
    }

    /**
     * Parses the json file and returns the recognized command
     */
    private String parseJson(String jsonDirectoryPath, String fileName) {
        try {
            WhisperResult whisperResult = (WhisperResult) whisperJsonParser.parseJsonFromFile(
                    jsonDirectoryPath + "/" + fileName + ".json", WhisperResult.class);

            return whisperResult.getText();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private String formatCommand(String text) {
        return text.toUpperCase().replaceAll("[^A-Z]", "");
    }

    public String mapCommand(User currUser, String recognizedCommand) {

        User user = YamlReader.read(currUser.yamlConfigPath());

        if (isFromCommandList(user.getUp(), recognizedCommand)) {
            return "UP";
        } else if (isFromCommandList(user.getDown(), recognizedCommand)) {
            return "DOWN";
        } else if (isFromCommandList(user.getLeft(), recognizedCommand)) {
            return "LEFT";
        } else if (isFromCommandList(user.getRight(), recognizedCommand)) {
            return "RIGHT";
        } else {
            return "NO_COMMAND";
        }
    }

    private boolean isFromCommandList(List<String> commands, String recognizedCommand) {
        for (String command : commands) {
            if (recognizedCommand.equals(command)) {
                return true;
            }
        }
        return false;
    }
}
