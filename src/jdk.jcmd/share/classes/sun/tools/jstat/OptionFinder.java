/*
 * Copyright (c) 2004, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.tools.jstat;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * A class for finding a specific special option in the jstat_options file.
 *
 * @author Brian Doherty
 * @since 1.5
 */
public class OptionFinder {

    private static final boolean debug = false;

    final List<String> optionsSources;


    public OptionFinder(List<String> optionsSources) {
        this.optionsSources = Collections.unmodifiableList(optionsSources);
    }

    public OptionFormat getOptionFormat(String option, boolean useTimestamp) {
        OptionFormat of = getOptionFormat(option);
        OptionFormat tof = null;
        if ((of != null) && (useTimestamp)) {
            // prepend the timestamp column as first column
            tof = getOptionFormat("timestamp");
            if (tof != null) {
                ColumnFormat cf = (ColumnFormat)tof.getSubFormat(0);
                of.insertSubFormat(0, cf);
            }
        }
        return of;
    }

    protected OptionFormat getOptionFormat(String option) {
        for (Parser parser : getOptionParsers()) {
            try {
                OptionFormat of = parser.parse(option);
                if (of != null)
                    return of;
            } catch (IOException | ParserException e) {
                if (debug) e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns a list of {@linkplain Parser} instances created out of the
     * provided {@linkplain InputStream}s.
     * <p>
     * ! Parsers can not be restarted !
     * Therefore we need to create new instances to parse each option.
     * </p>
     * @return list of {@linkplain Parser} instances created out of the
     * provided {@linkplain InputStream}s
     */
    private List<Parser> getOptionParsers() {
        return OptionParserFactory.getOptionParsers(optionsSources);
    }
}
