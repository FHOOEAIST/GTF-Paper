/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.gtf.paperexample.transformers;

import science.aist.gtf.graph.Edge;
import science.aist.gtf.graph.Graph;
import science.aist.gtf.graph.impl.traversal.DepthFirstSearchTraversalStrategy;
import science.aist.gtf.paperexample.graph.Node;
import science.aist.gtf.paperexample.graph.ProjectNode;
import science.aist.gtf.transformation.GraphTransformer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>The number of times a specific version is used in form of a csv result.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class VersionAnalyzerTransformer implements GraphTransformer<Node, String, String> {

    @Override
    public String applyTransformation(Graph<Node, String> graph) {
        Map<String, Integer> result = new HashMap<>();
        graph.setVertexTraversalStrategy(new DepthFirstSearchTraversalStrategy<>(graph));
        graph.traverseVertices(vertex -> {
            if (!(vertex.getElement() instanceof ProjectNode))
                result.put(vertex.getElement().getKey(), (int) vertex.getIncomingEdges().stream().mapToDouble(Edge::getWeight).sum());
        });

        return "Dependency;Count\n" +
                result.entrySet().stream().sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed())
                        .map(e -> e.getKey() + ";" + e.getValue())
                        .collect(Collectors.joining("\n"));
    }
}
