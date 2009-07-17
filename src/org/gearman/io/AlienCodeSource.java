/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.security.CodeSource;

class AlienCodeSource extends CodeSource {
    private static final long serialVersionUID = 1L;

    public AlienCodeSource() {
        super(null, (java.security.cert.Certificate[]) null);
    }
}
