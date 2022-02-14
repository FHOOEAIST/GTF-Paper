/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.gtf.paperexample.graph;

import lombok.Getter;

/**
 * <p>Node in the graph that represents a dependency.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@Getter
public class FullyQualifiedVersionDependencyNode implements Node {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String artifactQualifier;

    public FullyQualifiedVersionDependencyNode(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        artifactQualifier = groupId + ":" + artifactId;
    }

    @Override
    public String getKey() {
        return groupId + ":" + artifactId + ":" + version;
    }
}
