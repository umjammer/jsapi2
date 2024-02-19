/**
 *
 */

package javax.speech.mock;

import javax.speech.EngineList;
import javax.speech.EngineMode;
import javax.speech.mock.recognition.MockRecognizerFactory;
import javax.speech.recognition.RecognizerMode;
import javax.speech.spi.EngineListFactory;


/**
 * EngineListFactory for test purposes.
 *
 * @author Dirk Schnelle
 */
public class MockRecognizerEngineListFactory implements EngineListFactory {
    @Override
    public EngineList createEngineList(EngineMode require)
            throws SecurityException {
        if ((require == null) || (require instanceof RecognizerMode)) {
            EngineMode[] modes = new EngineMode[] {new MockRecognizerFactory()};

            return new EngineList(modes);
        }

        return null;
    }
}
