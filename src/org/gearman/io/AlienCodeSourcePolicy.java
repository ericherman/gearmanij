/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * I'd like to extend a super special thanks to Brett Neumeier for finding the
 * correct way to implement this very simple-seeming class.
 */
class AlienCodeSourcePolicy extends Policy {
    private Permissions allPriv;
    private Permissions noPriv;

    public AlienCodeSourcePolicy() {
        allPriv = new Permissions();
        allPriv.add(new AllPermission());
        noPriv = new Permissions();
    }

    public PermissionCollection getPermissions(CodeSource cs) {
        if (cs instanceof AlienCodeSource) {
            return noPriv;
        } else {
            return allPriv;
        }
    }

    public void refresh() { // required by api
    }
}
