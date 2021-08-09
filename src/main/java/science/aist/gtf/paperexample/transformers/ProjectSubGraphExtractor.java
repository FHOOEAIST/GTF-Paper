package science.aist.gtf.paperexample.transformers;

import lombok.AllArgsConstructor;
import science.aist.gtf.graph.Edge;
import science.aist.gtf.graph.Graph;
import science.aist.gtf.graph.Vertex;
import science.aist.gtf.graph.impl.GraphCollector;
import science.aist.gtf.paperexample.Utils;
import science.aist.gtf.paperexample.graph.Node;
import science.aist.gtf.paperexample.graph.ProjectNode;
import science.aist.gtf.transformation.Transformer;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * <p>Extracts a Sub-Graph from the full graph, that only represents the dependencies of a single project.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@AllArgsConstructor
public class ProjectSubGraphExtractor implements Transformer<Graph<Node, String>, Graph<Node, String>> {

    private final String projectKey;

    @Override
    public Graph<Node, String> applyTransformation(Graph<Node, String> graph) {
        var optionalProjectVertex = graph.getVertices()
                .stream()
                .filter(v -> v.getElement() instanceof ProjectNode)
                .map(Utils::<Vertex<ProjectNode, Void>>cast)
                .filter(v -> v.getElement().getKey().equals(projectKey))
                .findAny();
        if (optionalProjectVertex.isEmpty()) {
            throw new IllegalStateException("Project not found!");
        }

        var dependencyVertices = new ArrayList<Vertex<Node, String>>();

        var stack = new ArrayDeque<Vertex<Node, String>>();
        stack.add(Utils.cast(optionalProjectVertex.get()));
        while (!stack.isEmpty()) {
            var node = stack.pop();
            dependencyVertices.add(node);
            node.getOutgoingEdges().stream().map(Edge::getTarget).forEach(stack::push);
        }

        return dependencyVertices.stream().collect(GraphCollector.toSubGraph());
    }
}
