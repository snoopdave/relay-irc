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

   void onAction(String user, String chan, String txt);
   void onBan(String banned, String chan, String banner);
   void onClientInfo(String orgnick);
   void onClientSource(String orgnick);
   void onClientVersion(String orgnick);
   void onConnect() ;
   void onDisconnect();
   void onIsOn(String[] usersOn);
   void onInvite(String orgin, String orgnick, String invitee, String chan);
   void onJoin(String user, String nick, String chan, boolean create);
   void onJoins(String users, String chan);
   void onKick(String kicked, String chan, String kicker, String txt);
   void onMessage(String message);
   void onPrivateMessage(String orgnick, String chan, String txt);
   void onNick(String user, String oldnick, String newnick);
   void onNotice(String text);
   void onPart(String user, String nick, String chan);
   void onOp(String oper, String chan, String oped);
   void onParsingError(String message);
   void onPing(String params);
   void onStatus(String msg);
   void onTopic(String chanName, String newTopic);
   void onVersionNotice(String orgnick, String origin, String version);
   void onQuit(String user, String nick, String txt);

   void onReplyVersion(String version);
   void onReplyListUserChannels(int channelCount);
   void onReplyListStart();
   void onReplyList(String channel, int userCount, String topic);
   void onReplyListEnd();
   void onReplyListUserClient(String msg);
   void onReplyWhoIsUser(String nick, String user,
                         String name, String host);
   void onReplyWhoIsServer(String nick, String server, String info);
   void onReplyWhoIsOperator(String info);
   void onReplyWhoIsIdle(String nick, int idle, Date signon);
   void onReplyEndOfWhoIs(String nick);
   void onReplyWhoIsChannels(String nick, String channels);
   void onReplyMOTDStart();
   void onReplyMOTD(String msg);
   void onReplyMOTDEnd();
   void onReplyNameReply(String channel, String users);
   void onReplyTopic(String channel, String topic);

   void onErrorNoMOTD();
   void onErrorNeedMoreParams();
   void onErrorNoNicknameGiven();
   void onErrorNickNameInUse(String badNick);
   void onErrorNickCollision(String badNick);
   void onErrorErroneusNickname(String badNick);
   void onErrorAlreadyRegistered();
   void onErrorUnknown(String message);
   void onErrorUnsupported(String messag);
}

