package science.aist.gtf.paperexample.verification;

import science.aist.gtf.graph.Graph;
import science.aist.gtf.graph.impl.traversal.DepthFirstSearchTraversalStrategy;
import science.aist.gtf.verification.syntactic.PropertyVerificator;
import science.aist.gtf.verification.syntactic.PropertyVerificatorResult;
import science.aist.jack.data.LambdaContainer;

/**
 * <p>Implementation of the PropertyVerificator for graphs.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class GraphPropertyVerificator<V, E> extends PropertyVerificator<Graph<V, E>> {
    @Override
    public PropertyVerificatorResult getInvalidProperties(Graph<V, E> obj) {
        LambdaContainer<PropertyVerificatorResult> propertyVerificatorResultLambdaContainer = new LambdaContainer<>(new PropertyVerificatorResult());
        obj.setVertexTraversalStrategy(new DepthFirstSearchTraversalStrategy<>(obj));
        obj.traverseVertices(v -> propertyVerificatorResultLambdaContainer.setValue(PropertyVerificatorResult.mergeVerificationResults(propertyVerificatorResultLambdaContainer.getValue(), getVisitorFactory().createObjectVisitor().visit(v.getElement()))));
        return propertyVerificatorResultLambdaContainer.getValue();
    }
}
