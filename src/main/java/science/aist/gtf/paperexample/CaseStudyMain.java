package science.aist.gtf.paperexample;

import org.apache.commons.io.FileUtils;
import science.aist.gtf.paperexample.graph.FullyQualifiedVersionDependencyNode;
import science.aist.gtf.paperexample.transformers.FullyQualifiedVersionDependencyNodeGraphToFullyQualifiedDependencyNodeGraphTransformer;
import science.aist.gtf.paperexample.transformers.GraphVizTransformer;
import science.aist.gtf.paperexample.transformers.ProjectSubGraphExtractor;
import science.aist.gtf.paperexample.transformers.VersionAnalyzerTransformer;
import science.aist.gtf.paperexample.verification.DuplicatedVersionVerificatorTransformer;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * <p>Performs different analyses on the dependencies by creating a graph.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class CaseStudyMain {
    public static void main(String[] args) throws IOException {
        var graphCreator = new GraphCreator();
        var graphVizTransformer = new GraphVizTransformer(); // dot -Ksfdp -ooutput.svg -Tsvg antenna_pod.dot
        var versionAnalyzerTransformer = new VersionAnalyzerTransformer();
        var extractor = new ProjectSubGraphExtractor("AntennaPod:AntennaPod");
        var duplicatedVersionVerificatorTransformer = new DuplicatedVersionVerificatorTransformer();
        var fullyQualifiedVersionDependencyNodeGraphToFullyQualifiedDependencyNodeGraphTransformer = new FullyQualifiedVersionDependencyNodeGraphToFullyQualifiedDependencyNodeGraphTransformer();

        // Build Graph Structure
        var graph = graphCreator.makeGraph();
        var graphWithoutVersion = fullyQualifiedVersionDependencyNodeGraphToFullyQualifiedDependencyNodeGraphTransformer.applyTransformation(graph);

        // Graph Viz Transformation - for whole graph.
//        String s = graphVizTransformer.applyTransformation(graph);
//        FileUtils.writeStringToFile(new File("graph.dot"), s);
//
//        // Version Transformation
//        var res = versionAnalyzerTransformer.applyTransformation(graph);
//        FileUtils.writeStringToFile(new File("versions.txt"), res);
        var resWithoutVersion = versionAnalyzerTransformer.applyTransformation(graphWithoutVersion);
        FileUtils.writeStringToFile(new File("versionsWithVersion.txt"), resWithoutVersion);

        // Graph Viz Transformation for app AntennaPod:AntennaPod
        var antennaPodGraphViz = extractor.andThen(graphVizTransformer).applyTransformation(graph);
        FileUtils.writeStringToFile(new File("antenna_pod.dot"), antennaPodGraphViz);

        // Verification to find duplicated versions in a project
        var antennaPodConflictingDependencyVersions = extractor
                .andThen(duplicatedVersionVerificatorTransformer)
                .applyTransformation(graph)
                .stream()
                .map(FullyQualifiedVersionDependencyNode::getArtifactQualifier)
                .distinct()
                .collect(Collectors.joining("\n"));
        FileUtils.writeStringToFile(new File("antenna_pod_versions_conflicts.txt"), antennaPodConflictingDependencyVersions);
    }

}
