//----------------------------------------------------------------------------
// $RCSfile: IRCConnectionListener.java,v $
// $Revision: 1.1.2.4 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//----------------------------------------------------------------------------

package org.relayirc.core;
import org.relayirc.util.*;
import java.util.Date;

/** 
 * <p>Implement this interface to listen to an IRCConnection.</p>
 * <p>ISSUE: To follow the Java Bean conventions, every method
 * should take one argument of type IRCConnectionEvent. Does this
 * buy us anything?</p>
 * @see IRCConnection
 * @author David M. Johnson
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code:     Relay IRC Chat Server<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s):    No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public interface IRCConnectionListener {

   public void onAction( String user, String chan, String txt );
   public void onBan( String banned, String chan, String banner );
   public void onClientInfo(String orgnick);
   public void onClientSource(String orgnick);
   public void onClientVersion(String orgnick);
   public void onConnect() ;
   public void onDisconnect();
   public void onIsOn( String[] usersOn );
   public void onInvite(String orgin,String orgnick,String invitee,String chan);
   public void onJoin( String user, String nick, String chan, boolean create );
   public void onJoins( String users, String chan);
   public void onKick( String kicked, String chan, String kicker, String txt );
   public void onMessage(String message);
   public void onPrivateMessage(String orgnick, String chan, String txt);
   public void onNick( String user, String oldnick, String newnick );
   public void onNotice(String text);
   public void onPart( String user, String nick, String chan );
   public void onOp( String oper, String chan, String oped );
   public void onParsingError(String message);
   public void onPing(String params);
   public void onStatus(String msg);
   public void onTopic(String chanName, String newTopic);
   public void onVersionNotice(String orgnick, String origin, String version);
   public void onQuit( String user, String nick, String txt );

   public void onReplyVersion(String version);
   public void onReplyListUserChannels(int channelCount);
   public void onReplyListStart();
   public void onReplyList(String channel, int userCount, String topic);
   public void onReplyListEnd();
   public void onReplyListUserClient(String msg);
   public void onReplyWhoIsUser(String nick, String user, 
                                String name, String host);
   public void onReplyWhoIsServer(String nick, String server, String info);
   public void onReplyWhoIsOperator(String info);
   public void onReplyWhoIsIdle(String nick, int idle, Date signon);
   public void onReplyEndOfWhoIs(String nick);
   public void onReplyWhoIsChannels(String nick, String channels);
   public void onReplyMOTDStart();
   public void onReplyMOTD(String msg);
   public void onReplyMOTDEnd();
   public void onReplyNameReply(String channel, String users);
   public void onReplyTopic(String channel, String topic);

   public void onErrorNoMOTD();
   public void onErrorNeedMoreParams();
   public void onErrorNoNicknameGiven();
   public void onErrorNickNameInUse(String badNick);
   public void onErrorNickCollision(String badNick);
   public void onErrorErroneusNickname(String badNick);
   public void onErrorAlreadyRegistered();
   public void onErrorUnknown(String message);
   public void onErrorUnsupported(String messag);
}

