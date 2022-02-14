/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.gtf.paperexample.graph;

import lombok.*;

/**
 * <p>Node in the graph that represents a project.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProjectNode implements Node {

    private String userName;
    private String repoName;

    @Override
    public String getKey() {
        return userName + ":" + repoName;
    }
}
