/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.gtf.paperexample.verification;

import science.aist.gtf.verification.syntactic.constraint.Constraint;
import science.aist.gtf.verification.syntactic.constraint.ConstraintError;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>A constraint, that evaluates if there are dependencies with the same group_id + artifact_id but different
 * version.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class DependencyNodeArtifactQualifierConstraint implements Constraint<String> {

    public static final ConstraintError DUPLICATED_DEPENDENCY_CONSTRAINT_ERROR = new ConstraintError("Duplicated Dependency");

    private final Set<String> dependencies = new HashSet<>();

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public boolean isBreakingConstraint() {
        return false;
    }

    @Override
    public ConstraintError apply(String dependencyNode, Field field) {
        if (!dependencies.add(dependencyNode))
            return DUPLICATED_DEPENDENCY_CONSTRAINT_ERROR;
        return ConstraintError.NoError;
    }
}
