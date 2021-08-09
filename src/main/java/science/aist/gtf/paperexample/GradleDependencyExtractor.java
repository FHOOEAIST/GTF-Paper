package science.aist.gtf.paperexample;

import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static science.aist.gtf.paperexample.Utils.mkdirs;

/**
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class GradleDependencyExtractor {

    public static void main(String[] args) throws IOException {
        var dataPath = "data";

        List<String> paths = new ArrayList<>();

        for (var userName : Objects.requireNonNull(new File(dataPath).list())) {
            var userNamePath = dataPath + "/" + userName;
            for (var repo : Objects.requireNonNull(new File(userNamePath).list())) {
                var repoPath = userNamePath + "/" + repo;
                paths.add(repoPath);
            }
        }

        mkdirs("dependencies");

        try (var progressBar = new ProgressBarBuilder().setTaskName("Extracting dependencies").setInitialMax(paths.size()).setStyle(ProgressBarStyle.ASCII).build()) {
            for (String path : paths) {
//                if (!path.contains("AutoDark")) continue;
                progressBar.step();
                String filename = path.substring(dataPath.length() + 1).replace('/', '_').replace('\\', '_');
                progressBar.setExtraMessage(filename);
                List<File> gradleFiles;
                try(var stream = Files.walk(Paths.get(path))) {
                    gradleFiles = stream.filter(Files::isRegularFile)
                            .filter(f -> f.endsWith("build.gradle"))
                            .map(Path::toFile)
                            .collect(Collectors.toList());
                }

                List<String> dependencies = new ArrayList<>();
                List<String> versions = new ArrayList<>();

                for (File gradleFile : gradleFiles) {
                    var gradleFileContent = FileUtils.readFileToString(gradleFile);

                    for (String dependencyString : findBlock(gradleFileContent, "dependencies")) {
                        var patternDependency = Pattern.compile("[a-zA-Z]+\\s+\\(?['\"][^'\":]*:[^'\":]*:[^'\":]*['\"]\\)?");
                        var matcherDependency = patternDependency.matcher(dependencyString);
                        while(matcherDependency.find()) {
                            String dependency = matcherDependency.group();
                            dependencies.add(dependency.replace("(", "").replace(")", "").replace("\"","").replace("'", "").replaceAll("\\s+", " "));
                        }
                    }

                    for (String versionString : findBlock(gradleFileContent, "ext")) {
                        var patternDependency = Pattern.compile("\\w*\\s*=\\s*[\"'][^\"']*[\"']");
                        var matcherDependency = patternDependency.matcher(versionString);
                        while(matcherDependency.find()) {
                            versions.add(matcherDependency.group());
                        }
                    }
                }

                for (String version : versions) {
                    String[] split = version.split("=");
                    var versionName = split[0].trim();
                    var versionNumber = split[1].trim();
                    versionNumber = versionNumber.substring(1, versionNumber.length() -  1);
                    var versionNumberFinal = versionNumber;

                    dependencies = dependencies.stream()
                            .map(s -> s.replaceAll("\\$" + versionName + "$", versionNumberFinal))
                            .collect(Collectors.toList());
                }

                FileUtils.writeStringToFile(new File("dependencies/" + filename), String.join("\n", dependencies));
            }
        }
    }

    private static List<String> findBlock(String raw, String blockname) {
        var pattern = Pattern.compile(blockname+"\\s*\\{");
        var matcher = pattern.matcher(raw);
        List<String> blocks = new ArrayList<>();
        while (matcher.find()) {
            var dependencyStart = raw.substring(matcher.end());
            var starting = 1;
            var currentIdx = 0;
            while(starting != 0) {
                var currentChar = dependencyStart.charAt(currentIdx);
                if (currentChar == '{') starting++;
                if (currentChar == '}') starting--;
                currentIdx++;
            }
            blocks.add(dependencyStart.substring(0, currentIdx - 1));
        }
        return blocks;
    }
}
