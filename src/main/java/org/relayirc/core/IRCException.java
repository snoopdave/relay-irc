//-----------------------------------------------------------------------------
// $RCSfile: IRCException.java,v $
// $Revision: 1.1.2.1 $
// $Author $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.core;

//////////////////////////////////////////////////////////////////////////////

/**
 * Exception that caused disconnection from IRC server.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code: Relay-JFC Chat Client <br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class IRCException extends Exception {

    public IRCException(String message) {
        super(message);
    }
}


