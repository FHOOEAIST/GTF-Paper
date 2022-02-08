package science.aist.gtf.paperexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import science.aist.gtf.graph.Graph;
import science.aist.gtf.graph.builder.GraphBuilder;
import science.aist.gtf.graph.builder.impl.GraphBuilderImpl;
import science.aist.gtf.paperexample.domain.Dependency;
import science.aist.gtf.paperexample.domain.Repository;
import science.aist.gtf.paperexample.graph.FullyQualifiedVersionDependencyNode;
import science.aist.gtf.paperexample.graph.Node;
import science.aist.gtf.paperexample.graph.ProjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/**
 * <p>Creates the graph out of the dependency json files.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class GraphCreator {
    public Graph<Node, String> makeGraph() {
        var mapper = new ObjectMapper();
        var graphBuilder = GraphBuilderImpl.<Node, String>create(Node::getKey);
        var files = Objects.requireNonNull(new File("jsons").list());

        for (String file : files) {
            if (file.equals("_errors.txt")) continue;
            try {
                var repository = mapper.readValue(new File("jsons/" + file), Repository.class);
                var projectNode = ProjectNode.builder().repoName(repository.getRepoName()).userName(repository.getGithubUser()).build();
                graphBuilder.addVertex(projectNode);
                addDependencies(graphBuilder, projectNode, repository.getDependencies());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return graphBuilder.toGraph();
    }


    private void addDependencies(GraphBuilder<Node, String> builder, Node parent, Collection<Dependency> dependencyList) {
        if (dependencyList == null || dependencyList.isEmpty()) return;
        for (Dependency dependency : dependencyList) {
            var dp = new FullyQualifiedVersionDependencyNode(
                    dependency.getGroupId(),
                    dependency.getArtifactId(),
                    dependency.getVersion()
            );
            builder.from(parent).toData(dp).data(dependency.getScope());
            addDependencies(builder, dp, dependency.getChildren());
        }
    }
}
