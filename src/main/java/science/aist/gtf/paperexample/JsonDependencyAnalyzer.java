package science.aist.gtf.paperexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import science.aist.gtf.paperexample.domain.Dependency;
import science.aist.gtf.paperexample.domain.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>Does some basic analysis on the json results.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class JsonDependencyAnalyzer {
    public static void main(String[] args) {
        var mapper = new ObjectMapper();
        var files = Objects.requireNonNull(new File("jsons").list());

        Map<String, AtomicInteger> map = new HashMap<>();

        for (String file : files) {
            if (file.equals("_errors.txt") || !file.equals("AntennaPod_AntennaPod.json")) continue;
            try {
                flattenDependencies(mapper.readValue(new File("jsons/" + file), Repository.class).getDependencies())
                        .forEach(dependency -> map.computeIfAbsent(dependency.toDependencyString(), x -> new AtomicInteger()).incrementAndGet());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String res = map.entrySet().stream().sorted(Comparator.<Map.Entry<String, AtomicInteger>>comparingInt(e -> e.getValue().get()).reversed())
                .map(e -> e.getKey() + " --> " + e.getValue())
                .collect(Collectors.joining("\n"));

        try {
            FileUtils.writeStringToFile(new File("versions.txt"), res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Set<Dependency> flattenDependencies(List<Dependency> dependencies) {
        Set<Dependency> result = new HashSet<>();
        dependencies.forEach(d -> flattenDependency(result, d));
        return result;
    }

    static void flattenDependency(Set<Dependency> result, Dependency dependency) {
        if (dependency == null) return;
        result.add(dependency);
        dependency.getChildren().forEach(d -> flattenDependency(result, d));
    }
}
