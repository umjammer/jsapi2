/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 296 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.mac.synthesis;

import java.io.ByteArrayInputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.Voice;

import com.sun.jna.Pointer;
import org.jvoicexml.jsapi2.BaseAudioSegment;
import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.mac.MacNativeLibrary;
import org.jvoicexml.jsapi2.synthesis.BaseSynthesizer;

import static org.jvoicexml.jsapi2.mac.MacNativeLibrary.noErr;


/**
 * A SAPI compliant {@link Synthesizer}.
 *
 * @author Dirk Schnelle-Walka
 * @author Josua Arndt
 */
public final class MacSynthesizer extends BaseSynthesizer {

    /** Logger for this class. */
    private static final Logger logger = System.getLogger(MacSynthesizer.class.getName());

    static {
        System.loadLibrary("Jsapi2MacBridge");
    }

    /** SAPI synthesizer handle. */
    private long synthesizerHandle;

    /**
     * Constructs a new synthesizer object.
     *
     * @param mode the synthesizer mode
     */
    MacSynthesizer(MacSynthesizerMode mode) {
        super(mode);
    }

    @Override
    protected void handleAllocate() throws EngineStateException, EngineException, AudioException, SecurityException {
        Voice voice;
        MacSynthesizerMode mode = (MacSynthesizerMode) getEngineMode();
        if (mode == null) {
            voice = null;
        } else {
            Voice[] voices = mode.getVoices();
            if (voices == null) {
                voice = null;
            } else {
                voice = voices[0];
            }
        }
        synthesizerHandle = macHandleAllocate(voice);
    }

    /**
     * Allocates a Mac synthesizer.
     *
     * @param voice the voice to use
     * @return synthesizer handle
     */
    private long macHandleAllocate(Voice voice) {
//  logger.log(Level.DEBUG, "macHandleAllocate");

        // null will cause GetVoiceDescription to return the system default
        Pointer /* VoiceSpec */ voiceSpec;
        int /* OSErr */ theErr = noErr;

        if (voice != null) {
            // determine name of voice
            String name = voice.getName();

            // Carbon specific part
            if (name != null) {
                byte[] voiceName = name.getBytes();

//logger.log(Level.DEBUG, "Given Voice: " << voiceName);

                short numOfVoices;
                theErr = CountVoices(numOfVoices);

                int voiceIndex;
                for (voiceIndex = 1; voiceIndex <= numOfVoices; voiceIndex++) {
                    VoiceDescription tmpVDesc;
                    Pointer /* VoiceSpec */	tmpVSpec = new Pointer(0);

                    theErr = MacNativeLibrary.INSTANCE.GetIndVoice((short) voiceIndex, tmpVSpec);
                    theErr = GetVoiceDescription(tmpVSpec, tmpVDesc, sizeof(tmpVDesc));

                    byte[] currName = new byte[64];
                    snprintf(currName, 64, "%s", (tmpVDesc.name[1]));
//logger.log(Level.DEBUG, "Found voice " << currName);

                    if (strcmp(currName, voiceName) == 0) {
//logger.log(Level.DEBUG, "Set " << currName);
                        voiceSpec = tmpVSpec;
                    }
                }
            }
        }

        // create a new speech channel
        SpeechChannel chan = (SpeechChannel)malloc(sizeof(SpeechChannel));
        theErr = NewSpeechChannel(voiceSpec, chan);
        return (long) chan;
    }

    @Override
    public boolean handleCancel() {
        return macHandleCancel(synthesizerHandle);
    }

    /**
     * Cancels the current output.
     *
     * @param handle the synthesizer handle
     * @return <code>true</code> if the current output has been canceled
     */
    private boolean macHandleCancel(long handle) {
//logger.log(Level.DEBUG, "macHandleCancel");
        return false;
    }

    @Override
    protected boolean handleCancel(int id) {
        return macHandleCancel(synthesizerHandle, id);
    }

    /**
     * Cancels the output with the given id.
     *
     * @param handle the synthesizer handle
     * @param id     the item to cancel
     * @return <code>true</code> if the output with the given id has been
     * canceled
     */
    private boolean macHandleCancel(long handle, int id) {
//logger.log(Level.DEBUG, "macHandleCancel");
        return false;
    }

    @Override
    protected boolean handleCancelAll() {
        return macHandleCancelAll(synthesizerHandle);
    }

    /**
     * Cancels all outputs.
     *
     * @param handle the synthesizer handle
     * @return <code>true</code> if at least one output has been canceled
     */
    private boolean macHandleCancelAll(long handle) {
//logger.log(Level.DEBUG, "macHandleCancelAll");
        return false;
    }

    @Override
    public void handleDeallocate() {
        // Leave some time to let all resources detach
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return;
        }
        macHandlDeallocate(synthesizerHandle);
        synthesizerHandle = 0;
    }

    /**
     * Deallocates the SAPI synthesizer.
     *
     * @param handle the synthesizer handle
     */
    private void macHandlDeallocate(long handle) {
//logger.log(Level.DEBUG, "macHandlDeallocate");
    }

    @Override
    public void handlePause() {
        macHandlePause(synthesizerHandle);
    }

    /**
     * Pauses the synthesizer.
     *
     * @param handle the synthesizer handle
     *               the synthesizer handle
     */
    private void macHandlePause(long handle) {
//logger.log(Level.DEBUG, "macHandlePause");
    }

    @Override
    public boolean handleResume() {
        return macHandlResume(synthesizerHandle);
    }

    /**
     * Resumes the synthesizer.
     *
     * @param handle the synthesizer handle
     *               the synthesizer handle
     * @return <code>true</code> if the synthesizer is resumed
     */
    private boolean macHandlResume(long handle) {
//  logger.log(Level.DEBUG, "macHandlResume");
        return false;
    }

    @Override
    public AudioSegment handleSpeak(int id, String item) {
        byte[] bytes = macHandleSpeak(synthesizerHandle, id, item);
        AudioManager manager = getAudioManager();
        String locator = manager.getMediaLocator();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        AudioSegment segment;
        if (locator == null) {
            segment = new BaseAudioSegment(item, in);
        } else {
            segment = new BaseAudioSegment(locator, item, in);
        }
        return segment;
    }

    /**
     * Speaks the given item.
     *
     * @param handle synthesizer handle
     * @param id     id of the item
     * @param item   the item to speak
     * @return byte array of the synthesized speech
     */
    private byte[] macHandleSpeak(long handle, int id, String item) {
//logger.log(Level.DEBUG, "macHandleSpeak");

        SpeechChannel chan = (SpeechChannel) handle;
        int /* OSErr */ ok = noErr;
        int /* OSStatus */ stat;

String file = std::tmpnam(null);
        byte[] textBuf = GetStringNativeChars(env, item);
        long size = strlen(textBuf);

        CFURLRef url = CFURLCreateWithFileSystemPath(
                kCFAllocatorDefault,
                CFStringCreateWithCString(null, file, null),
                kCFURLPOSIXPathStyle,
                false ); //false == not a directory

        ok = SetSpeechInfo(chan, soOutputToFileWithCFURL, url);
        if (ok != noErr) {
            logger.log(Level.DEBUG, "SetSpeechInfo failed: " + ok);
        }

        // Write to dedicated audio file with conversion - not needed for now

//  AudioStreamBasicDescription asbd = AudioStreamBasicDescription();
//  bzero(&asbd, sizeof(asbd));
//  asbd.mSampleRate = 22050.0;
//  asbd.mFormatID = kAudioFormatLinearPCM;
//  asbd.mBytesPerPacket = 2;
//  asbd.mFramesPerPacket = 1;
//  asbd.mBytesPerFrame = 2;
//  asbd.mChannelsPerFrame = 1;
//  asbd.mBitsPerChannel = 16;
//  asbd.mFormatFlags = kAudioFormatFlagsAreAllClear;
//  asbd.mFormatFlags |= kAudioFormatFlagIsSignedInteger;
//  asbd.mFormatFlags |= kAudioFormatFlagIsBigEndian;
//
//  AudioChannelLayout acl = AudioChannelLayout();
//  bzero(&acl, sizeof(acl));
//
//  ExtAudioFileRef af;
//  stat = ExtAudioFileCreateWithURL (url, kAudioFileAIFFType, &asbd, &acl, kAudioFileFlags_EraseFile, &af);
//  if (stat != noErr) {
//    logger.log(Level.DEBUG, "ExtAudioFileCreateWithURL not ok! " << stat);
//  }
//
//  ok = SetSpeechInfo(*chan, soOutputToExtAudioFile, af);
//  if (ok != noErr) {
//    logger.log(Level.DEBUG, "SetSpeechInfo not ok! " << ok);
//  }

//  logger.log(Level.DEBUG, "filename " << file);

        // speak text into file
        ok = SpeakText((chan), textBuf, strlen(textBuf));
        if (ok != noErr) {
            logger.log(Level.DEBUG, "SpeakText failed: " + ok);
        }

        // wait for speech to finish!
        while (SpeechBusy()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // close the audio file
//  ExtAudioFileDispose(af);

        AudioFileID afId;
        int /* OSStatus */ __err = AudioFileOpenURL(url, kAudioFileReadPermission, kAudioFileAIFFType, afId);
        if (__err != 0)
            throw new IllegalStateException("can't open file");

        int propertySize;

//  AudioStreamBasicDescription srcFmt;
//  UInt32 propertySize = sizeof(srcFmt);
//  stat = AudioFileGetProperty(afId, kAudioFilePropertyDataFormat, &propertySize, &srcFmt);
//
//  logger.log(Level.DEBUG, "kAudioFilePropertyDataFormat: \n"
//    + "\tmFormatID " + (char*)&srcFmt.mFormatID + "\n"
//    + "\tmFormatFlags " + srcFmt.mFormatFlags + "\n"
//    + "\tmSampleRate " + srcFmt.mSampleRate + "\n"
//    + "\tmBitsPerChannel " + srcFmt.mBitsPerChannel + "\n"
//    + "\tmChannelsPerFrame " + srcFmt.mChannelsPerFrame + "\n"
//    + "\tmBytesPerPacket " + srcFmt.mBytesPerPacket + "\n"
//    + "\tmFramesPerPacket " + srcFmt.mFramesPerPacket + "\n"
//    + "\tmBytesPerFrame " + srcFmt.mBytesPerFrame + "\n"
//   );

        long numBytes;
        propertySize = sizeof(numBytes);
        /* OSStatus */ __err = AudioFileGetProperty(afId, kAudioFilePropertyAudioDataByteCount, propertySize, numBytes);
        if (__err != 0)
            throw new IllegalStateException("get byte count of audio file failed");

//logger.log(Level.DEBUG, "kAudioFilePropertyDataFormat: \n" + "\tnumBytes " + numBytes + "\n");

        long numPackets;
        propertySize = sizeof(numPackets);
        /* OSStatus */ __err = AudioFileGetProperty(afId, kAudioFilePropertyAudioDataPacketCount, propertySize, numPackets)
        if (__err != 0)
            throw new IllegalStateException("get packet count of audio file failed");

//logger.log(Level.DEBUG, "kAudioFilePropertyAudioDataPacketCount: \n" + "\tnumPackets " + numPackets + "\n");

        int numBytes32 = (int) numBytes;
        int numPackets32 = (int) numPackets;
        byte[] buffer = new byte[(int) numBytes];

        /* OSStatus */ __err = AudioFileReadPackets(afId, false, numBytes32, null, 0, numPackets32, buffer);
        if (__err != 0)
            throw new IllegalStateException("reading packets from audio file failed.");

//logger.log(Level.DEBUG, "AudioFileReadPackets: \n" + "\tnumPackets " + numPackets32 + "\n" + "\tnumBytes " + numBytes32 << "\n");

        // tidy up
        CFRelease(url);
        AudioFileClose(afId);
        remove(file);

        // byte swap
        int[] buf = (int[])buffer;
        int cnt = 0;
        while (cnt < (numBytes32 / 2)) {
            buf[cnt] = ((buf[cnt] & 0xff) << 8) | ((buf[cnt] & 0xff00) >> 8);
            cnt++;
        }

        byte[] jb = new byte[numBytes32];
        Arrays.fill(jb, 0, numBytes32, (byte) buffer[bufferP]);
//  logger.log(Level.DEBUG, "Returning buffer of size " << numBytes32);

        return jb;
    }

    @Override
    protected AudioSegment handleSpeak(int id, Speakable item) {
        throw new IllegalArgumentException("Synthesizer does not support" + " speech markup!");
    }

    /**
     * Speaks the given item.
     *
     * @param handle synthesizer handle
     * @param id     id of the item
     * @param ssml   the SSML markup to speak
     * @return byte array of the synthesized speech
     */
    private byte[] macHandleSpeakSsml(long handle, int id, String ssml) {
logger.log(Level.DEBUG, "macHandleSpeakSsml");
        return null;
    }

    @Override
    protected AudioFormat getEngineAudioFormat() {
        return macGetAudioFormat(synthesizerHandle);
    }

    /**
     * retrieves the default audio format.
     *
     * @param handle synthesizer handle.
     * @return native audio format
     */
    private AudioFormat macGetAudioFormat(long handle) {
//  logger.log(Level.DEBUG, "macGetAudioFormat");
        //return env.NewObject(clazz, method, format.nSamplesPerSec,
        //    format.wBitsPerSample, format.nChannels, JNI_TRUE, JNI_TRUE);
        return new AudioFormat(22050.0f, 16, 1, true, false);
    }

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties,
            String propName, Object oldValue,
            Object newValue) {
        properties.commitPropertyChange(propName, oldValue, newValue);
//        logger.warning("changing property '" + propName + "' to '" + newValue + "' ignored");
    }
}

