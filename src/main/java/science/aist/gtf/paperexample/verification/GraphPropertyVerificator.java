/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
