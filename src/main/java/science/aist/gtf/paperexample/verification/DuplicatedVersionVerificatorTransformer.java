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
import science.aist.gtf.paperexample.Utils;
import science.aist.gtf.paperexample.graph.FullyQualifiedVersionDependencyNode;
import science.aist.gtf.paperexample.graph.Node;
import science.aist.gtf.transformation.Transformer;
import science.aist.gtf.verification.syntactic.PropertyRestrictor;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Analyses all Dependency Nodes in a Graph and checks for duplicated artifact Qualifiers to find version
 * conflicts.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class DuplicatedVersionVerificatorTransformer implements Transformer<Graph<Node, String>, Set<FullyQualifiedVersionDependencyNode>> {
    @Override
    public Set<FullyQualifiedVersionDependencyNode> applyTransformation(Graph<Node, String> graph) {
        var pr = new PropertyRestrictor(false);
        /*Note: This may resulting in more constraints errors, than expected. The reason is because of the dependency
         * resolution. As we already discovered, the dependency resolution sometimes returns weird results, where certain
         * dependency do not contain all children, that are contained in other dependency resolution runs. This is
         * probably due to the way versions are declared in the pom. As we ignore this when building our graph, there may
         * be results, where dependencies show up for certain projects, that the dependency tree did not cover. This
         * leads to additional duplicated versions, compared to the raw dependency tree that is executed in the json file.
         * For the antenna_pod example this e.g. leads to the result that the dependency androidx.arch.core:core-common
         * is contained with three different version: 2.0.0, 2.0.1 and 2.1.0, but is only contained in the json in the
         * versions 2.0.0 and 2.1.0. */
        pr.addFieldConstraint(FullyQualifiedVersionDependencyNode.class, "artifactQualifier", new DependencyNodeArtifactQualifierConstraint());

        var pv = new GraphPropertyVerificator<Node, String>();
        pv.setRestrictor(pr);

        var constraintViolationStatistic = pv.getInvalidProperties(graph).createStatistic();
        return constraintViolationStatistic.getConstraintViolators(DependencyNodeArtifactQualifierConstraint.DUPLICATED_DEPENDENCY_CONSTRAINT_ERROR)
                .keySet()
                .stream()
                .map(Utils::<FullyQualifiedVersionDependencyNode>cast)
                .collect(Collectors.toSet());
    }
}
