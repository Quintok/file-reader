package com.company;

import com.beust.jcommander.JCommander;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        FileLoader fileLoader = new FileLoader();
        JCommander jCommander = new JCommander(fileLoader, args);
        fileLoader.loadFile(jCommander);
    }

}
