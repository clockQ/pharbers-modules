package com.pharbers.sercuity

import java.util.Date

object Sercurity {
	
	def md5Hash(text: String) : String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
	def getTimeSpanWithMillSeconds : String = String.valueOf(Math.floor(new Date().getTime))
	def getTimeSpanWithSeconds : String = String.valueOf(Math.floor(new Date().getTime / 1000))
	def getTimeSpanWithMinutes : String = String.valueOf(Math.floor(new Date().getTime / (1000 * 60)))
	def getTimeSpanWith10Minutes : String = String.valueOf(Math.floor(new Date().getTime / (1000 * 60 * 10)))
	
	def getTimeSpanWithPast10Minutes : List[String] = {
		val m = new Date().getTime / (1000 * 60)
		(0 to 9).map { tmp =>
			String.valueOf(Math.floor(m - tmp))
		}.toList
	}
	
	def getTimeSpanWithPastMinutes(minutes: Int): List[String] = {
		 minutes - 1 match {
			case n if n < 0 => throw new Exception("time bound error")
			case n =>
				val m = new Date().getTime / (1000 * 60)
				(0 to n).map { tmp =>String.valueOf(Math.floor(m - tmp))}.toList
		}
	}
}