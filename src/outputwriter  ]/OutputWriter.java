package outputwriter;

import java.io.*;
import java.nio.file.*;

public class OutputWriter {

    private final Path outputDir;
    private final Path compilerOutputDir;

    public OutputWriter(String basePath) {
        this.outputDir = Paths.get(basePath, "output");
        this.compilerOutputDir = Paths.get(basePath, "compiler_output");
        try {
            Files.createDirectories(outputDir);
            Files.createDirectories(compilerOutputDir);
        } catch (IOException e) {
            System.err.println("Could not create output directories: " + e.getMessage());
        }
    }

    public void writeHtml(String filename, String content) {
        writeFile(outputDir.resolve(filename), content);
    }

    public void writeTemplate(String filename, String content) {
        writeFile(outputDir.resolve(filename), content);
    }

    public void writeCompilerOutput(String filename, String content) {
        writeFile(compilerOutputDir.resolve(filename), content);
    }

    public void writeJson(String filename, String json) {
        writeFile(compilerOutputDir.resolve(filename), json);
    }

    public void copyAsset(String sourcePath) {
        File source = new File(sourcePath);
        if (!source.exists()) {
            System.err.println("Asset not found: " + sourcePath);
            return;
        }
        try {
            Files.copy(source.toPath(), outputDir.resolve(source.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Could not copy asset " + sourcePath + ": " + e.getMessage());
        }
    }

    public void copyAssetWithName(String sourcePath, String targetName) {
        File source = new File(sourcePath);
        if (!source.exists()) {
            File alt = new File(System.getProperty("user.dir"), sourcePath);
            if (alt.exists()) {
                source = alt;
            }
        }
        if (!source.exists()) {
            System.err.println("Asset not found: " + sourcePath);
            return;
        }
        try {
            Files.copy(source.toPath(), outputDir.resolve(targetName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Could not copy asset " + sourcePath + ": " + e.getMessage());
        }
    }

    public void copyDirectory(Path sourceDir, Path targetDir) {
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) return;
        try {
            Files.walk(sourceDir).forEach(source -> {
                try {
                    Path relative = sourceDir.relativize(source);
                    Path target = targetDir.resolve(relative);
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(target);
                    } else {
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    System.err.println("Could not copy " + source + ": " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Could not walk directory " + sourceDir + ": " + e.getMessage());
        }
    }

    public void copyFileToRoot(Path sourceFile) {
        if (!Files.exists(sourceFile) || Files.isDirectory(sourceFile)) return;
        try {
            Files.copy(sourceFile, outputDir.resolve(sourceFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Could not copy " + sourceFile + ": " + e.getMessage());
        }
    }

    private void writeFile(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Could not write file " + path + ": " + e.getMessage());
        }
    }

    public Path getOutputDir() {
        return outputDir;
    }

    public Path getCompilerOutputDir() {
        return compilerOutputDir;
    }
}
