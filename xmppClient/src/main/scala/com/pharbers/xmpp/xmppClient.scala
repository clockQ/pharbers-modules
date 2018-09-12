package com.pharbers.xmpp

import com.pharbers.baseModules.PharbersInjectModule
import org.jivesoftware.smack._
import org.jivesoftware.smack.packet.Message
import akka.actor.{ActorSystem, Props}
import akka.routing.RoundRobinPool

case class xmppClient(context : ActorSystem) extends PharbersInjectModule {

    override val id: String = "xmpp-module"
    override val configPath: String = "pharbers_config/xmpp_cmanager.xml"
    override val md = "xmpp-host" :: "xmpp-port" :: "xmpp-user" ::
        "xmpp-pwd" :: "xmpp-listens" :: "xmpp-report" :: Nil

    val xmpp_host = config.mc.find(p => p._1 == "xmpp-host").get._2.toString
    val xmpp_port = config.mc.find(p => p._1 == "xmpp-port").get._2.toString.toInt
    val xmpp_user = config.mc.find(p => p._1 == "xmpp-user").get._2.toString
    val xmpp_pwd = config.mc.find(p => p._1 == "xmpp-pwd").get._2.toString

    val xmpp_listens = config.mc.find(p => p._1 == "xmpp-listens").get._2.toString.split("#")
    val xmpp_report = config.mc.find(p => p._1 == "xmpp-report").get._2.toString.split("#")

    lazy val xmpp_config : ConnectionConfiguration = new ConnectionConfiguration(xmpp_host, xmpp_port)
    lazy val conn = {
        val conn = new XMPPConnection(xmpp_config)
        try {
            conn.connect()
        } catch {
            case ex: XMPPException => ex.printStackTrace()
        }
        conn
    }

    def startXmpp(decode : String => Any) = {
        conn.login(xmpp_user, xmpp_pwd)

        lazy val xmpp_receiver = context.actorOf(Props[xmppReceiver].withRouter(RoundRobinPool(5)))
        val cm = conn.getChatManager
        xmpp_listens.foreach { userJID =>
            cm.createChat(userJID, new MessageListener {
                override def processMessage(chat: Chat, message: Message): Unit = {
                    println("receiving message:" + message.getBody)
                    xmpp_receiver ! (userJID, message.getBody)
                }
            })
        }
    }

    def broadcastXmppMsg(encode : Any => String)(item : Any) = {
        val cm= conn.getChatManager

        xmpp_report.foreach { userJID =>
            cm.createChat(userJID, null).sendMessage(encode(item))
        }
    }

    def stopXmpp = {
        conn.disconnect()
    }
}
