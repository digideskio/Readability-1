import java.io._

/**
  * This program provides a measure for readability -- how good a reader
  * needs to be in order to understand a passage of English text. The measures
  * used are based on the average length of words, and the average length of 
  * sentences.
  *
  */
object Readability {
   
    /** Counts the number of words in a list of Strings */ 
    val getWordCount = (l: List[String]) => 
            (asSingleStr(l)) split " " length

    val getComplexWordCount = (l: List[String]) =>
            ((asSingleStr(l)) split " " toList) count (word => getSyllableCount(word) >= 3)

    /** Counts the number of letters in a a list of String */
    val getLetterCount = (l: List[String]) => 
            (asSingleStr(l)) filter (Character isLetter _) length
    
    /** Checks to see if ch is a vowel */
    val isVowel = (ch: Char) => 
            ("aeiouy" toList) exists (_ == ch)

	/** 
      * Starts the program
      * @param args Not used.
      */
    def main(args: Array[String]) {
        val file = IOHelper.openFile()
        
        if (file != null) {
            val lines = IOHelper.fetchText(file) map (_ trim)
            lines foreach println
            println("Total number of words: " + getWordCount(lines))
            println("Total number of letters: " + getLetterCount(lines))
            println("Total number of sentences: " + getSentenceCount(lines))
        } else
            ()
    }

    /**
      * Returns a list of String as a single String.
      * @param l The list of Strings
      * @return The single String
      */
    def asSingleStr(l: List[String]) = {
        val sb = new StringBuilder
        val listLength = l.length
        for (i <- 0 until listLength) {
            sb.append(l(i))
            sb.append(" ")
        }
        sb.toString.trim
    }

    /**
      * Gets the number of sentences in l.
      * @param l The list of Strings
      * @return The number of sentences
      */
    def getSentenceCount(l: List[String]): Int = {
        val lString = asSingleStr(l) 
        val strLength = lString.length
        var count = 0
                
        for (i <- 0 until strLength - 1) {
            if ((lString.charAt(i) == '.' && lString.charAt(i+1) == ' ') ||
                (lString.charAt(i) == '!' && lString.charAt(i+1) == ' ') ||
                (lString.charAt(i) == '?' && lString.charAt(i+1) == ' '))
                count = count + 1
            else if (i == strLength-2 && (lString.charAt(i+1) == '.' ||
                                          lString.charAt(i+1) == '!' ||
                                          lString.charAt(i+1) == '?'))
                count = count + 1
        } 
        count
    }

    /**
      * Gets the number of syllables in a string -- each vowel counts as one
      * syllable subject to the following rules:
      * -> ignore -es -ed, -e (except for -le)
      * -> words of three or fewer letters count as one syllable
      * -> consecutive vowels counts as one syllable (e.g. "delicious" has 3)
      * -> 'Y' at the beginning of a word doesn't count as a vowel.
      * @param word The word to count the syllables from.
      * @return The number of syllables in that word
      */
    def getSyllableCount(word: String): Int = {

        if (word.length <= 3) 
            return 1

        val wordMod = (word toLowerCase).toList
        val lastTwoLetters = (wordMod takeRight 2).mkString
        var totalNumberOfVowels = wordMod count isVowel 
       
        // Subtract 1 from count if word ends in -es, -ed, or -e but not -le 
        if (lastTwoLetters.equals("es") ||
            lastTwoLetters.equals("ed") ||
            (!lastTwoLetters.equals("le") &&
             !lastTwoLetters.equals("ee") &&
             (wordMod.last == 'e')))
            totalNumberOfVowels = totalNumberOfVowels - 1

        // Subtract 1 if word begins with y
        if (wordMod.head == 'y')
            totalNumberOfVowels = totalNumberOfVowels - 1  
    
        var consSyllCount = 0
        for (i <- 0 until wordMod.length-1) {
            if (isVowel(wordMod(i)) && isVowel(wordMod(i+1))) {
                if (i == 0)
                    totalNumberOfVowels = totalNumberOfVowels - 1
                else if (!isVowel(wordMod(i-1)))
                    totalNumberOfVowels = totalNumberOfVowels - 1
            }
        }
        totalNumberOfVowels
    } 
}

/** An object that handles IO.
  * @author Christopher Arriola
  * @version November 19, 2010
  */
object IOHelper {

    /**
      * Opens a file by using JFileChooser.
      * @return The opened file 
      */
    def openFile(): File = {
        import javax.swing._

        val chooser = new JFileChooser("Select sudoku problem to solve")
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
        chooser.showOpenDialog(null)
        chooser.getSelectedFile 
    }   
 
    /**
      * Will read text from just about anywhere, and will return it as a
      * list of Strings. (from Dr. Dave)
      * @param from The text to read from.
      * @return A list of strings. 
      */
    def fetchText(from: Any): List[String] = {
        import scala.io._
        val inStream: java.io.InputStream = from match {
            case stream: InputStream => stream
            case file: File => new FileInputStream(file)
            case fileName: String =>
                new FileInputStream(new File(fileName))
            case _ => null
        }

        val lines = Source.fromInputStream(inStream).getLines.toList
        inStream close()
        lines
    }
}
