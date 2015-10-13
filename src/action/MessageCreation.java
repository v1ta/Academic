package action;


import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Joseph on 10/11/15.
 */
public interface MessageCreation {
    /**
     * Added a payload to an appropriate message type
     * @param out
     * @throws IOException
     */
    void addPayload(DataOutputStream out) throws IOException;
}
