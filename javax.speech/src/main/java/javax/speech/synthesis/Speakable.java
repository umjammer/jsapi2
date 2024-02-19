/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 65 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.speech.synthesis;

// Comp 2.0.6

/**
 * An object implementing the Speakable interface can be provided to the
 * speak method of a Synthesizer to be spoken.
 * <p>
 * The text is accessed through
 * the getMarkupText method making it the spoken equivalent of the toString
 * method of a Java object.
 * <p>
 * Applications can extend Java objects to implement the
 * Speakable interface.
 * Examples might include graphical objects or database entries.
 * <p>
 * The getMarkupText method returns markup text formatted for the
 * Speech Synthesis Markup Language defined in the
 * <p>
 * <A href="http://www.w3.org/TR/speech-synthesis/">
 *  Speech Synthesis Markup Language Specification</A>
 * .
 * SSML allows structural information
 * (paragraphs and sentences), production information
 * (pronunciations, emphasis, breaks, and prosody), and other
 * miscellaneous markup. Appropriate use of this markup can improve the
 * quality and understandability of the synthesized speech.
 * <p>
 * A SpeakableListener can be attached to the Synthesizer with the
 * addSpeakableListener method to receive all SpeakableEvents for all
 * Speakable objects on the output queue.
 * @see javax.speech.synthesis.SpeakableListener
 * @see javax.speech.synthesis.SpeakableEvent
 * @see javax.speech.synthesis.Synthesizer
 * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#removeSpeakableListener(javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#speak(javax.speech.synthesis.Speakable, javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#speak(java.lang.String, javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#speakMarkup(java.lang.String, javax.speech.synthesis.SpeakableListener)
 */
public interface Speakable {

    /**
     * Returns text to be spoken formatted in the synthesis markup language.
     * <p>
     * This method is called immediately when a Speakable object is passed
     * to the speak method of a Synthesizer.
     * <p>
     * The markup text is a Unicode string and is assumed to contain text
     * of a single language (the language of the Synthesizer). The text is
     * treated as independent of other text output on the synthesizer's text
     * output queue, so, a sentence or other important structure should be
     * contained within a single speakable object.
     * <p>
     * The standard XML header is optional for software-created markup documents.
     * Thus, the getMarkupText method is not required to provide the header.
     * @return a string containing synthesis markup language
     * @see javax.speech.synthesis.Synthesizer#speak(javax.speech.synthesis.Speakable, javax.speech.synthesis.SpeakableListener)
     * @see javax.speech.synthesis.Synthesizer
     */
    String getMarkupText();
}
