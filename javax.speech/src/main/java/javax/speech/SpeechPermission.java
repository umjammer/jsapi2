package javax.speech;

import java.security.Permission;
import java.util.Map;


/**
 * Implementation of a speech permission to perform security checks.
 *
 * @author Dirk Schnelle-Walka
 */
public final class SpeechPermission extends Permission {

    /** The serial version UID. */
    private static final long serialVersionUID = 2261631202702374405L;

    /** Mapping of permissions to actions. */
    private static final Map<String, String> ACTIONS4PERMISSIONS;

    static {
        ACTIONS4PERMISSIONS = new java.util.HashMap<>();
        ACTIONS4PERMISSIONS.put("javax.speech.AudioSegment.openInputStream", "AudioSegment.openInputStream");
        ACTIONS4PERMISSIONS.put("javax.speech.AudioManager.control", "AudioManager.control");
        ACTIONS4PERMISSIONS.put("javax.speech.recognition.Recognizer.allocate", "Recognizer.allocate");
        ACTIONS4PERMISSIONS.put("javax.speech.EngineManager.registerEngineListFactory",
                "EngineManager.registerEngineListFactory");
        ACTIONS4PERMISSIONS.put("javax.speech.recognition.SpeakerManager", "SpeakerManager");
        ACTIONS4PERMISSIONS.put("javax.speech.recognition.SpeakerProfile", "SpeakerProfile");
        ACTIONS4PERMISSIONS.put("javax.speech.recognition.FinalResult.tokenCorrection",
                "FinalResult.tokenCorrection");
        ACTIONS4PERMISSIONS.put("javax.speech.VocabularyManager.update", "VocabularyManager.update");
    }

    /**
     * Constructs a new object.
     *
     * @param name name of the permission
     */
    public SpeechPermission(String name) {
        super(name);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SpeechPermission)) {
            return false;
        }
        SpeechPermission permission = (SpeechPermission) object;
        String name = getName();
        String otherName = permission.getName();
        return name.equals(otherName);
    }

    @Override
    public String getActions() {
        String name = getName();
        return ACTIONS4PERMISSIONS.get(name);
    }

    @Override
    public int hashCode() {
        String name = getName();
        return name.hashCode();
    }

    @Override
    public boolean implies(Permission permission) {
        return false;
    }
}
