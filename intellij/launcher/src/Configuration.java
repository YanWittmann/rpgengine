import java.io.*;

public class Configuration {
    String[][] config;
    String filename;
    boolean ready = false;

    public Configuration(String pFilename) {
        filename = pFilename.replace(".cfg", "") + ".cfg";
        readConfig(filename);
    }

    public String get(String option) {
        if (ready) {
            for (String[] strings : config) {
                if (strings[0].equals(option)) return strings[1];
            }
        }
        return "";
    }

    public int getInt(String option) {
        if (ready) {
            for (String[] strings : config) {
                if (strings[0].equals(option)) return Integer.parseInt(strings[1]);
            }
        }
        return -1;
    }

    public void addIfNotExist(String option, String value) {
        if (!optionExists(option)) {
            addOption(option);
            set(option, value);
        }
    }

    public void set(String option, String value) {
        if (ready) {
            addIfNotExist(option, value);
            StringBuilder output = new StringBuilder();
            for (String[] strings : config) {
                output.append(strings[0]).append(":").append(strings[1]).append("\n");
            }
            output = new StringBuilder(output.substring(0, output.length() - 1).replace(option + ":" + get(option), option + ":" + value));
            try {
                writeToFile(filename, output.toString().replaceAll("(?m)^[ \t]*\r?\n", ""));
            } catch (Exception ignored) {
            }
            readConfig(filename);
        }
    }

    public void set(String option, int value) {
        if (ready) {
            addIfNotExist(option, value + "");
            StringBuilder output = new StringBuilder();
            for (String[] strings : config) {
                output.append(strings[0]).append(":").append(strings[1]).append("\n");
            }
            output = new StringBuilder(output.substring(0, output.length() - 1).replace(option + ":" + get(option), option + ":" + value));
            try {
                writeToFile(filename, output.toString().replaceAll("(?m)^[ \t]*\r?\n", ""));
            } catch (Exception ignored) {
            }
            readConfig(filename);
        }
    }

    public void addOption(String option) {
        if (ready && !optionExists(option)) {
            StringBuilder output = new StringBuilder();
            for (String[] strings : config) {
                output.append(strings[0]).append(":").append(strings[1]).append("\n");
            }
            output.append("\n").append(option).append(":null");
            try {
                writeToFile(filename, output.toString().replaceAll("(?m)^[ \t]*\r?\n", ""));
            } catch (Exception ignored) {
            }
            readConfig(filename);
        }
    }

    public void removeOption(String option) {
        if (ready && optionExists(option)) {
            StringBuilder output = new StringBuilder();
            for (String[] strings : config) {
                if (!strings[0].equals(option))
                    output.append(strings[0]).append(":").append(strings[1]).append("\n");
            }
            output = new StringBuilder(output.substring(0, output.length() - 1));
            try {
                writeToFile(filename, output.toString().replaceAll("(?m)^[ \t]*\r?\n", ""));
            } catch (Exception ignored) {
            }
            readConfig(filename);
        }
    }

    public boolean optionExists(String option) {
        if (ready) {
            for (String[] strings : config) {
                if (option.equals(strings[0])) return true;
            }
        }
        return false;
    }

    public void refresh() {
        readConfig(filename);
    }

    private void readConfig(String pFilename) {
        if (!(new File(pFilename).isFile()))
            try {
                writeToFile(pFilename, "");
            } catch (Exception ignored) {
            }
        try {
            String[] input = readFile(pFilename);
            config = new String[input.length][2];
            for (int i = 0; i < input.length; i++) {
                config[i] = input[i].split(":");
            }
            ready = true;
        } catch (Exception ignored) {
        }
    }

    private String[] readFile(String pFilename) {
        String[] result;
        File file = new File(pFilename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Windows-1252"));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Windows-1252"));

            int lines = 0, counter = 0;
            while (br2.readLine() != null) lines++;
            result = new String[lines];

            String st;
            while ((st = br.readLine()) != null) {
                result[counter] = st;
                counter++;
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    private void writeToFile(String pFilename, String text) throws IOException {
        File file = new File(pFilename);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Cp1252"));
        bw.write(text);
        bw.close();
    }
}
