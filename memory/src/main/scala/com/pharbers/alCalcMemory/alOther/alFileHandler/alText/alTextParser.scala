package com.pharbers.alCalcMemory.alOther.alFileHandler.alText

import com.pharbers.alCalcMemory.alOther.alFileHandler.alFileHandlers
import com.pharbers.alCalcMemory.alOther.alFileHandler.alFilesOpt.alFileOpt


/**
  * Created by Alfred on 09/03/2017.
  */
object alTextParser {
    def apply(path : String) : List[Any] = {
        val p = new alTextParser
        p.prase(path)("0")
        p.data.toList
    }
}

class alTextParser extends alFileHandlers with CreateInnerParser {
    val parser = CreateInnerParser

    override def prase(path : String)(x : Any) : Any = {
        parser.startParse(path)
        this
    }
}

case class inner_parser(h : alFileHandlers) {
    def startParse(file : String) = h.data.appendAll(alFileOpt(file).requestDataFromFile(x => x))
}

trait CreateInnerParser { this : alFileHandlers =>
    def CreateInnerParser : inner_parser = new inner_parser(this)
}