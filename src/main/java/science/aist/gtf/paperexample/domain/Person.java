/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.gtf.paperexample.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Domain class which represent a person</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class Person {
    private String svnr;
    private String firstname;
    private String lastname;
}
