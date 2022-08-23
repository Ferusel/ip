package common;

import Commands.DeadlineCommand;
import Commands.EventCommand;
import TaskList.TaskList;
import Tasks.Deadline;
import Tasks.Event;
import Tasks.ToDo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Storage {
    private String storageName;
    private String storageDirname;
    private String filePath;

    public Storage(String storageName, String storageDirName) {
        this.storageName = storageName;
        this.storageDirname = storageDirName;
        this.filePath = String.format("%s%s%s", this.storageDirname, File.separator, this.storageName);
    }


    public void initializeStorage() throws IOException {
        Path filePath = Paths.get(this.filePath);
        Path dirPath = Paths.get(storageDirname);

        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
            Ui.printCreateNewDirectory(this.storageDirname);
        }
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
            Ui.printCreateNewStorage(this.storageName);
        }
    }

    public void writeToStorage(TaskList taskList) {
        try {
            FileWriter fw = new FileWriter(this.filePath);
            for (int i = 0; i < taskList.size(); i++) {
                fw.write(taskList.get(i).getEncodedValue()+ "\n");
            }
            fw.close();
        } catch (IOException e) {
            Ui.printError(e);
        }
    }

    public void readFromStorage(TaskList taskList) throws IOException {
        File f = new File(this.filePath);
        Scanner s = new Scanner(f); // create a Scanner using the File as the source
        while (s.hasNext()) {
            String[] encodedTask = s.nextLine().split("#");
            switch (encodedTask[0]) {
                case "[T]": {
                   taskList.addTask(new ToDo(encodedTask[1], Boolean.parseBoolean(encodedTask[2])));
                   break;
                }
                case "[D]": {
                    taskList.addTask(new Deadline(encodedTask[1], Boolean.parseBoolean(encodedTask[2]), DeadlineCommand.parseDeadlineDatetimeFromStorage(encodedTask[3])));
                    break;
                }
                case "[E]": {
                    taskList.addTask(new Event(encodedTask[1], Boolean.parseBoolean(encodedTask[2]), EventCommand.parseEventDatetimeFromStorage(encodedTask[3]), EventCommand.parseEventDatetimeFromStorage(encodedTask[4])));
                    break;
                }
                default:
                    throw new IOException();
            }
        }
    }
}
