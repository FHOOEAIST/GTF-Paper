package science.aist.gtf.paperexample;

import science.aist.gtf.graph.Edge;
import science.aist.gtf.graph.Graph;
import science.aist.gtf.graph.builder.impl.GraphBuilderImpl;
import science.aist.gtf.graph.impl.traversal.DepthFirstSearchTraversalStrategy;
import science.aist.gtf.transformation.Transformer;
import science.aist.gtf.transformation.renderer.TransformationRender;

/**
 * <p>Running Paper Example</p>
 *
 * @author Andreas Pointner
 */
public class SimpleSample {
    public static void main(String[] args) {
        // Creating a simple Person graph:
        var graph = GraphBuilderImpl.<String, String>create()
                .from("Markus").toData("Max").data("is_father")
                .from("Marianne").toData("Max").data("is_mother")
                .from("Max").toData("Erika").data("are_married")
                .from("Erika").toData("John").data("work_together")
                .from("John").toData("Richard").data("are_friends")
                .from("Max").toData("Richard").data("are_best_friends")
                .toGraph();


        // Visualize Graph
        var edgeRenderer = new TransformationRender<String, StringBuilder, Graph<String, String>, Edge<String, String>>() {
            @Override
            public String renderElement(Graph<String, String> vertices, Edge<String, String> currentElement) {
                return mapProperties(createElement(), vertices, currentElement).toString();
            }

            @Override
            public StringBuilder createElement() {
                return new StringBuilder();
            }

            @Override
            public StringBuilder mapProperties(StringBuilder stringBuilder, Graph<String, String> vertices, Edge<String, String> currentElement) {
                return stringBuilder
                        .append(currentElement.getSource().getElement()).append("->")
                        .append(currentElement.getTarget().getElement())
                        .append("[label=\"").append(currentElement.getElement()).append("\"];\n");
            }
        };

        Transformer<Graph<String, String>, String> transformer = g -> {
            g.setVertexTraversalStrategy(new DepthFirstSearchTraversalStrategy<>(g));
            var result = new StringBuilder();
            result.append("digraph G {\n");
            g.traverseEdges(
                  x -> {},
                    e -> result.append("  ").append(edgeRenderer.renderElement(g, e))
            );
            result.append("}\n");
            return result.toString();
        };
        System.out.println(transformer.applyTransformation(graph));
    }
}
