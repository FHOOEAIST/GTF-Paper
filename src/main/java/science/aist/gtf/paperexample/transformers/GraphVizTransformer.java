package science.aist.gtf.paperexample.transformers;

import science.aist.gtf.graph.Graph;
import science.aist.gtf.graph.impl.traversal.DepthFirstSearchTraversalStrategy;
import science.aist.gtf.paperexample.graph.Node;
import science.aist.gtf.paperexample.graph.ProjectNode;
import science.aist.gtf.transformation.GraphTransformer;
import science.aist.jack.stream.FunctionUtil;

/**
 * <p>Creates a GraphViz string out of the graph.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class GraphVizTransformer implements GraphTransformer<Node, Void, String> {
    @Override
    public String applyTransformation(Graph<Node, Void> graph) {
        graph.setVertexTraversalStrategy(new DepthFirstSearchTraversalStrategy<>(graph));
        StringBuilder edgeBuilder = new StringBuilder("digraph G {").append('\n')
                .append("graph [ overlap=false ]").append('\n');
        graph.traverseEdges(FunctionUtil.emptyConsumer()::accept, element -> {
                    if (element.getSource().getElement() instanceof ProjectNode) {
                        edgeBuilder.append('"').append(element.getSource().getElement().getKey()).append('"').append("[color=\"red\"]").append('\n');
                    }
                    edgeBuilder
                            .append('"').append(element.getSource().getElement().getKey()).append('"')
                            .append("->")
                            .append('"').append(element.getTarget().getElement().getKey()).append('"')
                            .append('\n');
                }
        );

        edgeBuilder.append("}");
        return edgeBuilder.toString();
    }
}