package science.aist.gtf.paperexample.transformers;

import science.aist.gtf.graph.Graph;
import science.aist.gtf.graph.Vertex;
import science.aist.gtf.graph.builder.impl.GraphBuilderImpl;
import science.aist.gtf.graph.impl.AbstractEdge;
import science.aist.gtf.graph.impl.traversal.DepthFirstSearchTraversalStrategy;
import science.aist.gtf.paperexample.graph.FullyQualifiedDependencyNode;
import science.aist.gtf.paperexample.graph.FullyQualifiedVersionDependencyNode;
import science.aist.gtf.paperexample.graph.Node;
import science.aist.gtf.paperexample.graph.ProjectNode;
import science.aist.gtf.transformation.Transformer;
import science.aist.gtf.transformation.renderer.AbstractGraphTransformationRenderer;
import science.aist.gtf.transformation.renderer.MultiGraphTransformationRenderer;
import science.aist.gtf.transformation.renderer.condition.RendererCondition;
import science.aist.jack.general.PropertyMapperCreator;
import science.aist.jack.stream.FunctionUtil;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * <p>Transforms the graph from a graph of {@link FullyQualifiedVersionDependencyNode} nodes to a graph of
 * {@link FullyQualifiedDependencyNode}</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class FullyQualifiedVersionDependencyNodeGraphToFullyQualifiedDependencyNodeGraphTransformer implements Transformer<Graph<Node, String>, Graph<Node, String>> {

    @Override
    public Graph<Node, String> applyTransformation(Graph<Node, String> graph) {
        var graphBuilder = GraphBuilderImpl.<Node, String>create(Node::getKey);

        MultiGraphTransformationRenderer<Node, Node, String> renderer = new MultiGraphTransformationRenderer<>(List.of(
                new ProjectNodeRenderer(new ProjectNodeCondition()),
                new FullyQualifiedDependencyNodeRenderer(new FullyQualifiedVersionDependencyNodeCondition())
        ));

        graph.setVertexTraversalStrategy(new DepthFirstSearchTraversalStrategy<>(graph));
        graph.traverseEdges(
                FunctionUtil.emptyConsumer()::accept,
                edge -> graphBuilder
                        .from(renderer.renderElement(graph, edge.getSource()))
                        .toWith(renderer.renderElement(graph, edge.getTarget()))
                        .with(newEdge -> ((AbstractEdge<?, ?>) newEdge).setWeight(newEdge.getWeight() - 1 + edge.getWeight()))
        );

        return graphBuilder.toGraph();
    }


    static class ProjectNodeRenderer extends AbstractGraphTransformationRenderer<ProjectNode, Node, String> {
        private final BiFunction<ProjectNode, ProjectNode, ProjectNode> mapping;

        public ProjectNodeRenderer(RendererCondition<Vertex<Node, String>> rendererCondition) {
            super(rendererCondition, ProjectNode.class, Node.class);
            mapping = new PropertyMapperCreator<ProjectNode, ProjectNode>()
                    .from(ProjectNode::getUserName).to(ProjectNode::setUserName)
                    .from(ProjectNode::getRepoName).to(ProjectNode::setRepoName)
                    .create();
        }

        @Override
        public ProjectNode mapProperties(ProjectNode projectNode, Graph<Node, String> vertices, Vertex<Node, String> currentElement) {
            return mapping.apply((ProjectNode) currentElement.getElement(), projectNode);
        }
    }

    static class ProjectNodeCondition implements RendererCondition<Vertex<Node, String>> {
        @Override
        public Predicate<Vertex<Node, String>> createCondition() {
            return x -> x.getElement() instanceof ProjectNode;
        }
    }

    static class FullyQualifiedDependencyNodeRenderer extends AbstractGraphTransformationRenderer<FullyQualifiedDependencyNode, Node, String> {
        private final BiFunction<FullyQualifiedVersionDependencyNode, FullyQualifiedDependencyNode, FullyQualifiedDependencyNode> mapping;

        public FullyQualifiedDependencyNodeRenderer(RendererCondition<Vertex<Node, String>> rendererCondition) {
            super(rendererCondition, FullyQualifiedDependencyNode.class, Node.class);
            mapping = new PropertyMapperCreator<FullyQualifiedVersionDependencyNode, FullyQualifiedDependencyNode>()
                    .from(FullyQualifiedVersionDependencyNode::getGroupId).to(FullyQualifiedDependencyNode::setGroupId)
                    .from(FullyQualifiedVersionDependencyNode::getArtifactId).to(FullyQualifiedDependencyNode::setArtifactId)
                    .create();
        }

        @Override
        public FullyQualifiedDependencyNode mapProperties(FullyQualifiedDependencyNode fullyQualifiedDependencyNode, Graph<Node, String> vertices, Vertex<Node, String> currentElement) {
            return mapping.apply((FullyQualifiedVersionDependencyNode) currentElement.getElement(), fullyQualifiedDependencyNode);
        }
    }

    static class FullyQualifiedVersionDependencyNodeCondition implements RendererCondition<Vertex<Node, String>> {
        @Override
        public Predicate<Vertex<Node, String>> createCondition() {
            return x -> x.getElement() instanceof FullyQualifiedVersionDependencyNode;
        }
    }
}
